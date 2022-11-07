package io.github.nickid2018.th.sound;

import io.github.nickid2018.th.pack.PackManager;
import io.github.nickid2018.th.util.ResourceLocation;
import io.github.nickid2018.tiny2d.sound.OggAudioStream;
import io.github.nickid2018.tiny2d.sound.SoundBuffer;
import io.github.nickid2018.tiny2d.sound.SoundInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SoundRepository {

    public static final Logger LOGGER = LoggerFactory.getLogger("Sound Repository");

    private static final Map<ResourceLocation, SoundDefinition> SOUND_DEFINITION_MAP = new HashMap<>();
    private static final Map<ResourceLocation, SoundInstance> STATIC_SOUND_INSTANCE_MAP = new HashMap<>();

    public static SoundDefinition createSoundDefinition(ResourceLocation location) {
        return SOUND_DEFINITION_MAP.computeIfAbsent(location, l -> PackManager.createObject(l, SoundDefinition.CODEC));
    }

    public static SoundInstance createSoundInstance(SoundDefinition definition) {
        if (definition.streaming()) {
            SoundInstance soundInstance = SoundInstance.create();
            soundInstance.setVolume(definition.volume());
            soundInstance.setPitch(definition.pitch());
            try {
                soundInstance.attachBufferStream(new OggAudioStream(
                        PackManager.createInputStream(definition.location())));
            } catch (IOException e) {
                LOGGER.error("Cannot load streaming sound " + definition.location(), e);
            }
            return soundInstance;
        } else {
            if (STATIC_SOUND_INSTANCE_MAP.containsKey(definition.location()))
                return STATIC_SOUND_INSTANCE_MAP.get(definition.location());

            SoundInstance soundInstance = SoundInstance.create();
            soundInstance.setVolume(definition.volume());
            soundInstance.setPitch(definition.pitch());
            try {
                SoundBuffer buffer = new SoundBuffer(new OggAudioStream(
                        PackManager.createInputStream(definition.location())));
                buffer.init();
                soundInstance.attachStaticBuffer(buffer);
            } catch (IOException e) {
                LOGGER.error("Cannot load static sound " + definition.location(), e);
            }

            STATIC_SOUND_INSTANCE_MAP.put(definition.location(), soundInstance);
            return soundInstance;
        }
    }
}
