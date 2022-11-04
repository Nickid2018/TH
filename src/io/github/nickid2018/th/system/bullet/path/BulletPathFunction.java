package io.github.nickid2018.th.system.bullet.path;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.bullet.PathControllingBullet;
import io.github.nickid2018.th.system.valueprovider.AxisPositionFunction;
import io.github.nickid2018.th.system.valueprovider.AxisPositionFunctions;
import io.github.nickid2018.th.system.valueprovider.vector.Vector2fProvider;
import lombok.Getter;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BulletPathFunction {

    public static final Codec<BulletPathFunction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AxisPositionFunctions.CODEC.fieldOf("x_function").forGetter(BulletPathFunction::getXFunction),
            AxisPositionFunctions.CODEC.fieldOf("y_function").forGetter(BulletPathFunction::getYFunction),
            Vector2fProvider.CODEC.listOf().fieldOf("control_points").forGetter(BulletPathFunction::getControlPoints)
    ).apply(instance, BulletPathFunction::new));

    private final AxisPositionFunction xFunction;
    private final AxisPositionFunction yFunction;
    private final List<Vector2fProvider> controlPoints;

    public BulletPathFunction(AxisPositionFunction xFunction,
                              AxisPositionFunction yFunction,
                              List<Vector2fProvider> controlPoints) {
        this.xFunction = xFunction;
        this.yFunction = yFunction;
        this.controlPoints = controlPoints;
    }

    public Object[] createArguments(PathControllingBullet bullet) {
        List<Vector2f> list = new ArrayList<>();
        list.add(bullet.getHitSphere().getPosition());
        for (Vector2fProvider provider : controlPoints)
            list.add(provider.getValue(bullet));
        return list.toArray();
    }

    public Vector2f getPosition(float t, PathControllingBullet bullet) {
        float x = xFunction.getValue(t, i -> ((Vector2f) bullet.getStoredArgs()[i]).x);
        float y = yFunction.getValue(t, i -> ((Vector2f) bullet.getStoredArgs()[i]).y);
        return new Vector2f(x, y);
    }
}
