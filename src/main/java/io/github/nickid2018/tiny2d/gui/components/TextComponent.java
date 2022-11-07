package io.github.nickid2018.tiny2d.gui.components;

import io.github.nickid2018.tiny2d.buffer.VertexArray;
import io.github.nickid2018.tiny2d.font.FontRenderer;
import io.github.nickid2018.tiny2d.gui.ComponentResizePolicy;
import io.github.nickid2018.tiny2d.gui.GuiRenderContext;
import io.github.nickid2018.tiny2d.gui.RenderComponent;
import io.github.nickid2018.tiny2d.texture.StaticTexture;
import io.github.nickid2018.tiny2d.window.Window;
import it.unimi.dsi.fastutil.Pair;

import java.util.List;

public class TextComponent extends RenderComponent {

    private String text;
    private int size;
    private float r, g, b;

    private List<Pair<StaticTexture, VertexArray>> arrayData;

    public static TextComponent create(Window window, String text, int size, float x, float y) {
        float width = window.getFontRenderer().getTextWidth(text, size);
        return new TextComponent(window, text, size, x, y, width, (float) size);
    }

    protected TextComponent(Window window, String text, int size, float x, float y, float width, float height) {
        super(window, x, y, width, height, ComponentResizePolicy.NO_RESIZE_XY_FIXED);
        this.size = size;
        this.text = text;
    }

    @Override
    public void render(GuiRenderContext context) {
        if (arrayData == null)
            arrayData = window.getFontRenderer().createTextArrayDataMerged(text, x, y, size, r, g, b);
        FontRenderer.drawTextArrayData(arrayData);
    }

    @Override
    public void onComponentShapeChanged() {
        freeTextData();
    }

    private void freeTextData() {
        if (arrayData != null) {
            FontRenderer.destroyTextArrayData(arrayData);
            width = window.getFontRenderer().getTextWidth(text, size);
            height = size;
            arrayData = null;
        }
    }

    public void setSize(int size) {
        if (this.size == size)
            return;
        this.size = size;
        freeTextData();
    }

    public void setColor(int rgb) {
        r = ((rgb >> 16) & 0xFF) / 255F;
        g = ((rgb >> 8) & 0xFF) / 255F;
        b = (rgb & 0xFF) / 255F;
        freeTextData();
    }

    public void setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        freeTextData();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text.equals(this.text))
            return;
        this.text = text;
        freeTextData();
    }
}
