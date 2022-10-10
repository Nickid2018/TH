package io.github.nickid2018.tiny2d.texture;

import io.github.nickid2018.tiny2d.RenderThreadOnly;
import org.lwjgl.opengl.GL30;

public interface Texture {

    @RenderThreadOnly
    void bind();

    @RenderThreadOnly
    void unbind();

    @RenderThreadOnly
    default void activeAndBind(int unit) {
        GL30.glActiveTexture(GL30.GL_TEXTURE0 + unit);
        bind();
    }

    boolean isLinear();

    Texture setLinear(boolean linear);

    boolean isClamp();

    Texture setClamp(boolean clamp);

    @RenderThreadOnly
    Texture update();

    @RenderThreadOnly
    Texture update(int x, int y, int sizeX, int sizeY);
}

