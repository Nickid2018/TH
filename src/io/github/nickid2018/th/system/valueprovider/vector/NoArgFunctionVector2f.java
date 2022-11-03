package io.github.nickid2018.th.system.valueprovider.vector;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.compute.HittableItem;
import org.joml.Vector2f;

public record NoArgFunctionVector2f(String actionKey) implements Vector2fProvider {

    public static final Codec<NoArgFunctionVector2f> NO_ARG_CODEC = RecordCodecBuilder.create(app -> app.group(
            Codec.STRING.fieldOf("action").forGetter(NoArgFunctionVector2f::actionKey)
    ).apply(app, NoArgFunctionVector2f::new));

    public static final Codec<NoArgFunctionVector2f> CODEC = Codec.either(
            Codec.STRING, NO_ARG_CODEC
    ).xmap(e -> e.map(NoArgFunctionVector2f::new, v -> v), Either::right);

    @Override
    public Vector2f getValue(HittableItem item) {
        return null;
    }

    @Override
    public String getComputeType() {
        return "no_arg_function";
    }
}
