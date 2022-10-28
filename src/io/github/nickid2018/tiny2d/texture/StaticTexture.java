package io.github.nickid2018.tiny2d.texture;

import io.github.nickid2018.tiny2d.RenderThreadOnly;

import static org.lwjgl.opengl.GL30.*;

public class StaticTexture implements Texture {

    private final int id;
    private final Image image;
    private final int level;
    private boolean linear;
    private boolean clamp;

    public StaticTexture(Image image, int level) {
        id = TextureUtil.generateTextureId();
        this.image = image;
        this.level = level;
        TextureUtil.prepareImage(id, image.getWidth(), image.getHeight());
    }

    @RenderThreadOnly
    public void bindInternal() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public int getLevel() {
        return level;
    }

    public boolean isLinear() {
        return linear;
    }

    @RenderThreadOnly
    public StaticTexture setLinear(boolean linear) {
        this.linear = linear;
        return this;
    }

    public boolean isClamp() {
        return clamp;
    }

    @RenderThreadOnly
    public StaticTexture setClamp(boolean clamp) {
        this.clamp = clamp;
        return this;
    }

    public int getId() {
        return id;
    }

    public Image getImage() {
        return image;
    }

    @RenderThreadOnly
    public StaticTexture update() {
        return update(0, 0, image.getWidth(), image.getHeight());
    }

    @RenderThreadOnly
    public StaticTexture update(int x, int y, int sizeX, int sizeY) {
        bindInternal();
        image.upload(0, x, y, x, y, sizeX, sizeY, linear, clamp, level > 0);
        if (level > 0)
            glGenerateMipmap(level);
        return this;
    }

    @RenderThreadOnly
    @Override
    public void delete() {
        glDeleteTextures(id);
    }

    @RenderThreadOnly
    @Override
    public void deleteTextureAndImage() {
        glDeleteTextures(id);
        image.close();
    }
}
