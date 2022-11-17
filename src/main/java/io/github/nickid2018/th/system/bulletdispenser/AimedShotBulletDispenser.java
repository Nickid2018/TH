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
public class AimedShotBulletDispenser extends BulletDispenser {

    private final BulletVariantProvider provider;

    private final IntProvider ways;
    private final FloatProvider maxAngle;
    private final FloatProvider speed;

    private final int waysValue;
    private final float maxAngleValue;

    private final float angle;

    public AimedShotBulletDispenser(Playground playground, BulletVariantProvider provider,
                                    IntProvider ways, FloatProvider maxAngle,
                                    FloatProvider speed, Enemy enemy) {
        super(playground, enemy);
        this.provider = provider;
        this.ways = ways;
        this.maxAngle = maxAngle;
        this.speed = speed;
        waysValue = ways.getValue(this);
        maxAngleValue = maxAngle.getValue(this);
        angle = maxAngleValue / (waysValue - 1);
    }

    @Override
    protected void dispenseBullet(long tickTime) {
        Vector2f thisPos = getHitSphere().getPosition();
        Vector2f base = playground.getPlayer().getHitSphere().getPosition().sub(thisPos);
        float speed = this.speed.getValue(this);
        float baseAngle = (float) Math.atan2(base.y, base.x);
        baseAngle -= maxAngleValue / 2;
        for (int i = 0; i < waysValue; i++) {
            float angle = baseAngle + this.angle * i;
            Vector2f dir = new Vector2f((float) Math.cos(angle), (float) Math.sin(angle));
            BulletBasicData data = provider.getBulletBasicData(this);
            String variant = provider.getVariant(this);
            StraightBullet bullet = new StraightBullet(playground, data, variant, thisPos, speed, dir);
            if (provider.hasDefinedPriority(this))
                bullet.setPriority(provider.getPriority(this));
            playground.addBullet(bullet);
        }
    }
}
