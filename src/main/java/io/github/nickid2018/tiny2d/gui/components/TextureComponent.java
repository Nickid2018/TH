package io.github.nickid2018.tiny2d.gui.components;

import io.github.nickid2018.tiny2d.buffer.IndexBufferProvider;
import io.github.nickid2018.tiny2d.buffer.VertexArray;
import io.github.nickid2018.tiny2d.buffer.VertexArrayBuilder;
import io.github.nickid2018.tiny2d.buffer.VertexAttributeList;
import io.github.nickid2018.tiny2d.gui.ComponentResizePolicy;
import io.github.nickid2018.tiny2d.gui.GuiRenderContext;
import io.github.nickid2018.tiny2d.gui.RenderComponent;
import io.github.nickid2018.tiny2d.shader.ShaderProgram;
import io.github.nickid2018.tiny2d.texture.StaticTexture;
import io.github.nickid2018.tiny2d.window.Window;

public class TextureComponent extends RenderComponent {

    private StaticTexture texture;

    private float u1, v1, u2, v2;
    private float r, g, b;

    private VertexArray arrayData;

    public static TextureComponent create(Window window, StaticTexture texture, float x, float y, float x1, float y1) {
        return new TextureComponent(window, texture, x, y, x1 - x, y1 - y);
    }

    public TextureComponent(Window window, StaticTexture texture, float x, float y, float width, float height) {
        this(window, texture, x, y, width, height,
                0, 0, 1, 1,
                1, 1, 1,
                ComponentResizePolicy.RESIZE_XY);
    }

    public TextureComponent(Window window, StaticTexture texture,
                            float x, float y, float width, float height,
                            float u1, float v1, float u2, float v2) {
        this(window, texture, x, y, width, height,
                u1, v1, u2, v2,
                1, 1, 1,
                ComponentResizePolicy.RESIZE_XY);
    }

    public TextureComponent(Window window, StaticTexture texture,
                            float x, float y, float width, float height,
                            float u1, float v1, float u2, float v2,
                            float r, float g, float b) {
        this(window, texture, x, y, width, height,
                u1, v1, u2, v2,
                r, g, b,
                ComponentResizePolicy.RESIZE_XY);
    }

    public TextureComponent(Window window, StaticTexture texture,
                            float x, float y, float width, float height,
                            float u1, float v1, float u2, float v2,
                            float r, float g, float b, ComponentResizePolicy policy) {
        super(window, x, y, width, height, policy);
        this.texture = texture;
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    private void freeData() {
        if (arrayData != null) {
            arrayData.destroy();
            arrayData = null;
        }
    }

    @Override
    public void render(GuiRenderContext context) {
        if (arrayData == null) {
            VertexArrayBuilder builder = new VertexArrayBuilder(VertexAttributeList.TEXTURE_2D, IndexBufferProvider.DEFAULT);
            builder.pos(renderX, renderY).uv(u1, v1).end();
            builder.pos(renderX + renderWidth, renderY).uv(u2, v1).end();
            builder.pos(renderX, renderY + renderHeight).uv(u1, v2).end();
            builder.pos(renderX + renderWidth, renderY + renderHeight).uv(u2, v2).end();
            arrayData = builder.build();
        }
        ShaderProgram shader = ShaderProgram.getDefaultShader("tex_color");
        shader.use();
        shader.addUniform("color").set3fv(r, g, b);
        texture.bind();
        arrayData.draw();
    }

    @Override
    public void onComponentShapeChanged() {
        freeData();
    }

    public void setTexture(StaticTexture texture) {
        this.texture = texture;
    }

    public void setUV(float u1, float v1, float u2, float v2) {
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
        freeData();
    }

    public void setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        freeData();
    }
}
