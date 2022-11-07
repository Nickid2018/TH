package io.github.nickid2018.th.pack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record PackMetadata(int packFormat,
                           String version,
                           String name,
                           List<String> authors,
                           String description) {

    public static final Codec<PackMetadata> CODEC = RecordCodecBuilder.create(app -> app.group(
                    Codec.INT.fieldOf("pack_format").forGetter(PackMetadata::packFormat),
                    Codec.STRING.fieldOf("version").forGetter(PackMetadata::version),
                    Codec.STRING.fieldOf("name").forGetter(PackMetadata::name),
                    Codec.list(Codec.STRING).fieldOf("authors").forGetter(PackMetadata::authors),
                    Codec.STRING.fieldOf("description").forGetter(PackMetadata::description)
            ).apply(app, PackMetadata::new));
}
