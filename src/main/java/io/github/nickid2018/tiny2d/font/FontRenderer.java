package io.github.nickid2018.tiny2d.font;

import io.github.nickid2018.tiny2d.buffer.IndexBufferProvider;
import io.github.nickid2018.tiny2d.buffer.VertexArray;
import io.github.nickid2018.tiny2d.buffer.VertexArrayBuilder;
import io.github.nickid2018.tiny2d.buffer.VertexAttributeList;
import io.github.nickid2018.tiny2d.shader.ShaderProgram;
import io.github.nickid2018.tiny2d.texture.StaticTexture;
import io.github.nickid2018.tiny2d.window.Window;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class FontRenderer {

    private final Window window;
    private final VectorFont font;

    public FontRenderer(Window window, VectorFont font) {
        this.window = window;
        this.font = font;
    }

    public int getTextWidth(String text, int size) {
        int width = 0;
        int[] codePoints = text.codePoints().toArray();
        for (int i = 0; i < codePoints.length - 1; i++)
            width += font.getKerning(size, codePoints[i], codePoints[i + 1]);
        width += text.codePoints().map(codepoint -> (int) font.getCharLength(size, codepoint)).sum();
        return width;
    }

    public VectorFont getFont() {
        return font;
    }

    public List<Pair<StaticTexture, VertexArray>> createTextArrayDataMerged(String text, float x, float y, int size) {
        return createTextArrayDataMerged(text, x, y, size, 0, 0, 0);
    }

    public List<Pair<StaticTexture, VertexArray>> createTextArrayDataMerged(
            String text, float x, float y, int size, float r, float g, float b) {
        List<FontVertexInfos> infos = text.codePoints().mapToObj(c -> font.getCodepointInfo(size, c)).toList();
        Map<StaticTexture, VertexArrayBuilder> storedArrays = new HashMap<>();
        float xNow = x;
        for (int i = 0; i < infos.size(); i++) {
            FontVertexInfos info = infos.get(i);
            FontAtlas atlas = info.atlas;
            if (atlas != null) {
                float x1 = xNow + info.leftBearing;
                float y1 = y + info.topSide;
                float x2 = x1 + info.width;
                float y2 = y1 + info.height;
                float ndcX1 = window.toNDCX(x1);
                float ndcY1 = window.toNDCY(y1);
                float ndcX2 = window.toNDCX(x2);
                float ndcY2 = window.toNDCY(y2);
                if (!storedArrays.containsKey(atlas.getTexture()))
                    storedArrays.put(atlas.getTexture(), new VertexArrayBuilder(
                            VertexAttributeList.COLOR_TEXTURE_2D, IndexBufferProvider.DEFAULT));
                VertexArrayBuilder builder = storedArrays.get(atlas.getTexture());
                builder.pos(ndcX1, ndcY1).color(r, g, b).uv(info.minU, info.minV).end();
                builder.pos(ndcX2, ndcY1).color(r, g, b).uv(info.maxU, info.minV).end();
                builder.pos(ndcX1, ndcY2).color(r, g, b).uv(info.minU, info.maxV).end();
                builder.pos(ndcX2, ndcY2).color(r, g, b).uv(info.maxU, info.maxV).end();
                builder.makeIndex();
            }
            xNow += info.advanceWidth;
            if (i < infos.size() - 1) {
                FontVertexInfos nextInfo = infos.get(i + 1);
                xNow += font.getKerning(size, info.codepoint, nextInfo.codepoint);
            }
        }

        List<Pair<StaticTexture, VertexArray>> result = new ArrayList<>();
        for (Map.Entry<StaticTexture, VertexArrayBuilder> entry : storedArrays.entrySet())
            result.add(new ObjectObjectImmutablePair<>(entry.getKey(), entry.getValue().build()));
        return result;
    }

    public static void drawTextArrayData(List<Pair<StaticTexture, VertexArray>> storedArrays) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        ShaderProgram.getDefaultShader("font").use();
        storedArrays.forEach(pair -> {
            pair.left().bind();
            pair.right().draw();
        });
        glDisable(GL_BLEND);
    }

    public static void destroyTextArrayData(List<Pair<StaticTexture, VertexArray>> storedArrays) {
        storedArrays.forEach(pair -> pair.right().destroy());
    }

    public void drawStringImmediately(String text, float x, float y, int size) {
        drawStringImmediately(text, x, y, size, 0, 0, 0);
    }

    public void drawStringImmediately(String text, float x, float y, int size, float r, float g, float b) {
        List<Pair<StaticTexture, VertexArray>> storedArrays = createTextArrayDataMerged(text, x, y, size, r, g, b);
        drawTextArrayData(storedArrays);
        destroyTextArrayData(storedArrays);
    }
}
