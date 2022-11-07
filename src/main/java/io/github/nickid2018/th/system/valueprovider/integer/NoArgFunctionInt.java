package io.github.nickid2018.th.system.valueprovider.integer;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.system.compute.HittableItem;

public class NoArgFunctionInt implements IntProvider {

    public static final Codec<? extends IntProvider> NO_ARG_CODEC = null;

    @Override
    public Integer getValue(HittableItem item) {
        return null;
    }

    @Override
    public String getComputeType() {
        return "no_arg_function";
    }
}
