package io.github.nickid2018.th.system.bullet.path;

import io.github.nickid2018.th.system.bullet.Bullet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FixedFunctionBulletPath extends BulletPath {

    private final BulletPathFunction function;
    private final float time;

    @Override
    public void tick(long tickTime, Bullet bullet) {
        bullet.getHitSphere().moveTo(function.getPosition(tickTime / time, bullet));
    }
}
