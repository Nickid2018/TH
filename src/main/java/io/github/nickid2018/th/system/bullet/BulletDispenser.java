package io.github.nickid2018.th.system.bullet;

import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.compute.Tickable;
import io.github.nickid2018.th.system.enemy.Enemy;

public abstract class BulletDispenser implements Tickable {

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

    protected abstract void dispenseBullet(long tickTime);
}
