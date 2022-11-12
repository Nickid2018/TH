package io.github.nickid2018.th.system.bulletdispenser.args;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.system.bullet.BulletProvider;
import io.github.nickid2018.th.system.bulletdispenser.BulletDispenser;
import io.github.nickid2018.th.system.bulletdispenser.variant.BulletVariantProvider;

import java.util.List;

public interface BulletArgsProvider {

    Codec<BulletArgsProvider> CODEC = Codec.STRING.dispatch(BulletArgsProvider::name, BulletArgsProvider::getProvider);

    static Codec<? extends BulletArgsProvider> getProvider(String s) {
        return switch (s) {
            case "simple" -> SimpleBulletArgsProvider.CODEC;
            default -> throw new IllegalArgumentException("Unknown bullet args provider: " + s);
        };
    }

    List<Bullet> getBullets(BulletDispenser dispenser, BulletVariantProvider variantProvider, BulletProvider bulletProvider);

    String name();
}
