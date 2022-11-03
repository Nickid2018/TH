package io.github.nickid2018.th.system.valueprovider;

import org.joml.Math;

public interface AxisPositionFunction {

    float getValue(float t, IntToFloatFunction arguments);

    AxisPositionFunction LINEAR = (t, args) -> Math.lerp(args.applyAsFloat(0), args.applyAsFloat(1), t);
    AxisPositionFunction QUADRATIC = (t, args) -> Math.lerp(args.applyAsFloat(0), args.applyAsFloat(1), t * t);
    AxisPositionFunction CUBIC = (t, args) -> Math.lerp(args.applyAsFloat(0), args.applyAsFloat(1), t * t * t);
    AxisPositionFunction QUARTIC = (t, args) -> Math.lerp(args.applyAsFloat(0), args.applyAsFloat(1), t * t * t * t);
    AxisPositionFunction INV_QUADRATIC = (t, args) -> Math.lerp(args.applyAsFloat(1), args.applyAsFloat(0), 1 - t * t);
    AxisPositionFunction INV_CUBIC = (t, args) -> Math.lerp(args.applyAsFloat(1), args.applyAsFloat(0), 1 - t * t * t);
    AxisPositionFunction INV_QUARTIC = (t, args) -> Math.lerp(args.applyAsFloat(1), args.applyAsFloat(0), 1 - t * t * t * t);

    AxisPositionFunction BEZIER_2 = (t, args) -> Math.lerp(
            Math.lerp(args.applyAsFloat(0), args.applyAsFloat(1), t),
            Math.lerp(args.applyAsFloat(1), args.applyAsFloat(2), t),
            t);
    AxisPositionFunction BEZIER_3 = (t, args) -> Math.lerp(
            Math.lerp(
                    Math.lerp(args.applyAsFloat(0), args.applyAsFloat(1), t),
                    Math.lerp(args.applyAsFloat(1), args.applyAsFloat(2), t),
                    t),
            Math.lerp(
                    Math.lerp(args.applyAsFloat(1), args.applyAsFloat(2), t),
                    Math.lerp(args.applyAsFloat(2), args.applyAsFloat(3), t),
                    t),
            t);
}
