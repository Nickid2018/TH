package io.github.nickid2018.th.system.bullet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.bulletdispenser.BulletDispenser;
import io.github.nickid2018.th.system.valueprovider.floating.FloatProvider;
import io.github.nickid2018.th.system.valueprovider.vector.Vector2fProvider;
import org.joml.Vector2f;

public record StraightBulletProvider(FloatProvider speed, Vector2fProvider direction) implements BulletProvider {

    public static final Codec<StraightBulletProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FloatProvider.CODEC.fieldOf("speed").forGetter(StraightBulletProvider::speed),
            Vector2fProvider.CODEC.fieldOf("direction").forGetter(StraightBulletProvider::direction)
    ).apply(instance, StraightBulletProvider::new));

    @Override
    public Bullet getBullet(BulletDispenser dispenser, BulletBasicData bulletBasicData, String variant, Vector2f position) {
        return new StraightBullet(dispenser.getPlayground(), bulletBasicData, variant, position, speed, direction);
    }

    public String name() {
        return "straight";
    }
}
