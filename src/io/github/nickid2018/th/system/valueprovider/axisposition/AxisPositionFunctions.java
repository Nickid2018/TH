package io.github.nickid2018.th.system.valueprovider.axisposition;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class AxisPositionFunctions {

    public static final Codec<AxisPositionFunction> REF_CODEC =
            ResourceLocation.CODEC.xmap(AxisPositionFunctions::createValueFunction, AxisPositionFunctions::getLocation);

    private static final Map<ResourceLocation, AxisPositionFunction> VALUE_FUNCTION_MAP = new HashMap<>();

    public static void registerValueFunction(ResourceLocation location, AxisPositionFunction function) {
        VALUE_FUNCTION_MAP.put(location.normalize(), function);
    }

    public static AxisPositionFunction getValueFunction(ResourceLocation location) {
        return VALUE_FUNCTION_MAP.get(location);
    }

    public static AxisPositionFunction createValueFunction(ResourceLocation location) {
        AxisPositionFunction function = getValueFunction(location);
        if (function != null)
            return function;
        function = new UserDefinedAxisPositionFunction(location);
        registerValueFunction(location, function);
        return function;
    }

    public static ResourceLocation getLocation(AxisPositionFunction function) {
        for (Map.Entry<ResourceLocation, AxisPositionFunction> entry : VALUE_FUNCTION_MAP.entrySet()) {
            if (entry.getValue() == function)
                return entry.getKey();
        }
        return null;
    }

    static {
        registerValueFunction(ResourceLocation.fromString("internal:linear"), AxisPositionFunction.LINEAR);
        registerValueFunction(ResourceLocation.fromString("internal:quadratic"), AxisPositionFunction.QUADRATIC);
        registerValueFunction(ResourceLocation.fromString("internal:cubic"), AxisPositionFunction.CUBIC);
        registerValueFunction(ResourceLocation.fromString("internal:quartic"), AxisPositionFunction.QUARTIC);
        registerValueFunction(ResourceLocation.fromString("internal:inv_quadratic"), AxisPositionFunction.INV_QUADRATIC);
        registerValueFunction(ResourceLocation.fromString("internal:inv_cubic"), AxisPositionFunction.INV_CUBIC);
        registerValueFunction(ResourceLocation.fromString("internal:inv_quartic"), AxisPositionFunction.INV_QUARTIC);
        registerValueFunction(ResourceLocation.fromString("internal:bezier_2"), AxisPositionFunction.BEZIER_2);
        registerValueFunction(ResourceLocation.fromString("internal:bezier_3"), AxisPositionFunction.BEZIER_3);
        registerValueFunction(ResourceLocation.fromString("internal:polynomial"), AxisPositionFunction.POLYNOMIAL);
        registerValueFunction(ResourceLocation.fromString("internal:polynomial_lerp"), AxisPositionFunction.POLYNOMIAL_LERP);
        registerValueFunction(ResourceLocation.fromString("internal:sin"), AxisPositionFunction.SIN);
        registerValueFunction(ResourceLocation.fromString("internal:cos"), AxisPositionFunction.COS);
    }
}
