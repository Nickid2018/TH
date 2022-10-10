package io.github.nickid2018.tiny2d.sound;

import com.google.common.base.Preconditions;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.IntBuffer;
import java.util.Optional;

public class SoundEngine {

    public static final Logger LOGGER = LoggerFactory.getLogger("Sound System");

    private static long device;
    private static long context;
    private static int channelCount;

    public static void initEngine(CharSequence deviceName) {
        device = ALC10.alcOpenDevice(deviceName);
        Preconditions.checkState(!checkALCError("opening device"));
        ALCCapabilities cap = ALC.createCapabilities(device);
        Preconditions.checkState(!checkALCError("getting capabilities"));
        Preconditions.checkState(cap.OpenALC11, "OpenAL 1.1 not supported");
        context = ALC10.alcCreateContext(device, (IntBuffer) null);
        ALC10.alcMakeContextCurrent(context);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int size = ALC10.alcGetInteger(device, ALC10.ALC_ATTRIBUTES_SIZE);
            Preconditions.checkState(!checkALCError("getting attributes size"));
            IntBuffer buffer = stack.mallocInt(size);
            ALC10.alcGetIntegerv(device, ALC10.ALC_ALL_ATTRIBUTES, buffer);
            Preconditions.checkState(!checkALCError("getting attributes"));
            int index = 0;
            while (index < size) {
                int name = buffer.get(index++);
                if (name == 0) {
                    channelCount = 30;
                    break;
                }
                int count = buffer.get(index++);
                if (name == ALC11.ALC_MONO_SOURCES) {
                    channelCount = count;
                    break;
                }
            }
        }
        ALCapabilities capUse = AL.createCapabilities(cap);
        Preconditions.checkState(!checkALError("initialization"));
        Preconditions.checkState(capUse.AL_EXT_source_distance_model, "AL_EXT_source_distance_model is not supported");
        AL10.alEnable(512);
        Preconditions.checkState(capUse.AL_EXT_LINEAR_DISTANCE, "AL_EXT_LINEAR_DISTANCE is not supported");
        checkALError("enabling per-source distance models");
        LOGGER.info("OpenAL Initialized");
    }

    public static void stopEngine() {
        ALC10.alcDestroyContext(context);
        if (device != 0L)
            ALC10.alcCloseDevice(device);
    }

    public static int getChannelCount() {
        return channelCount;
    }

    @AudioThreadOnly
    public static Optional<String> getALError() {
        int errorCode = AL10.alGetError();
        return switch (errorCode) {
            case AL10.AL_NO_ERROR -> Optional.empty();
            case AL10.AL_INVALID_NAME -> Optional.of("Invalid name");
            case AL10.AL_INVALID_ENUM -> Optional.of("Invalid enum");
            case AL10.AL_INVALID_VALUE -> Optional.of("Invalid value");
            case AL10.AL_INVALID_OPERATION -> Optional.of("Invalid operation");
            case AL10.AL_OUT_OF_MEMORY -> Optional.of("Out of memory");
            default -> Optional.of("Unknown AL Error");
        };
    }

    @AudioThreadOnly
    public static boolean checkALError(String stage) {
        Optional<String> err = getALError();
        if (err.isPresent()) {
            LOGGER.error("AL Error in {}: {}", stage, err.get());
            return true;
        }
        return false;
    }

    @AudioThreadOnly
    public static Optional<String> getALCError() {
        int errorCode = ALC10.alcGetError(device);
        return switch (errorCode) {
            case ALC10.ALC_NO_ERROR -> Optional.empty();
            case ALC10.ALC_INVALID_DEVICE -> Optional.of("Invalid device");
            case ALC10.ALC_INVALID_CONTEXT -> Optional.of("Invalid context");
            case ALC10.ALC_INVALID_ENUM -> Optional.of("Invalid enum");
            case ALC10.ALC_INVALID_VALUE -> Optional.of("Invalid value");
            case ALC10.ALC_OUT_OF_MEMORY -> Optional.of("Out of memory");
            default -> Optional.of("Unknown ALC Error");
        };
    }

    @AudioThreadOnly
    public static boolean checkALCError(String stage) {
        Optional<String> err = getALCError();
        if (err.isPresent()) {
            LOGGER.error("ALC Error in {}: {}", stage, err.get());
            return true;
        }
        return false;
    }
}
