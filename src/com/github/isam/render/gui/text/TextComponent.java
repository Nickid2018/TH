/*
 * Copyright 2021 ISAM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.isam.render.gui.text;

import com.github.isam.crash.CrashReport;
import com.github.isam.crash.CrashReportSession;
import com.github.isam.crash.DetectedCrashException;
import com.github.isam.phys.AABB;
import com.github.isam.render.Component;
import com.github.isam.render.Renderer;
import com.github.isam.render.font.FontAtlas;
import com.github.isam.render.font.FontVertexInfos;
import com.github.isam.render.font.VectorFont;
import com.github.isam.render.shader.Shaders;
import com.github.isam.render.texture.StaticTexture;
import com.github.isam.render.vertex.ElementBuffer;
import com.github.isam.render.vertex.VertexArray;
import com.github.isam.render.vertex.VertexBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public abstract class TextComponent extends Component {

    protected String text;
    protected final VectorFont font;
    protected final List<VertexArray> arrayRender = new ArrayList<>();
    protected final List<StaticTexture> fonts = new ArrayList<>();
    protected boolean dirty;
    protected int color;

    /* Color: XXRRGGBB */
    public TextComponent(Renderer renderer, AABB position, VectorFont font, String text, int color) {
        super(renderer, position);
        this.font = font;
        this.color = color;
        setText(text);
    }

    public TextComponent(Renderer renderer, AABB position, VectorFont font, String text) {
        this(renderer, position, font, text, 0x00FFFFFF);
    }

    public TextComponent(Renderer renderer, AABB position, VectorFont font) {
        this(renderer, position, font, "");
    }

    public String getText() {
        return text;
    }

    public TextComponent setText(String text) {
        this.text = text;
        dirty = true;
        return this;
    }

    public int getColor() {
        return color;
    }

    public TextComponent setColor(int color) {
        this.color = color & 0x00FFFFFF;
        dirty = true;
        return this;
    }

    @Override
    public void render() {
        if (dirty) {
            dirty = false;
            try {
                prepareText();
            } catch (Exception e) {
                CrashReport report = new CrashReport("Text GUI prepares text", e);
                CrashReportSession session = new CrashReportSession("Text GUI Information");
                session.addDetail("Text Component", this);
                session.addDetail("Text", text);
                report.addSession(session);
                throw new DetectedCrashException(report);
            }
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        for (int i = 0; i < arrayRender.size(); i++) {
            fonts.get(i).activeAndBind(0);
            arrayRender.get(i).render();
        }
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void onResize(int sWidth, int sHeight, int reWidth, int reHeight) {
        dirty = true;
    }

    protected void prepareText() {
        arrayRender.forEach(VertexArray::destroy);
        arrayRender.clear();
        fonts.clear();
        int nowPosition = (int) position.minX;
        int topLine = (int) position.minY;
        for (int now = 0; now < text.codePointCount(0, text.length()); now++) {
            int codepoint = text.codePointAt(now);
            if (codepoint == ' ') {
                nowPosition += font.getSpaceLength();
                continue;
            }
            if (codepoint == '\t') {
                nowPosition += font.getSpaceLength() * 4;
                continue;
            }
            FontVertexInfos info = font.getCodepointInfo(codepoint);
            fonts.add(info.atlas.getTexture());
            int width = (int) info.advanceWidth;
            int left = (int) info.leftSide;
            int ascent = (int) info.topSide;
            int height = (int) ((info.maxV - info.minV) * FontAtlas.ATLAS_SIZE);
            float x1 = renderer.getXPosition(nowPosition + left);
            float y1 = renderer.getYPosition(topLine + ascent + height);
            float x2 = renderer.getXPosition(nowPosition += width);
            float y2 = renderer.getYPosition(topLine + ascent);
            arrayRender.add(prepareFontRender(x1, y1, x2, y2, info.minU, info.minV, info.maxU, info.maxV));
        }
    }

    protected VertexArray prepareFontRender(float x1, float y1, float x2, float y2, float tx1, float ty1, float tx2,
                                         float ty2) {
        float r = (color >> 16) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        VertexBuffer buffer = new VertexBuffer(4, GL30.GL_STATIC_DRAW);
        buffer.pos(x1, y1, 0).color(r, g, b).uv(tx1, ty2).endVertex();
        buffer.pos(x2, y1, 0).color(r, g, b).uv(tx2, ty2).endVertex();
        buffer.pos(x2, y2, 0).color(r, g, b).uv(tx2, ty1).endVertex();
        buffer.pos(x1, y2, 0).color(r, g, b).uv(tx1, ty1).endVertex();
        ElementBuffer ebo = new ElementBuffer(2);
        ebo.putTriangle(0, 1, 3);
        ebo.putTriangle(1, 2, 3);
        VertexArray array = new VertexArray(Shaders.FONT);
        array.bindVBO(buffer);
        array.bindEBO(ebo);
        array.upload();
        return array;
    }
}
