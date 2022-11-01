package io.github.nickid2018.th.system.bullet;

import io.github.nickid2018.th.phys.Sphere;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.system.compute.Playground;
import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class Bullet implements HittableItem {

    protected final Playground playground;

    protected final Sphere sphere;

    @Getter
    protected final BulletBasicData bulletBasicData;
    @Getter
    protected final String variant;

    @Getter
    protected long lifeTime;

    @Getter
    protected float renderAngle;

    @Getter
    protected Vector3f color;

    public Bullet(Playground playground, BulletBasicData bulletBasicData, String variant, Vector2f position) {
        this.playground = playground;
        this.bulletBasicData = bulletBasicData;
        this.variant = variant;
        this.sphere = new Sphere(position, bulletBasicData.getRadius());
        lifeTime = -15;
    }

    @Override
    public Sphere getHitSphere() {
        return sphere;
    }

    public boolean similar(Bullet o) {
        return bulletBasicData.equals(o.bulletBasicData) && variant.equals(o.variant);
    }
}
