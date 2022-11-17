package io.github.nickid2018.th.system.bulletdispenser;

import io.github.nickid2018.th.system.bullet.BulletBasicData;
import io.github.nickid2018.th.system.bullet.StraightBullet;
import io.github.nickid2018.th.system.bulletdispenser.variant.BulletVariantProvider;
import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.enemy.Enemy;
import io.github.nickid2018.th.system.valueprovider.floating.FloatProvider;
import io.github.nickid2018.th.system.valueprovider.integer.IntProvider;
import lombok.Getter;
import org.joml.Vector2f;

@Getter
public class RoundShotBulletDispenser extends BulletDispenser {

    private final BulletVariantProvider provider;

    private final FloatProvider radius;
    private final IntProvider ways;
    private final FloatProvider speed;
    private final FloatProvider initialAngle;

    private final int waysValue;
    private final float wayAngle;


    public RoundShotBulletDispenser(Playground playground, BulletVariantProvider provider, FloatProvider radius,
                                    IntProvider ways, FloatProvider speed, FloatProvider initialAngle, Enemy enemy) {
        super(playground, enemy);
        this.provider = provider;
        this.radius = radius;
        this.ways = ways;
        this.speed = speed;
        this.initialAngle = initialAngle;
        this.waysValue = ways.getValue(this);
        this.wayAngle = (float) (Math.PI / waysValue) * 2;
    }

    @Override
    protected void dispenseBullet(long tickTime) {
        float angle = initialAngle.getValue(this);
        float radius = this.radius.getValue(this);
        float speed = this.speed.getValue(this);
        Vector2f thisPos = getHitSphere().getPosition();
        for (int i = 0; i < waysValue; i++) {
            Vector2f dir = new Vector2f((float) Math.cos(angle), (float) Math.sin(angle));
            Vector2f offset = new Vector2f();
            dir.mul(radius, offset);
            thisPos.add(offset, offset);
            String variant = provider.getVariant(this);
            BulletBasicData data = provider.getBulletBasicData(this);
            StraightBullet bullet = new StraightBullet(playground, data, variant, offset, speed, dir);
            if (provider.hasDefinedPriority(this))
                bullet.setPriority(provider.getPriority(this));
            playground.addBullet(bullet);
            angle += wayAngle;
        }
    }
}
