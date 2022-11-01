package io.github.nickid2018.th.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.util.ResourceLocation;

public record SoundDefinition(ResourceLocation location,
                              float volume, float pitch,
                              boolean streaming) {

    public static final Codec<SoundDefinition> CODEC = RecordCodecBuilder.create(app -> app.group(
            ResourceLocation.CODEC.fieldOf("location").forGetter(SoundDefinition::location),
            Codec.floatRange(0, 10).fieldOf("volume").forGetter(SoundDefinition::volume),
            Codec.floatRange(0, 10).fieldOf("pitch").forGetter(SoundDefinition::pitch),
            Codec.BOOL.fieldOf("streaming").orElse(false).forGetter(SoundDefinition::streaming)
    ).apply(app, SoundDefinition::new));
}
