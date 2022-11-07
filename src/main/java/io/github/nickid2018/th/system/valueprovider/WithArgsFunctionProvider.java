package io.github.nickid2018.th.system.valueprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.system.valueprovider.function.UserValueFunctions;
import io.github.nickid2018.th.system.valueprovider.function.ValueFunction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Getter
@AllArgsConstructor
public abstract class WithArgsFunctionProvider<T> implements ValueProvider<T> {

    private final ValueFunction<T> function;
    private final List<ValueProvider<?>> arguments;

    @Override
    public T getValue(HittableItem item) {
        return function.compute(item, arguments);
    }

    public static <R, P extends WithArgsFunctionProvider<R>> Codec<P> codec(
            BiFunction<ValueFunction<R>, List<ValueProvider<?>>, P> ctor,
            Map<String, ValueFunction<R>> functions) {
        return RecordCodecBuilder.create(app -> app.group(
                Codec.STRING.xmap(
                        key -> UserValueFunctions.getValueProvider(key, functions), ValueFunction::getName
                ).fieldOf("function").forGetter(P::getFunction),
                ValueProvider.CODEC.listOf().fieldOf("arguments").forGetter(P::getArguments)
        ).apply(app, ctor));
    }
}
