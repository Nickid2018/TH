package io.github.nickid2018.th.system.bulletdispenser;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.system.bullet.Bullet;
import org.joml.Vector2f;

public interface BulletProvider {

    Codec<BulletProvider> CODEC = null;

    Bullet getBullet(BulletDispenser dispenser, Vector2f position);

    String name();
}
