package io.github.nickid2018.tiny2d.texture;

import org.lwjgl.stb.STBImage;

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

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getLevel() {
        return level;
    }

    public boolean isLinear() {
        return linear;
    }

    public StaticTexture setLinear(boolean linear) {
        this.linear = linear;
        return this;
    }

    public boolean isClamp() {
        return clamp;
    }

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

    public StaticTexture update() {
        return update(0, 0, image.getWidth(), image.getHeight());
    }

    public StaticTexture update(int x, int y, int sizeX, int sizeY) {
        bind();
        image.upload(0, x, y, x, y, sizeX, sizeY, linear, clamp, level > 0);
        if (level > 0)
            glGenerateMipmap(level);
        return this;
    }
}
