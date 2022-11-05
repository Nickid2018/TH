package io.github.nickid2018.th.system.valueprovider.function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.system.valueprovider.ValueProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public abstract class NoArgFunction<T> implements ValueProvider<T> {

    private final ValueFunction<T> actionKey;

    public static final List<ValueProvider<?>> EMPTY_LIST = List.of();

    @Override
    public T getValue(HittableItem item) {
        return actionKey.compute(item, EMPTY_LIST);
    }

    public static <R, P extends NoArgFunction<R>> Codec<P> noArgCodec(
            Function<ValueFunction<R>, P> ctor,
            Map<String, ValueFunction<R>> functions) {
        return RecordCodecBuilder.create(app -> app.group(
                Codec.STRING.xmap(functions::get, ValueFunction::getName)
                        .fieldOf("action").forGetter(P::getActionKey)
        ).apply(app, ctor));
    }

    public static <R, P extends NoArgFunction<R>> Codec<P> codec(
            Codec<P> noArgCodec,
            Function<ValueFunction<R>, P> ctor,
            Map<String, ValueFunction<R>> functions) {
        return Codec.either(
                Codec.STRING.xmap(functions::get, ValueFunction::getName), noArgCodec
        ).xmap(e -> e.map(ctor, v -> v), Either::right);
    }
}
