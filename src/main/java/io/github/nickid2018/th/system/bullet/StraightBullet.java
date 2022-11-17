package io.github.nickid2018.th.system.bullet;

import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.valueprovider.floating.FloatProvider;
import io.github.nickid2018.th.system.valueprovider.vector.Vector2fProvider;
import org.joml.Vector2f;

public class StraightBullet extends Bullet {

    private final Vector2f speed;

    public StraightBullet(Playground playground, BulletBasicData bulletBasicData,
                          String variant, Vector2f position,
                          FloatProvider speed, Vector2fProvider direction) {
        super(playground, bulletBasicData, variant, position);
        this.speed = direction.getValue(this).mul(speed.getValue(this));
        angle = this.speed.angle(Bullet.POSITIVE_X);
    }

    public StraightBullet(Playground playground, BulletBasicData bulletBasicData,
                          String variant, Vector2f position,
                          float speed, Vector2f direction) {
        super(playground, bulletBasicData, variant, position);
        this.speed = direction.mul(speed);
        angle = this.speed.angle(Bullet.POSITIVE_X);
    }

    @Override
    public void tick(long tickTime) {
        getHitSphere().move(speed.x, speed.y);
        super.tick(tickTime);
    }
}
