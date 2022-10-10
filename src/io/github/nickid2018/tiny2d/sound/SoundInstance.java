package io.github.nickid2018.tiny2d.sound;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.openal.AL11.*;

public class SoundInstance {

    private final int source;

    private final AtomicBoolean initialized = new AtomicBoolean(true);

    private int streamingBufferSize = 16384;

    private OggAudioStream stream;

    private SoundInstance(int source) {
        this.source = source;
    }

    @AudioThreadOnly
    public static SoundInstance create() {
        int source = alGenSources();
        return SoundEngine.checkALError("allocating new source") ? null : new SoundInstance(source);
    }

    private static int calculateBufferSize(AudioFormat audioFormat) {
        return (int) (audioFormat.getSampleSizeInBits() / 8.0F * audioFormat.getChannels() * audioFormat.getSampleRate());
    }

    @AudioThreadOnly
    public void destroy() {
        if (initialized.compareAndSet(true, false)) {
            alSourceStop(source);
            SoundEngine.checkALError("stopping");
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    SoundEngine.LOGGER.error("Failed to close audio stream", e);
                }
                removeProcessedBuffers();
                stream = null;
            }
            alDeleteSources(source);
            SoundEngine.checkALError("cleaning up");
        }
    }

    @AudioThreadOnly
    public SoundInstance play() {
        alSourcePlay(source);
        return this;
    }

    @AudioThreadOnly
    private int getState() {
        return !initialized.get() ? AL_STOPPED : alGetSourcei(source, AL_SOURCE_STATE);
    }

    @AudioThreadOnly
    public SoundInstance pause() {
        if (getState() == AL_PLAYING) alSourcePause(source);
        return this;
    }

    @AudioThreadOnly
    public SoundInstance unpause() {
        if (getState() == AL_PAUSED) alSourcePlay(source);
        return this;
    }

    @AudioThreadOnly
    public SoundInstance stop() {
        if (initialized.get()) {
            alSourceStop(source);
            SoundEngine.checkALError("stoping");
        }
        return this;
    }

    public boolean stopped() {
        return getState() == AL_STOPPED;
    }

    @AudioThreadOnly
    public SoundInstance setSelfPosition(float x, float y, float z) {
        alSourcefv(source, AL_POSITION, new float[]{x, y, z});
        return this;
    }

    @AudioThreadOnly
    public SoundInstance setPitch(float pitch) {
        alSourcef(source, AL_PITCH, pitch);
        return this;
    }

    @AudioThreadOnly
    public SoundInstance setLooping(boolean loop) {
        alSourcei(source, AL_LOOPING, loop ? 1 : 0);
        return this;
    }

    @AudioThreadOnly
    public SoundInstance setVolume(float volume) {
        alSourcef(source, AL_GAIN, volume);
        return this;
    }

    @AudioThreadOnly
    public SoundInstance disableAttenuation() {
        alSourcei(source, AL_DISTANCE_MODEL, 0);
        return this;
    }

    @AudioThreadOnly
    public SoundInstance linearAttenuation(float maxDistance) {
        alSourcei(source, AL_DISTANCE_MODEL, AL_LINEAR_DISTANCE_CLAMPED);
        alSourcef(source, AL_MAX_DISTANCE, maxDistance);
        alSourcef(source, AL_ROLLOFF_FACTOR, 1.0F);
        alSourcef(source, AL_REFERENCE_DISTANCE, 0.0F);
        return this;
    }

    @AudioThreadOnly
    public SoundInstance setRelative(boolean relative) {
        alSourcei(source, AL_SOURCE_RELATIVE, relative ? 1 : 0);
        return this;
    }

    @AudioThreadOnly
    public SoundInstance attachStaticBuffer(SoundBuffer soundBuffer) {
        soundBuffer.getHandle().ifPresent(i -> alSourcei(source, AL_BUFFER, i));
        return this;
    }

    @AudioThreadOnly
    public SoundInstance attachBufferStream(OggAudioStream audioStream) {
        stream = audioStream;
        AudioFormat audioFormat = audioStream.getFormat();
        streamingBufferSize = calculateBufferSize(audioFormat);
        pumpBuffers(4);
        return this;
    }

    @AudioThreadOnly
    private void pumpBuffers(int buffers) {
        if (stream != null)
            try {
                for (int index = 0; index < buffers; index++) {
                    ByteBuffer buffer = stream.read(streamingBufferSize);
                    if (buffer != null)
                        (new SoundBuffer(buffer, stream.getFormat())).release().ifPresent(id -> alSourceQueueBuffers(source, id));
                }
            } catch (IOException e) {
                SoundEngine.LOGGER.error("Failed to read from audio stream", e);
            }
    }

    @AudioThreadOnly
    public void updateStream() {
        if (stream != null && getState() == AL_PLAYING) {
            pumpBuffers(removeProcessedBuffers());
        }
    }

    public boolean isStreamed() {
        return stream != null;
    }

    @AudioThreadOnly
    private int removeProcessedBuffers() {
        int bufferSize = alGetSourcei(source, AL_BUFFERS_PROCESSED);
        if (bufferSize > 0) {
            int[] buffers = new int[bufferSize];
            alSourceUnqueueBuffers(source, buffers);
            SoundEngine.checkALError("Dequeue buffers");
            alDeleteBuffers(buffers);
            SoundEngine.checkALError("Remove processed buffers");
        }
        return bufferSize;
    }
}
