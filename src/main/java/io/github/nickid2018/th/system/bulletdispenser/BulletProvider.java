package io.github.nickid2018.th.system.bulletdispenser;

import io.github.nickid2018.th.system.bullet.Bullet;
import org.joml.Vector2f;

public interface BulletProvider {

    Bullet getBullet(BulletDispenser dispenser, Vector2f position);
}
