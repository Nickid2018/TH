package io.github.nickid2018.tiny2d.gui;

import io.github.nickid2018.tiny2d.RenderThreadOnly;
import io.github.nickid2018.tiny2d.buffer.IndexBufferProvider;
import io.github.nickid2018.tiny2d.buffer.VertexArray;
import io.github.nickid2018.tiny2d.buffer.VertexArrayBuilder;
import io.github.nickid2018.tiny2d.buffer.VertexAttributeList;
import io.github.nickid2018.tiny2d.math.AABB;
import io.github.nickid2018.tiny2d.window.Window;

public abstract class RenderComponent {

    protected Window window;

    protected float x, y;
    protected float width, height;

    protected float renderX, renderY;
    protected float renderWidth, renderHeight;

    protected ComponentResizePolicy resizePolicy;

    public RenderComponent(Window window) {
        this(window, 0, 0, -1, -1);
    }

    public RenderComponent(Window window, float x, float y, float width, float height) {
        this(window, x, y, width, height, ComponentResizePolicy.RESIZE_XY);
    }

    public RenderComponent(Window window, float x, float y, float width, float height, ComponentResizePolicy resizePolicy) {
        this.window = window;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.resizePolicy = resizePolicy;
        computeRenderSize();
    }

    private void computeRenderSize() {
        AABB aabb = resizePolicy.getAABB(x, y, width, height, window);
        renderX = (float) aabb.minX;
        renderY = (float) aabb.minY;
        renderWidth = (float) (aabb.maxX - aabb.minX);
        renderHeight = (float) (aabb.maxY - aabb.minY);
    }

    public void render(GuiRenderContext context) {
    }

    public void onComponentShapeChanged() {
    }

    public void onWindowResize() {
        computeRenderSize();
        onComponentShapeChanged();
    }

    public void onDispose() {
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setResizePolicy(ComponentResizePolicy resizePolicy) {
        this.resizePolicy = resizePolicy;
    }

    @RenderThreadOnly
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
