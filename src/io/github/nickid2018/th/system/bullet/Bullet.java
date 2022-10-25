package io.github.nickid2018.th.system.bullet;

import io.github.nickid2018.th.phys.Sphere;
import io.github.nickid2018.th.system.HittableItem;

public abstract class Bullet implements HittableItem {

    private final Sphere sphere;
    private long lifeTime;
    private Path path;

    public Bullet(Sphere sphere) {
        this.sphere = sphere;
    }

    @Override
    public Sphere getHitSphere() {
        return sphere;
    }

    public long getLifeTime() {
        return lifeTime;
    }

    public abstract void tick();
}
