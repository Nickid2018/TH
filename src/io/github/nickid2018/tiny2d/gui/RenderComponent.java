package io.github.nickid2018.tiny2d.gui;

import io.github.nickid2018.tiny2d.buffer.IndexBufferProvider;
import io.github.nickid2018.tiny2d.buffer.VertexArray;
import io.github.nickid2018.tiny2d.buffer.VertexArrayBuilder;
import io.github.nickid2018.tiny2d.buffer.VertexAttributeList;
import io.github.nickid2018.tiny2d.window.Window;

public abstract class RenderComponent {

    private int x, y;
    private int width, height;

    public RenderComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(GuiRenderContext context) {
    }

    public void onComponentShapeChanged() {
    }

    public void onWindowResize(Window window) {
    }

    public void onDispose() {
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    protected VertexArray createWindowColoredTexture(Window window) {
        VertexArrayBuilder builder = new VertexArrayBuilder(
                VertexAttributeList.COLOR_TEXTURE_2D, IndexBufferProvider.DEFAULT);
        float ndcX = window.toNDCX(x);
        float ndcY = window.toNDCY(y);
        float ndcXE = window.toNDCX(x + width);
        float ndcYE = window.toNDCY(y + height);
        builder.pos(ndcX, ndcY).color(1, 1, 1).uv(0, 0).end();
        builder.pos(ndcXE, ndcY).color(1, 1, 1).uv(1, 0).end();
        builder.pos(ndcX, ndcYE).color(1, 1, 1).uv(0, 1).end();
        builder.pos(ndcXE, ndcYE).color(1, 1, 1).uv(1, 1).end();
        return builder.build();
    }
}
