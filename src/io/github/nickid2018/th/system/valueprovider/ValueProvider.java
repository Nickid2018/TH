package io.github.nickid2018.th.system.valueprovider;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.system.valueprovider.floating.ConstantFloat;
import io.github.nickid2018.th.system.valueprovider.floating.FloatProvider;
import io.github.nickid2018.th.system.valueprovider.integer.ConstantInt;
import io.github.nickid2018.th.system.valueprovider.integer.IntProvider;
import io.github.nickid2018.th.system.valueprovider.vector.ConstantVector2f;
import io.github.nickid2018.th.system.valueprovider.vector.Vector2fProvider;
import io.github.nickid2018.th.util.CodecUtil;

public interface ValueProvider<T> {

    Codec<ValueProvider<?>> CODEC = Codec.either(
            Codec.INT,
            Codec.either(
                    Codec.FLOAT,
                    Codec.either(
                            CodecUtil.VECTOR_2F_CODEC,
                            Codec.STRING.<ValueProvider<?>>dispatch(ValueProvider::getValueProviderType, ValueProvider::getCodec)
                    ).<ValueProvider<?>>xmap(e -> e.map(ConstantVector2f::new, v -> v), Either::right)
            ).<ValueProvider<?>>xmap(e -> e.map(ConstantFloat::new, v -> v), Either::right)
    ).<ValueProvider<?>>xmap(e -> e.map(ConstantInt::new, v -> v), Either::right);

    static Codec<? extends ValueProvider<?>> getCodec(String s) {
        return switch (s) {
            case "int" -> IntProvider.CODEC;
            case "float" -> FloatProvider.CODEC;
            case "vec2" -> Vector2fProvider.CODEC;
            default -> throw new IllegalArgumentException("Unknown type: " + s);
        };
    }

    String getValueProviderType();

    T getValue(HittableItem item);

    static void main(String[] args) {
        String json = """
                {
                    "type": "vec2",
                    "compute_type": "function",
                    "function": "add",
                    "arguments": [
                        {
                            "type": "vec2",
                            "compute_type": "constant",
                            "x": 1,
                            "y": 2
                        },
                        {
                            "type": "vec2",
                            "compute_type": "function",
                            "function": "normalize",
                            "arguments": [
                                {
                                    "type": "vec2",
                                    "compute_type": "constant",
                                    "x": 3,
                                    "y": 4
                                }
                            ]
                        }
                    ]
                }
                """;
        JsonElement element = JsonParser.parseString(json);
        ValueProvider<?> provider = CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, element))
                .getOrThrow(false, System.out::println);
        System.out.println(provider.getValue(null));
    }
}
