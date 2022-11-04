package io.github.nickid2018.th.system.valueprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.system.valueprovider.floating.ConstantFloat;
import io.github.nickid2018.th.system.valueprovider.floating.FloatProvider;
import io.github.nickid2018.th.system.valueprovider.integer.ConstantInt;
import io.github.nickid2018.th.system.valueprovider.integer.IntProvider;
import io.github.nickid2018.th.system.valueprovider.vector.ConstantVector2f;
import io.github.nickid2018.th.system.valueprovider.vector.Vector2fProvider;
import io.github.nickid2018.th.util.CodecUtil;

import java.util.HashMap;
import java.util.Map;

public interface ValueProvider<T> {

    Codec<ValueProvider<?>> CODEC = Codec.either(
            RecordCodecBuilder.<ConstantInt>create(app -> app.group(
                    Codec.INT.fieldOf("int").forGetter(ConstantInt::value) // Need extra data
            ).apply(app, ConstantInt::new)),
            Codec.either(
                    Codec.FLOAT,
                    Codec.either(
                            CodecUtil.VECTOR_2F_CODEC,
                            Codec.STRING.<ValueProvider<?>>dispatch(ValueProvider::getValueProviderType, ValueProvider::getCodec)
                    ).<ValueProvider<?>>xmap(e -> e.map(ConstantVector2f::new, v -> v), Either::right)
            ).<ValueProvider<?>>xmap(e -> e.map(ConstantFloat::new, v -> v), Either::right)
    ).xmap(e -> e.map(v -> v, v -> v), Either::right);

    static Codec<? extends ValueProvider<?>> getCodec(String s) {
        return switch (s) {
            case "int" -> IntProvider.CODEC;
            case "float" -> FloatProvider.CODEC;
            case "vec2" -> Vector2fProvider.DISPATCH_CODEC;
            default -> throw new IllegalArgumentException("Unknown type: " + s);
        };
    }

    @SafeVarargs
    static <T> Map<String, ValueFunction<T>> getFunctionMap(ValueFunction<T>... functions) {
        Map<String, ValueFunction<T>> map = new HashMap<>();
        for (ValueFunction<T> function : functions)
            map.put(function.getName(), function);
        return map;
    }

    String getValueProviderType();

    T getValue(HittableItem item);
}
