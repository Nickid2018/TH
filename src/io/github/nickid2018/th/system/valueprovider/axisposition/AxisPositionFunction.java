package io.github.nickid2018.th.system.valueprovider.axisposition;

import io.github.nickid2018.th.system.valueprovider.IntToFloatFunction;
import org.joml.Math;

public interface AxisPositionFunction {

    float getValue(float t, IntToFloatFunction arguments);

    AxisPositionFunction LINEAR = (t, args) -> Math.lerp(args.get(0), args.get(1), t);
    AxisPositionFunction QUADRATIC = (t, args) -> Math.lerp(args.get(0), args.get(1), t * t);
    AxisPositionFunction CUBIC = (t, args) -> Math.lerp(args.get(0), args.get(1), t * t * t);
    AxisPositionFunction QUARTIC = (t, args) -> Math.lerp(args.get(0), args.get(1), t * t * t * t);
    AxisPositionFunction INV_QUADRATIC = (t, args) -> Math.lerp(args.get(1), args.get(0), 1 - t * t);
    AxisPositionFunction INV_CUBIC = (t, args) -> Math.lerp(args.get(1), args.get(0), 1 - t * t * t);
    AxisPositionFunction INV_QUARTIC = (t, args) -> Math.lerp(args.get(1), args.get(0), 1 - t * t * t * t);

    AxisPositionFunction BEZIER_2 = (t, args) -> Math.lerp(
            Math.lerp(args.get(0), args.get(1), t),
            Math.lerp(args.get(1), args.get(2), t),
            t);
    AxisPositionFunction BEZIER_3 = (t, args) -> Math.lerp(
            Math.lerp(
                    Math.lerp(args.get(0), args.get(1), t),
                    Math.lerp(args.get(1), args.get(2), t),
                    t),
            Math.lerp(
                    Math.lerp(args.get(1), args.get(2), t),
                    Math.lerp(args.get(2), args.get(3), t),
                    t),
            t);
}
