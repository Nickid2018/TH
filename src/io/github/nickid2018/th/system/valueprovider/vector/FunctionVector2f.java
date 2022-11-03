package io.github.nickid2018.th.system.valueprovider.vector;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.system.valueprovider.ValueFunction;
import io.github.nickid2018.th.system.valueprovider.ValueProvider;
import io.github.nickid2018.th.system.valueprovider.WithArgsFunctionProvider;
import io.github.nickid2018.th.system.valueprovider.floating.FloatProvider;
import org.joml.Vector2f;
import org.joml.Math;

import java.util.List;

@SuppressWarnings("unchecked")
public class FunctionVector2f extends WithArgsFunctionProvider<Vector2f> implements Vector2fProvider {

    public static final ValueFunction<Vector2f> WITH_ANGLE = new ValueFunction<>("with_angle", FloatProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            float angle = (float) arguments.get(0).getValue(item);
            return new Vector2f(Math.cos(angle), Math.sin(angle));
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

    public static final ValueFunction<Vector2f> ADD = new ValueFunction<>(
            "add", Vector2fProvider.class, Vector2fProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).add((Vector2f) arguments.get(1).getValue(item));
        }
    };

    public static final ValueFunction<Vector2f> SUBTRACT = new ValueFunction<>(
            "subtract", Vector2fProvider.class, Vector2fProvider.class) {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return ((Vector2f) arguments.get(0).getValue(item)).sub((Vector2f) arguments.get(1).getValue(item));
        }
    };

    public static final Codec<FunctionVector2f> CODEC = WithArgsFunctionProvider.codec(
            FunctionVector2f::new,
            WITH_ANGLE, NEGATE, NORMALIZE, ABSOLUTE, FLOOR, CEIL, ROUND, PERPENDICULAR, // 1 argument
            ADD, SUBTRACT // 2 arguments
    );

    public FunctionVector2f(ValueFunction<Vector2f> function, List<ValueProvider<?>> arguments) {
        super(function, arguments);
    }

    @Override
    public String getComputeType() {
        return "function";
    }
}
