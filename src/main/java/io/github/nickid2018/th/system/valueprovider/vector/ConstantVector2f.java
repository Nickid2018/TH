package io.github.nickid2018.th.system.valueprovider.vector;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.util.CodecUtil;
import lombok.Getter;
import org.joml.Vector2f;

public record ConstantVector2f(@Getter Vector2f value) implements Vector2fProvider {

    public static final Codec<ConstantVector2f> CONSTANT_CODEC = RecordCodecBuilder.create(app -> app.group(
            Codec.FLOAT.fieldOf("x").forGetter(ConstantVector2f::getX),
            Codec.FLOAT.fieldOf("y").forGetter(ConstantVector2f::getY)
    ).apply(app, ConstantVector2f::new));

    public static final Codec<ConstantVector2f> CODEC = Codec.either(
            CodecUtil.VECTOR_2F_CODEC, CONSTANT_CODEC
    ).xmap(e -> e.map(ConstantVector2f::new, v -> v), Either::right);

    public ConstantVector2f(float x, float y) {
        this(new Vector2f(x, y));
    }

    public float getX() {
        return value.x;
    }

    public float getY() {
        return value.y;
    }

    @Override
    public Vector2f getValue(HittableItem item) {
        return value;
    }

    @Override
    public String getComputeType() {
        return "constant";
    }
}
