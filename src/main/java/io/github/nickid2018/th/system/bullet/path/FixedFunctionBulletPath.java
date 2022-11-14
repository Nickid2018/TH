package io.github.nickid2018.th.system.bullet.path;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.bullet.PathControllingBullet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joml.Vector2f;

@Getter
@AllArgsConstructor
public class FixedFunctionBulletPath extends BulletPath {

    public static final Codec<FixedFunctionBulletPath> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("time").forGetter(FixedFunctionBulletPath::getTime),
            BulletPathFunction.CODEC.fieldOf("function").forGetter(FixedFunctionBulletPath::getFunction)
    ).apply(instance, FixedFunctionBulletPath::new));

    private final float time;
    private final BulletPathFunction function;

    @Override
    public float tick(long tickTime, PathControllingBullet bullet) {
        Vector2f lastPos = bullet.getHitSphere().getPosition();
        float t = tickTime / time;
        if (t > 1 && bullet.getPlayground().isItemOutsidePlayground(bullet))
            bullet.getPlayground().dispose(bullet);
        else
            bullet.getHitSphere().moveTo(function.getPosition(t, bullet));
        Vector2f pos = bullet.getHitSphere().getPosition();
        return (float) Math.atan2(pos.y - lastPos.y, pos.x - lastPos.x);
    }

    @Override
    public Object[] createArguments(PathControllingBullet bullet) {
        return function.createArguments(bullet);
    }

    @Override
    public String getComputeType() {
        return "fixed_path";
    }
}
