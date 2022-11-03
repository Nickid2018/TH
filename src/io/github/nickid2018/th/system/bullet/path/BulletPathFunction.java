package io.github.nickid2018.th.system.bullet.path;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.bullet.Bullet;
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

    private final List<Vector2f> controlPointsList = new ArrayList<>();

    private boolean filledFirstPos = false;

    public BulletPathFunction(AxisPositionFunction xFunction,
                              AxisPositionFunction yFunction,
                              List<Vector2fProvider> controlPoints) {
        this.xFunction = xFunction;
        this.yFunction = yFunction;
        this.controlPoints = controlPoints;
    }

    public Vector2f getPosition(float t, Bullet bullet) {
        if (!filledFirstPos) {
            filledFirstPos = true;
            controlPointsList.add(bullet.getHitSphere().getPosition());
            controlPoints.stream()
                    .map(v -> v.getValue(bullet))
                    .forEach(controlPointsList::add);
        }
        float x = xFunction.getValue(t, i -> controlPointsList.get(i).x);
        float y = yFunction.getValue(t, i -> controlPointsList.get(i).y);
        return new Vector2f(x, y);
    }
}
