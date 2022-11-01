package io.github.nickid2018.th.system.bullet.path;

import io.github.nickid2018.th.system.bullet.Bullet;
import lombok.Getter;
import org.joml.Vector2f;

public class StraightBulletPath extends BulletPath {

    @Getter
    private final float speed;
    @Getter
    private final Vector2f direction;

    private final Vector2f movePerTick;

    public StraightBulletPath(float speed, Vector2f direction) {
        this.speed = speed;
        this.direction = direction.normalize();
        movePerTick = new Vector2f(direction).mul(speed);
    }

    @Override
    public void tick(long tickTime, Bullet bullet) {
        bullet.getHitSphere().move(movePerTick.x, movePerTick.y);
        if (bullet.getPlayground().isItemOutsidePlayground(bullet))
            bullet.getPlayground().dispose(bullet);
    }
}
