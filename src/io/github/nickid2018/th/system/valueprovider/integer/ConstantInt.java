package io.github.nickid2018.th.system.valueprovider.integer;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.compute.HittableItem;
import lombok.Getter;

public record ConstantInt(@Getter int value) implements IntProvider {

    public static final Codec<ConstantInt> CODEC = Codec.either(
            Codec.INT,
            RecordCodecBuilder.<ConstantInt>create(app -> app.group(
                    Codec.INT.fieldOf("value").forGetter(ConstantInt::value)
            ).apply(app, ConstantInt::new))
    ).xmap(e -> e.map(ConstantInt::new, v -> v), Either::right);

    @Override
    public Integer getValue(HittableItem item) {
        return value;
    }
}
