package io.github.nickid2018.th.system.bulletdispenser;

import io.github.nickid2018.th.phys.Sphere;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.enemy.Enemy;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class BulletDispenser implements HittableItem {

    protected final Playground playground;
    protected final Enemy enemy;

    protected int lifeTime = -1;
    protected int dispenseStep = 0;
    @Setter
    protected int dispenseInterval = 5;

    public BulletDispenser(Playground playground) {
        this.playground = playground;
        enemy = null;
    }

    public BulletDispenser(Playground playground, Enemy enemy) {
        this.playground = playground;
        this.enemy = enemy;
    }

    @Override
    public void tick(long tickTime) {
        lifeTime++;
        if (dispenseInterval != 0 && lifeTime % dispenseInterval != 0)
            return;
        if (enemy != null && enemy.isDead()) {
            playground.dispose(this);
            return;
        }
        dispenseBullet(tickTime);
        dispenseStep++;
    }

    @Override
    public Sphere getHitSphere() {
        return enemy == null ? new Sphere(0, 0, 0) : enemy.getHitSphere();
    }

    protected abstract void dispenseBullet(long tickTime);
}
