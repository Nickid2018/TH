package io.github.nickid2018.th.system.valueprovider.integer;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.system.valueprovider.ValueProvider;

public interface IntProvider extends ValueProvider<Integer> {

    Codec<IntProvider> CODEC = Codec.STRING.dispatch(
            "compute_type",
            IntProvider::getComputeType,
            IntProvider::getIntCodec);

    static Codec<? extends IntProvider> getIntCodec(String s) {
        return switch (s) {
            case "constant" -> ConstantInt.CONSTANT_CODEC;
            case "no_arg_function" -> NoArgFunctionInt.NO_ARG_CODEC;
            case "function" -> FunctionInt.CODEC;
            default -> throw new IllegalArgumentException("Unknown type: " + s);
        };
    }

    String getComputeType();

    default String getValueProviderType() {
        return "int";
    }
}
