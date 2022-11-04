package io.github.nickid2018.th.system.valueprovider.vector;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.github.nickid2018.th.system.valueprovider.ValueProvider;
import io.github.nickid2018.th.util.CodecUtil;
import org.joml.Vector2f;

public interface Vector2fProvider extends ValueProvider<Vector2f> {

    Codec<Vector2fProvider> DISPATCH_CODEC = Codec.STRING.dispatch(
            "compute_type",
            Vector2fProvider::getComputeType,
            Vector2fProvider::getVector2fCodec);

    Codec<Vector2fProvider> CODEC = Codec.either(
            CodecUtil.VECTOR_2F_CODEC, DISPATCH_CODEC
    ).xmap(e -> e.map(ConstantVector2f::new, v -> v), Either::right);

    static Codec<? extends Vector2fProvider> getVector2fCodec(String s) {
        return switch (s) {
            case "constant" -> ConstantVector2f.CONSTANT_CODEC;
            case "no_arg_function" -> NoArgFunctionVector2f.NO_ARG_CODEC;
            case "function" -> FunctionVector2f.CODEC;
            default -> throw new IllegalArgumentException("Unknown type: " + s);
        };
    }

    String getComputeType();

    @Override
    default String getValueProviderType() {
        return "vec2";
    }
}
