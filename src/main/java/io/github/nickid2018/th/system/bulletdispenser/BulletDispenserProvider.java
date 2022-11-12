package io.github.nickid2018.th.system.bulletdispenser;

import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.enemy.Enemy;

public interface BulletDispenserProvider {

    BulletDispenser getDispenser(Playground playground, Enemy enemy);

    String name();
}
