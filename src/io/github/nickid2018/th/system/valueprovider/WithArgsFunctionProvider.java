package io.github.nickid2018.th.system.valueprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.compute.HittableItem;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class WithArgsFunctionProvider<T> implements ValueProvider<T> {

    @Getter
    private final ValueFunction<T> function;
    @Getter
    private final List<ValueProvider<?>> arguments;

    public WithArgsFunctionProvider(ValueFunction<T> function, List<ValueProvider<?>> arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    @Override
    public T getValue(HittableItem item) {
        return function.compute(item, arguments);
    }

    @SafeVarargs
    public static <R, P extends WithArgsFunctionProvider<R>> Codec<P> codec(
            BiFunction<ValueFunction<R>, List<ValueProvider<?>>, P> ctor,
            ValueFunction<R>... functions) {
        Map<String, ValueFunction<R>> variables = new HashMap<>();
        for (ValueFunction<R> function : functions)
            variables.put(function.getName(), function);
        return RecordCodecBuilder.create(app -> app.group(
                Codec.STRING.xmap(variables::get, ValueFunction::getName)
                        .fieldOf("function").forGetter(P::getFunction),
                ValueProvider.CODEC.listOf().fieldOf("arguments").forGetter(P::getArguments)
        ).apply(app, ctor));
    }
}
