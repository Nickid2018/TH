package io.github.nickid2018.th.system.bullet;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.github.nickid2018.th.system.bulletdispenser.BulletDispenser;
import io.github.nickid2018.th.system.compute.Registry;
import io.github.nickid2018.th.system.dyn.UserDefinedBulletProvider;
import io.github.nickid2018.th.util.ResourceLocation;
import org.joml.Vector2f;

public interface BulletProvider {

    Codec<BulletProvider> FILE_CODEC = Codec.STRING.dispatch(BulletProvider::name, BulletProvider::getProvider);
    Codec<BulletProvider> CODEC = Codec.either(
            ResourceLocation.CODEC.xmap(BulletProvider.REGISTRY::get, BulletProvider.REGISTRY::key),
            FILE_CODEC
    ).xmap(e -> e.map(b -> b, b -> b), Either::right);

    Registry<BulletProvider> REGISTRY = new Registry<>(FILE_CODEC);

    static Codec<? extends BulletProvider> getProvider(String s) {
        return switch (s) {
            case "path" -> PathControllingBulletProvider.CODEC;
            case "straight" -> StraightBulletProvider.CODEC;
            case "user" -> UserDefinedBulletProvider.CODEC;
            default -> throw new IllegalArgumentException("Unknown bullet provider: " + s);
        };
    }


    Bullet getBullet(BulletDispenser dispenser, BulletBasicData data, String variant, Vector2f position);

    String name();
}
