package io.github.nickid2018.tiny2d.font;

import io.github.nickid2018.tiny2d.buffer.IndexBufferProvider;
import io.github.nickid2018.tiny2d.buffer.VertexArrayBuilder;
import io.github.nickid2018.tiny2d.buffer.VertexAttributeList;
import io.github.nickid2018.tiny2d.shader.ShaderProgram;
import io.github.nickid2018.tiny2d.shader.ShaderSource;
import io.github.nickid2018.tiny2d.shader.ShaderType;
import io.github.nickid2018.tiny2d.util.LazyLoadValue;
import io.github.nickid2018.tiny2d.window.Window;

import java.io.IOException;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class FontRenderer {

    private final Window window;
    private final VectorFont font;

    private static final LazyLoadValue<ShaderProgram> TEXT_SHADER = new LazyLoadValue<>(() -> {
        ShaderProgram program = new ShaderProgram();
        try {
            program.attachShader(ShaderSource.createShader(ShaderType.VERTEX, "/assets/shader/font.vsh"));
            program.attachShader(ShaderSource.createShader(ShaderType.FRAGMENT, "/assets/shader/font.fsh"));
            program.link();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return program;
    });

    public FontRenderer(Window window, VectorFont font) {
        this.window = window;
        this.font = font;
    }

    public VectorFont getFont() {
        return font;
    }

    public void drawString(String text, int x, int y, int size) {
        drawString(text, x, y, size, 0, 0, 0);
    }

    public void drawString(String text, int x, int y, int size, float r, float g, float b) {
        List<FontVertexInfos> infos = text.codePoints().mapToObj(c -> font.getCodepointInfo(size, c)).toList();
        float xNow = x;
        for (int i = 0; i < infos.size(); i++) {
            FontVertexInfos info = infos.get(i);
            FontAtlas atlas = info.atlas;
            if (atlas != null) {
                float x1 = xNow + info.leftBearing;
                float y1 = (float) y + info.topSide;
                float x2 = x1 + info.width;
                float y2 = y1 + info.height;
                float ndcX1 = window.toNDCX(x1);
                float ndcY1 = window.toNDCY(y1);
                float ndcX2 = window.toNDCX(x2);
                float ndcY2 = window.toNDCY(y2);
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                atlas.getTexture().bind();
                VertexArrayBuilder builder = new VertexArrayBuilder(VertexAttributeList.COLOR_TEXTURE_2D, IndexBufferProvider.DEFAULT);
                builder.pos(ndcX1, ndcY1).color(r, g, b).uv(info.minU, info.minV).end();
                builder.pos(ndcX2, ndcY1).color(r, g, b).uv(info.maxU, info.minV).end();
                builder.pos(ndcX1, ndcY2).color(r, g, b).uv(info.minU, info.maxV).end();
                builder.pos(ndcX2, ndcY2).color(r, g, b).uv(info.maxU, info.maxV).end();
                TEXT_SHADER.get().use();
                builder.build().draw();
                glDisable(GL_BLEND);
            }
            xNow += info.advanceWidth;
            if (i < infos.size() - 1) {
                FontVertexInfos nextInfo = infos.get(i + 1);
                xNow += font.getKerning(size, info.codepoint, nextInfo.codepoint);
            }
        }
    }
}
