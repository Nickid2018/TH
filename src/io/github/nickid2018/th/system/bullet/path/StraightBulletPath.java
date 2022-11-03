package io.github.nickid2018.th.system.bullet.path;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.system.valueprovider.floating.FloatProvider;
import io.github.nickid2018.th.system.valueprovider.vector.Vector2fProvider;
import lombok.Getter;
import org.joml.Vector2f;

public class StraightBulletPath extends BulletPath {

    public static final Codec<StraightBulletPath> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("continueTime").orElse(-1).forGetter(StraightBulletPath::getContinueTime),
            FloatProvider.CODEC.fieldOf("speed").forGetter(StraightBulletPath::getSpeed),
            Vector2fProvider.CODEC.fieldOf("direction").forGetter(StraightBulletPath::getDirection)
    ).apply(instance, StraightBulletPath::new));

    @Getter
    private final int continueTime;
    @Getter
    private final FloatProvider speed;
    @Getter
    private final Vector2fProvider direction;

    private Vector2f movePerTick;

    public StraightBulletPath(int continueTime, FloatProvider speed, Vector2fProvider direction) {
        this.continueTime = continueTime;
        this.speed = speed;
        this.direction = direction;
    }

    @Override
    public void tick(long tickTime, Bullet bullet) {
        if (continueTime > 0 && tickTime > continueTime)
            return;
        if (movePerTick == null)
            movePerTick = direction.getValue(bullet).normalize(speed.getValue(bullet));
        bullet.getHitSphere().move(movePerTick.x, movePerTick.y);
        if (bullet.getPlayground().isItemOutsidePlayground(bullet))
            bullet.getPlayground().dispose(bullet);
    }
}
