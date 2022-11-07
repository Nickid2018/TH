package io.github.nickid2018.tiny2d.texture;

import com.madgag.gif.fmsware.GifDecoder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.lwjgl.system.MemoryUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DynamicImage {

    private final List<Image> textures = new ArrayList<>();
    private final IntList delays = new IntArrayList();
    private int totalTime = 0;

    public DynamicImage(InputStream gifStream) throws IOException {
        GifDecoder decoder = new GifDecoder();
        int status = decoder.read(gifStream);
        if (status != GifDecoder.STATUS_OK)
            throw new IOException(
                    status == GifDecoder.STATUS_OPEN_ERROR ? "Error in opening gif data" : "Invalid GIF Format");
        for (int i = 0; i < decoder.getFrameCount(); i++) {
            int delay = decoder.getDelay(i);
            delays.add(delay);
            totalTime += delay;
            BufferedImage image = decoder.getFrame(i);
            int[] data = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
            //ARGB->~RGBA
            for (int now = 0; now < data.length; now++) {
                int color = data[now];
                data[now] = (color & 0xFF000000) | ((color & 0x00FF0000) >> 16) | ((color & 0x0000FF00))
                        | ((color & 0x000000FF) << 16);
            }
            ByteBuffer buffer = MemoryUtil.memAlloc(data.length * 4);
            buffer.asIntBuffer().put(data);
            buffer.rewind();
            Image tex = new Image(ImageFormat.RGBA, image.getWidth(), image.getHeight(), false,
                    MemoryUtil.memAddress(buffer));
            textures.add(tex);
        }
    }

    public IntList getDelays() {
        return delays;
    }

    public int getFrames() {
        return delays.size();
    }

    public Image getImage(int frame) {
        return textures.get(frame);
    }

    public int getTotalTime() {
        return totalTime;
    }
}
