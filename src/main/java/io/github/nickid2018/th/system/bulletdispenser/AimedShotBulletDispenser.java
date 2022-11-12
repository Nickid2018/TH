package io.github.nickid2018.th.system.bulletdispenser;

import io.github.nickid2018.th.system.bullet.BulletBasicData;
import io.github.nickid2018.th.system.bullet.PathControllingBullet;
import io.github.nickid2018.th.system.bullet.path.BulletPath;
import io.github.nickid2018.th.system.bullet.path.StraightBulletPath;
import io.github.nickid2018.th.system.bulletdispenser.variant.BulletVariantProvider;
import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.enemy.Enemy;
import io.github.nickid2018.th.system.valueprovider.floating.FloatProvider;
import io.github.nickid2018.th.system.valueprovider.integer.IntProvider;
import io.github.nickid2018.th.system.valueprovider.vector.ConstantVector2f;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
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
        float baseAngle = (float) Math.atan2(base.y, base.x);
        baseAngle -= maxAngleValue / 2;
        for (int i = 0; i < waysValue; i++) {
            float angle = baseAngle + this.angle * i;
            Vector2f dir = new Vector2f((float) Math.cos(angle), (float) Math.sin(angle));
            String variant = provider.getVariant(this);
            BulletBasicData data = provider.getBulletBasicData(this);
            StraightBulletPath path = new StraightBulletPath(-1, speed, new ConstantVector2f(dir));
            Long2ObjectAVLTreeMap<BulletPath> map = new Long2ObjectAVLTreeMap<>();
            map.put(0L, path);
            PathControllingBullet bullet = new PathControllingBullet(playground, data, variant, thisPos, map);
            bullet.setAngle(angle);
            playground.addBullet(bullet);
        }
    }
}
