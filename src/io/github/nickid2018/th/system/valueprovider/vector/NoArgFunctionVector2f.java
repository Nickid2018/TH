package io.github.nickid2018.th.system.valueprovider.vector;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.system.valueprovider.NoArgFunction;
import io.github.nickid2018.th.system.valueprovider.ValueFunction;
import io.github.nickid2018.th.system.valueprovider.ValueProvider;
import org.joml.Math;
import org.joml.Vector2f;

import java.util.List;
import java.util.Map;

public class NoArgFunctionVector2f extends NoArgFunction<Vector2f> implements Vector2fProvider {

    public static final ValueFunction<Vector2f> ZERO = new ValueFunction<>("zero") {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return new Vector2f();
        }
    };

    public static final ValueFunction<Vector2f> RANDOM = new ValueFunction<>("random") {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            float angle = (float) (Math.random() * 2 * Math.PI);
            return new Vector2f(Math.cos(angle), Math.sin(angle));
        }
    };

    public static final ValueFunction<Vector2f> THIS_POSITION = new ValueFunction<>("this") {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return new Vector2f(item.getHitSphere().getPosition());
        }
    };

    public static final ValueFunction<Vector2f> PLAYER_POSITION = new ValueFunction<>("player") {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            return new Vector2f(item.getPlayground().getPlayer().getHitSphere().getPosition());
        }
    };

    public static final ValueFunction<Vector2f> BULLET_ANGLE = new ValueFunction<>("bullet_angle") {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            if (item instanceof Bullet bullet) {
                float angle = bullet.getAngle();
                return new Vector2f(Math.cos(angle), Math.sin(angle));
            } else
                throw new IllegalArgumentException("The item is not a bullet");
        }
    };

    public static final ValueFunction<Vector2f> TO_PLAYER_VEC = new ValueFunction<>("to_player") {
        @Override
        protected Vector2f getValue(HittableItem item, List<ValueProvider<?>> arguments) {
            Vector2f me = item.getHitSphere().getPosition();
            Vector2f player = item.getPlayground().getPlayer().getHitSphere().getPosition();
            return player.sub(me);
        }
    };

    public static final Map<String, ValueFunction<Vector2f>> FUNCTIONS = ValueProvider.getFunctionMap(
            ZERO, RANDOM, THIS_POSITION, PLAYER_POSITION, BULLET_ANGLE, TO_PLAYER_VEC
    );

    public static final Codec<NoArgFunctionVector2f> NO_ARG_CODEC = noArgCodec(NoArgFunctionVector2f::new, FUNCTIONS);

    public static final Codec<NoArgFunctionVector2f> CODEC = codec(NO_ARG_CODEC, NoArgFunctionVector2f::new, FUNCTIONS);

    public NoArgFunctionVector2f(ValueFunction<Vector2f> actionKey) {
        super(actionKey);
    }

    @Override
    public String getComputeType() {
        return "no_arg_function";
    }
}
