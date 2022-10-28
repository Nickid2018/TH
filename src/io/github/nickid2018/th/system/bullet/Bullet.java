package io.github.nickid2018.th.system.bullet;

import io.github.nickid2018.th.phys.Sphere;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.system.compute.Playground;

public abstract class Bullet implements HittableItem {

    protected final Playground playground;
    protected final Sphere sphere;
    protected long lifeTime;

    public Bullet(Playground playground, Sphere sphere) {
        this.playground = playground;
        this.sphere = sphere;
        lifeTime = 0;
    }

    @Override
    public Sphere getHitSphere() {
        return sphere;
    }

    public long getLifeTime() {
        return lifeTime;
    }
}
