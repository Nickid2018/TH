package io.github.nickid2018.th.system.valueprovider.floating;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.compute.HittableItem;
import lombok.Getter;

public record ConstantFloat(@Getter float value) implements FloatProvider {

    public static final Codec<ConstantFloat> CODEC = Codec.either(
            Codec.FLOAT,
            RecordCodecBuilder.<ConstantFloat>create(app -> app.group(
                    Codec.FLOAT.fieldOf("value").forGetter(ConstantFloat::value)
            ).apply(app, ConstantFloat::new))
    ).xmap(e -> e.map(ConstantFloat::new, v -> v), Either::right);

    @Override
    public Float getValue(HittableItem item) {
        return value;
    }

    @Override
    public String getComputeType() {
        return "constant";
    }
}
