package io.github.nickid2018.th.system.bulletdispenser;

import io.github.nickid2018.th.phys.Sphere;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.enemy.Enemy;
import lombok.Getter;

@Getter
public abstract class BulletDispenser implements HittableItem {

    protected final Playground playground;
    protected final Enemy enemy;

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
        if (enemy != null && enemy.isDead())
            return;
        dispenseBullet(tickTime);
    }

    @Override
    public Sphere getHitSphere() {
        return enemy == null ? new Sphere(0, 0, 0) : enemy.getHitSphere();
    }

    protected abstract void dispenseBullet(long tickTime);
}
