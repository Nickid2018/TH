package io.github.nickid2018.th.system.valueprovider.integer;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.system.valueprovider.ValueFunction;
import io.github.nickid2018.th.system.valueprovider.ValueProvider;
import io.github.nickid2018.th.system.valueprovider.WithArgsFunctionProvider;

import java.util.List;

public class FunctionInt extends WithArgsFunctionProvider<Integer> implements IntProvider {

    public static final Codec<FunctionInt> CODEC = codec(FunctionInt::new, null);

    public FunctionInt(ValueFunction<Integer> function, List<ValueProvider<?>> arguments) {
        super(function, arguments);
    }

    @Override
    public String getComputeType() {
        return "function";
    }
}
