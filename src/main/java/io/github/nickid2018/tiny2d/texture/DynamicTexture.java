package io.github.nickid2018.tiny2d.texture;

import io.github.nickid2018.tiny2d.RenderThreadOnly;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.ArrayList;
import java.util.List;

public class DynamicTexture implements Texture {

    private final IntList delays;
    private final int totalTime;
    private final List<StaticTexture> textures = new ArrayList<>();
    private boolean linear;
    private boolean clamp;

    public DynamicTexture(DynamicImage image, int level) {
        delays = image.getDelays();
        totalTime = image.getTotalTime();
        for (int now = 0; now < image.getFrames(); now++) {
            StaticTexture texture = new StaticTexture(image.getImage(now), level);
            textures.add(texture);
        }
    }

    public DynamicTexture(List<StaticTexture> textures, IntList delays) {
        this.textures.addAll(textures);
        this.delays = delays;
        int time = 0;
        for (int now : delays)
            time += now;
        totalTime = time;
    }

    @Override
    @RenderThreadOnly
    public void bindInternal() {
        if (totalTime != 0) {
            long remaining = System.currentTimeMillis() % totalTime;
            for (int now = 0; now < delays.size(); now++) {
                if ((remaining -= delays.getInt(now)) <= 0) {
                    textures.get(now).bind();
                    break;
                }
            }
        }
    }

    public boolean isLinear() {
        return linear;
    }

    @RenderThreadOnly
    public DynamicTexture setLinear(boolean linear) {
        this.linear = linear;
        for (StaticTexture texture : textures)
            texture.setLinear(linear);
        return this;
    }

    public boolean isClamp() {
        return clamp;
    }

    @RenderThreadOnly
    public DynamicTexture setClamp(boolean clamp) {
        this.clamp = clamp;
        for (StaticTexture texture : textures)
            texture.setClamp(clamp);
        return this;
    }

    @RenderThreadOnly
    public DynamicTexture update() {
        for (StaticTexture texture : textures)
            texture.update();
        return this;
    }

    public DynamicTexture update(int x, int y, int sizeX, int sizeY) {
        throw new UnsupportedOperationException("update");
    }

    @Override
    @RenderThreadOnly
    public void delete() {
        for (StaticTexture texture : textures)
            texture.delete();
    }

    @Override
    @RenderThreadOnly
    public void deleteTextureAndImage() {
        for (StaticTexture texture : textures)
            texture.deleteTextureAndImage();
    }

    public StaticTexture getFrame(int frame) {
        return textures.get(frame);
    }
}

