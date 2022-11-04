package io.github.nickid2018.th.system.valueprovider.vector;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.system.valueprovider.ValueFunction;
import io.github.nickid2018.th.system.valueprovider.ValueProvider;
import io.github.nickid2018.th.system.valueprovider.WithArgsFunctionProvider;
import io.github.nickid2018.th.system.valueprovider.floating.FloatProvider;
import org.joml.Math;
import org.joml.Vector2f;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class FunctionVector2f extends WithArgsFunctionProvider<Vector2f> implements Vector2fProvider {

    public static final ValueFunction<Vector2f> WITH_ANGLE = new ValueFunction<>("with_angle", FloatProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            float angle = (float) arguments.get(0).getValue(item);
            return new Vector2f(Math.cos(angle), Math.sin(angle));
        }
    };

    public static final ValueFunction<Vector2f> VEC2_1 = new ValueFunction<>("vec2_1", FloatProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            float x = (float) arguments.get(0).getValue(item);
            return new Vector2f(x);
        }
    };

    public static final ValueFunction<Vector2f> NEGATE = new ValueFunction<>("negate", Vector2fProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).negate();
        }
    };

    public static final ValueFunction<Vector2f> NORMALIZE = new ValueFunction<>("normalize", Vector2fProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).normalize();
        }
    };

    public static final ValueFunction<Vector2f> ABSOLUTE = new ValueFunction<>("abs", Vector2fProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).absolute();
        }
    };

    public static final ValueFunction<Vector2f> FLOOR = new ValueFunction<>("floor", Vector2fProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).floor();
        }
    };

    public static final ValueFunction<Vector2f> CEIL = new ValueFunction<>("ceil", Vector2fProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).ceil();
        }
    };

    public static final ValueFunction<Vector2f> ROUND = new ValueFunction<>("round", Vector2fProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).round();
        }
    };

    public static final ValueFunction<Vector2f> PERPENDICULAR = new ValueFunction<>("perpendicular", Vector2fProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).perpendicular();
        }
    };

    public static final ValueFunction<Vector2f> VEC2_2 = new ValueFunction<>("vec2_2", FloatProvider.class, FloatProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return new Vector2f((float) arguments.get(0).getValue(item),
                    (float) arguments.get(1).getValue(item));
        }
    };

    public static final ValueFunction<Vector2f> ADD = new ValueFunction<>(
            "add", Vector2fProvider.class, Vector2fProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).add(
                    (Vector2f) arguments.get(1).getValue(item));
        }
    };

    public static final ValueFunction<Vector2f> SUBTRACT = new ValueFunction<>(
            "subtract", Vector2fProvider.class, Vector2fProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).sub(
                    (Vector2f) arguments.get(1).getValue(item));
        }
    };

    public static final ValueFunction<Vector2f> MULTIPLY = new ValueFunction<>(
            "multiply", Vector2fProvider.class, Vector2fProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).mul(
                    (Vector2f) arguments.get(1).getValue(item));
        }
    };

    public static final ValueFunction<Vector2f> DIVIDE = new ValueFunction<>(
            "divide", Vector2fProvider.class, Vector2fProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).div(
                    (Vector2f) arguments.get(1).getValue(item));
        }
    };

    public static final ValueFunction<Vector2f> NUMBER_MULTIPLY = new ValueFunction<>(
            "number_mul", Vector2fProvider.class, FloatProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).mul(
                    (float) arguments.get(1).getValue(item));
        }
    };

    public static final ValueFunction<Vector2f> NUMBER_DIVIDE = new ValueFunction<>(
            "number_div", Vector2fProvider.class, FloatProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).div(
                    (float) arguments.get(1).getValue(item));
        }
    };

    public static final ValueFunction<Vector2f> MIN = new ValueFunction<>(
            "min", Vector2fProvider.class, Vector2fProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).min(
                    (Vector2f) arguments.get(1).getValue(item));
        }
    };

    public static final ValueFunction<Vector2f> MAX = new ValueFunction<>(
            "max", Vector2fProvider.class, Vector2fProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).max(
                    (Vector2f) arguments.get(1).getValue(item));
        }
    };

    public static final ValueFunction<Vector2f> FMA = new ValueFunction<>(
            "fma", Vector2fProvider.class, Vector2fProvider.class, Vector2fProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).fma(
                    (Vector2f) arguments.get(1).getValue(item),
                    (Vector2f) arguments.get(2).getValue(item));
        }
    };

    public static final ValueFunction<Vector2f> LERP = new ValueFunction<>(
            "lerp", Vector2fProvider.class, Vector2fProvider.class, FloatProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).lerp(
                    (Vector2f) arguments.get(1).getValue(item),
                    (float) arguments.get(2).getValue(item));
        }
    };

    public static final Map<String, ValueFunction<Vector2f>> FUNCTIONS = ValueProvider.getFunctionMap(
            WITH_ANGLE, VEC2_1, NEGATE, NORMALIZE, ABSOLUTE, FLOOR, CEIL, ROUND, PERPENDICULAR, // 1 argument
            VEC2_2, ADD, SUBTRACT, MULTIPLY, DIVIDE, NUMBER_MULTIPLY, NUMBER_DIVIDE, MIN, MAX, // 2 arguments
            FMA, LERP // 3 arguments
    );

    public static final Codec<FunctionVector2f> CODEC = codec(
            FunctionVector2f::new, FUNCTIONS
    );

    public FunctionVector2f(ValueFunction<Vector2f> function, List<ValueProvider<?>> arguments) {
        super(function, arguments);
    }

    @Override
    public String getComputeType() {
        return "function";
    }
}
