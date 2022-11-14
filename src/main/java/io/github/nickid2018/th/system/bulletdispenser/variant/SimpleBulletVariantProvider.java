package io.github.nickid2018.th.system.bulletdispenser.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.bullet.BulletBasicData;
import io.github.nickid2018.th.system.bulletdispenser.BulletDispenser;
import io.github.nickid2018.th.util.ResourceLocation;

public record SimpleBulletVariantProvider(BulletBasicData bulletBasicData,
                                          String variant,
                                          int priority) implements BulletVariantProvider {

    public static final Codec<SimpleBulletVariantProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.xmap(BulletBasicData.REGISTRY::get, BulletBasicData.REGISTRY::key)
                    .fieldOf("data").forGetter(SimpleBulletVariantProvider::bulletBasicData),
            Codec.STRING.optionalFieldOf("variant", "default").forGetter(SimpleBulletVariantProvider::variant),
            Codec.INT.optionalFieldOf("priority", Integer.MIN_VALUE).forGetter(SimpleBulletVariantProvider::priority)
    ).apply(instance, SimpleBulletVariantProvider::new));

    public SimpleBulletVariantProvider(BulletBasicData bulletBasicData, String variant) {
        this(bulletBasicData, variant, Integer.MIN_VALUE);
    }

    @Override
    public BulletBasicData getBulletBasicData(BulletDispenser dispenser) {
        return bulletBasicData;
    }

    @Override
    public String getVariant(BulletDispenser dispenser) {
        return variant;
    }

    public boolean hasDefinedPriority(BulletDispenser dispenser) {
        return priority != Integer.MIN_VALUE;
    }

    public int getPriority(BulletDispenser dispenser) {
        return priority;
    }

    @Override
    public String name() {
        return "simple";
    }
}
