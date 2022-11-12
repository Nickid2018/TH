package io.github.nickid2018.th.system.bulletdispenser;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.bulletdispenser.variant.BulletVariantProvider;
import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.enemy.Enemy;
import io.github.nickid2018.th.system.valueprovider.floating.FloatProvider;
import io.github.nickid2018.th.system.valueprovider.integer.IntProvider;

public record AimedShotBulletDispenserProvider(BulletVariantProvider provider, IntProvider ways, FloatProvider maxAngle,
                                               FloatProvider speed) implements BulletDispenserProvider {

    public static final Codec<AimedShotBulletDispenserProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BulletVariantProvider.CODEC.fieldOf("provider").forGetter(AimedShotBulletDispenserProvider::provider),
            IntProvider.CODEC.fieldOf("ways").forGetter(AimedShotBulletDispenserProvider::ways),
            FloatProvider.CODEC.fieldOf("max_angle").forGetter(AimedShotBulletDispenserProvider::maxAngle),
            FloatProvider.CODEC.fieldOf("speed").forGetter(AimedShotBulletDispenserProvider::speed)
    ).apply(instance, AimedShotBulletDispenserProvider::new));

    @Override
    public BulletDispenser getDispenser(Playground playground, Enemy enemy) {
        return new AimedShotBulletDispenser(playground, provider, ways, maxAngle, speed, enemy);
    }

    @Override
    public String name() {
        return "aimed_shot";
    }
}
