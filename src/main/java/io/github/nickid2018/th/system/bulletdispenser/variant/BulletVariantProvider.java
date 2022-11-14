package io.github.nickid2018.th.system.bulletdispenser.variant;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.system.bullet.BulletBasicData;
import io.github.nickid2018.th.system.bulletdispenser.BulletDispenser;

public interface BulletVariantProvider {

    Codec<BulletVariantProvider> CODEC = Codec.STRING.dispatch(BulletVariantProvider::name, BulletVariantProvider::getProvider);

    static Codec<? extends BulletVariantProvider> getProvider(String name) {
        return switch (name) {
            case "count" -> CountSelectBulletVariantProvider.CODEC;
            case "simple" -> SimpleBulletVariantProvider.CODEC;
            default -> throw new IllegalArgumentException("Unknown bullet variant provider: " + name);
        };
    }

    BulletBasicData getBulletBasicData(BulletDispenser dispenser);
    String getVariant(BulletDispenser dispenser);
    boolean hasDefinedPriority(BulletDispenser dispenser);
    int getPriority(BulletDispenser dispenser);

    String name();
}
