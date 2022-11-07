package io.github.nickid2018.th.sound;

import io.github.nickid2018.th.system.compute.Tickable;
import io.github.nickid2018.tiny2d.sound.SoundInstance;
import lombok.Getter;
import org.lwjgl.openal.AL10;

public class Sound implements Tickable {

    @Getter
    private final SoundDefinition definition;
    @Getter
    private final SoundInstance instance;

    public Sound(SoundDefinition definition) {
        this.definition = definition;
        this.instance = SoundRepository.createSoundInstance(definition);
    }

    public void play() {
        instance.play();
    }

    public void stop() {
        instance.stop();
    }

    public void pause() {
        instance.pause();
    }

    public void resume() {
        instance.unpause();
    }

    public boolean isPlaying() {
        return instance.getState() == AL10.AL_PLAYING;
    }

    @Override
    public void tick(long tickTime) {
        if (definition.streaming())
            instance.updateStream();
    }
}
