package io.github.nickid2018.th.system.bulletdispenser;

import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.enemy.Enemy;

public class SimpleBulletDispenser extends BulletDispenser {

    public SimpleBulletDispenser(Playground playground) {
        super(playground);
    }

    public SimpleBulletDispenser(Playground playground, Enemy enemy) {
        super(playground, enemy);
    }

    @Override
    protected void dispenseBullet(long tickTime) {
        dispenseCount++;
    }
}
