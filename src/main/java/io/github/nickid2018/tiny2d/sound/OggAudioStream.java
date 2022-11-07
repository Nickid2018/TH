package io.github.nickid2018.tiny2d.sound;

import com.google.common.collect.Lists;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import javax.sound.sampled.AudioFormat;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

public class OggAudioStream implements Closeable {

    private final AudioFormat audioFormat;
    private final InputStream input;
    private long handle;
    private ByteBuffer buffer = MemoryUtil.memAlloc(8192);

    public OggAudioStream(InputStream stream) throws IOException {
        input = stream;
        buffer.limit(0);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer dataBlocks = stack.mallocInt(1);
            IntBuffer error = stack.mallocInt(1);
            while (handle == 0L) {
                if (refillFromStreamReturnEOF())
                    throw new IOException("Failed to find Ogg header");
                int pos = buffer.position();
                buffer.position(0);
                handle = STBVorbis.stb_vorbis_open_pushdata(buffer, dataBlocks, error, null);
                buffer.position(pos);
                int errorInt = error.get(0);
                if (errorInt == 1) {
                    forwardBuffer();
                    continue;
                }
                if (errorInt != 0)
                    throw new IOException("Failed to read Ogg file " + errorInt);
            }
            buffer.position(buffer.position() + dataBlocks.get(0));
            STBVorbisInfo info = STBVorbisInfo.malloc(stack);
            STBVorbis.stb_vorbis_get_info(handle, info);
            audioFormat = new AudioFormat(info.sample_rate(), 16, info.channels(), true, false);
        }
    }

    private static int clamp(int value) {
        return Math.min(32767, Math.max(value, -32768));
    }

    private boolean refillFromStreamReturnEOF() throws IOException {
        int limit = buffer.limit();
        int remains = buffer.capacity() - limit;
        if (remains == 0)
            return false;
        byte[] streamFill = new byte[remains];
        int length = input.read(streamFill);
        if (length == -1)
            return true;
        int pos = buffer.position();
        buffer.limit(limit + length);
        buffer.position(limit);
        buffer.put(streamFill, 0, length);
        buffer.position(pos);
        return false;
    }

    private void forwardBuffer() {
        boolean atHead = buffer.position() == 0;
        boolean atTail = buffer.position() == buffer.limit();
        if (atTail && !atHead) {
            buffer.position(0);
            buffer.limit(0);
        } else {
            ByteBuffer newBuffer = MemoryUtil.memAlloc(atHead ? buffer.capacity() << 1 : buffer.capacity());
            newBuffer.put(buffer);
            MemoryUtil.memFree(buffer);
            newBuffer.flip();
            buffer = newBuffer;
        }
    }

    private boolean readFrame(BufferList list) throws IOException {
        if (handle == 0L)
            return false;
        if (input.available() == 0)
            return false;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer output = stack.mallocPointer(1);
            IntBuffer channels = stack.mallocInt(1);
            IntBuffer sample = stack.mallocInt(1);
            while (true) {
                int readCount = STBVorbis.stb_vorbis_decode_frame_pushdata(handle, buffer, channels, output, sample);
                buffer.position(buffer.position() + readCount);
                int errorCode = STBVorbis.stb_vorbis_get_error(handle);
                if (errorCode == STBVorbis.VORBIS_need_more_data) {
                    forwardBuffer();
                    if (refillFromStreamReturnEOF())
                        break;
                    continue;
                }
                if (errorCode != STBVorbis.VORBIS__no_error)
                    throw new IOException("Failed to read Ogg file " + errorCode);
                int sampleCount = sample.get(0);
                if (sampleCount == 0)
                    continue;
                int channelCount = channels.get(0);
                PointerBuffer pointer = output.getPointerBuffer(channelCount);
                if (channelCount == 1) {
                    convertMono(pointer.getFloatBuffer(0, sampleCount), list);
                    return true;
                }
                if (channelCount == 2) {
                    convertStereo(pointer.getFloatBuffer(0, sampleCount), pointer.getFloatBuffer(1, sampleCount), list);
                    return true;
                }
                throw new IllegalStateException("Invalid number of channels: " + channelCount);
            }
            return false;
        }
    }

    private void convertMono(FloatBuffer channel, BufferList list) {
        while (channel.hasRemaining())
            list.put(channel.get());
    }

    private void convertStereo(FloatBuffer channel1, FloatBuffer channel2, BufferList list) {
        while (channel1.hasRemaining() && channel2.hasRemaining()) {
            list.put(channel1.get());
            list.put(channel2.get());
        }
    }

    public void close() throws IOException {
        if (handle != 0L) {
            STBVorbis.stb_vorbis_close(handle);
            handle = 0L;
        }
        MemoryUtil.memFree(buffer);
        input.close();
    }

    public AudioFormat getFormat() {
        return audioFormat;
    }

    public ByteBuffer read(int bytes) throws IOException {
        BufferList list = new BufferList(bytes + 8192);
        while (readFrame(list) && list.byteCount < bytes)
            ;
        return list.get();
    }

    public ByteBuffer readAll() throws IOException {
        BufferList list = new BufferList(16384);
        while (readFrame(list))
            ;
        return list.get();
    }

    private static class BufferList {

        private final List<ByteBuffer> buffers = Lists.newArrayList();

        private final int bufferSize;

        private int byteCount;

        private ByteBuffer currentBuffer;

        public BufferList(int size) {
            bufferSize = size + 1 & 0xFFFFFFFE;
            createNewBuffer();
        }

        private void createNewBuffer() {
            currentBuffer = BufferUtils.createByteBuffer(bufferSize);
        }

        public void put(float f) {
            if (currentBuffer.remaining() == 0) {
                currentBuffer.flip();
                buffers.add(currentBuffer);
                createNewBuffer();
            }
            int i = clamp((int) (f * 32767.5F - 0.5F));
            currentBuffer.putShort((short) i);
            byteCount += 2;
        }

        public ByteBuffer get() {
            currentBuffer.flip();
            if (buffers.isEmpty())
                return currentBuffer;
            ByteBuffer byteBuffer = BufferUtils.createByteBuffer(byteCount);
            buffers.forEach(byteBuffer::put);
            byteBuffer.put(currentBuffer);
            byteBuffer.flip();
            return byteBuffer;
        }
    }
}

