package io.github.nickid2018.th.system.bullet.path;

import io.github.nickid2018.th.system.bullet.Bullet;

public abstract class BulletPath {

    public abstract void tick(long runningTick, Bullet bullet);
}
