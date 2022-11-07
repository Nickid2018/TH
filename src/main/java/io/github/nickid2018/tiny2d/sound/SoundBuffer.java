package io.github.nickid2018.tiny2d.sound;

import com.google.common.base.Preconditions;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.OptionalInt;

import static org.lwjgl.openal.AL10.*;

public class SoundBuffer {

    private ByteBuffer buffer;
    private AudioFormat format;
    private int id = -1;

    public SoundBuffer(ByteBuffer buffer, AudioFormat format) {
        this.buffer = buffer;
        this.format = format;
    }

    public SoundBuffer(OggAudioStream stream) {
        try {
            buffer = stream.readAll();
            format = stream.getFormat();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        id = -1;
    }

    private static int audioFormatToOpenAL(AudioFormat format) {
        AudioFormat.Encoding encode = format.getEncoding();
        int channels = format.getChannels();
        int size = format.getSampleSizeInBits();
        if (encode.equals(AudioFormat.Encoding.PCM_UNSIGNED) || encode.equals(AudioFormat.Encoding.PCM_SIGNED)) {
            if (channels == 1) {
                if (size == 8)
                    return AL_FORMAT_MONO8;
                if (size == 16)
                    return AL_FORMAT_MONO16;
            } else if (channels == 2) {
                if (size == 8)
                    return AL_FORMAT_STEREO8;
                if (size == 16)
                    return AL_FORMAT_STEREO16;
            }
        }
        throw new IllegalArgumentException("Invalid audio format");
    }

    public void setStream(OggAudioStream stream) {
        try {
            Preconditions.checkState(!isAlive());
            buffer = stream.readAll();
            format = stream.getFormat();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void init() {
        if (isAlive())
            return;
        id = alGenBuffers();
        if (SoundEngine.checkALError("generating buffer")) {
            id = -1;
            return;
        }
        int type = audioFormatToOpenAL(format);
        alBufferData(id, type, buffer, (int) format.getSampleRate());
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        if (SoundEngine.checkALError("assigning buffer data")) {
            alDeleteBuffers(id);
            id = -1;
        }
    }

    public void delete() {
        alDeleteBuffers(id);
        if (SoundEngine.checkALError("deleting buffer"))
            return;
        id = -1;
    }

    public OptionalInt release() {
        init();
        OptionalInt handle = getHandle();
        id = -1;
        return handle;
    }

    public boolean isAlive() {
        return id != -1;
    }

    public OptionalInt getHandle() {
        return isAlive() ? OptionalInt.of(id) : OptionalInt.empty();
    }
}
