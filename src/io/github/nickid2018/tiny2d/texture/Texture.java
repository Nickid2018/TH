package io.github.nickid2018.tiny2d.texture;

import io.github.nickid2018.tiny2d.RenderThreadOnly;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.*;

public interface Texture {

    @RenderThreadOnly
    void bindInternal();

    @RenderThreadOnly
    default void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @RenderThreadOnly
    default void bind() {
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        bindInternal();
    }

    @RenderThreadOnly
    default void activeAndBind(int unit) {
        GL30.glActiveTexture(GL30.GL_TEXTURE0 + unit);
        bindInternal();
    }

    boolean isLinear();

    @RenderThreadOnly
    Texture setLinear(boolean linear);

    boolean isClamp();

    @RenderThreadOnly
    Texture setClamp(boolean clamp);

    @RenderThreadOnly
    Texture update();

    @RenderThreadOnly
    Texture update(int x, int y, int sizeX, int sizeY);

    void delete();

    void deleteTextureAndImage();
}

