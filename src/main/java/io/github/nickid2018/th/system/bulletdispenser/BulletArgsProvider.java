package io.github.nickid2018.th.system.bulletdispenser;

import io.github.nickid2018.th.system.bullet.Bullet;

import java.util.List;

public interface BulletArgsProvider {

    List<Bullet> getBullets(BulletDispenser dispenser, BulletProvider provider);

    String name();
}
