package io.github.nickid2018.th.system.valueprovider.floating;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.system.valueprovider.ValueProvider;
import io.github.nickid2018.th.system.valueprovider.WithArgsFunctionProvider;
import io.github.nickid2018.th.system.valueprovider.function.ValueFunction;

import java.util.List;
import java.util.Map;

public class FunctionFloat extends WithArgsFunctionProvider<Float> implements FloatProvider {

    public static final ValueFunction<Float> RANDOM = new ValueFunction<>("random", FloatProvider.class, FloatProvider.class) {
        @Override
        protected Float getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return item.getPlayground().getRandom().nextFloat((float) arguments.get(0).getValue(item), (float) arguments.get(1).getValue(item));
        }
    };

    public FunctionFloat(ValueFunction<Float> function, List<ValueProvider<?>> arguments) {
        super(function, arguments);
    }

    public static final Map<String, ValueFunction<Float>> FUNCTIONS = ValueProvider.getFunctionMap(
            RANDOM
    );

    public static final Codec<FunctionFloat> CODEC = codec(
            FunctionFloat::new, FUNCTIONS
    );

    @Override
    public String getComputeType() {
        return "function";
    }
}
