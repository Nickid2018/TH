package io.github.nickid2018.th.system.bulletdispenser;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.bullet.BulletProvider;
import io.github.nickid2018.th.system.bulletdispenser.args.BulletArgsProvider;
import io.github.nickid2018.th.system.bulletdispenser.variant.BulletVariantProvider;
import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.enemy.Enemy;

public record SimpleBulletDispenserProvider(BulletVariantProvider variantProvider,
                                            BulletArgsProvider argsProvider,
                                            BulletProvider bulletProvider) implements BulletDispenserProvider {

    public static final Codec<SimpleBulletDispenserProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BulletVariantProvider.CODEC.fieldOf("variant").forGetter(SimpleBulletDispenserProvider::variantProvider),
            BulletArgsProvider.CODEC.fieldOf("args").forGetter(SimpleBulletDispenserProvider::argsProvider),
            BulletProvider.CODEC.fieldOf("bullet").forGetter(SimpleBulletDispenserProvider::bulletProvider)
    ).apply(instance, SimpleBulletDispenserProvider::new));

    @Override
    public BulletDispenser getDispenser(Playground playground, Enemy enemy) {
        return new SimpleBulletDispenser(playground, variantProvider, argsProvider, bulletProvider, enemy);
    }

    @Override
    public String name() {
        return "simple";
    }
}
