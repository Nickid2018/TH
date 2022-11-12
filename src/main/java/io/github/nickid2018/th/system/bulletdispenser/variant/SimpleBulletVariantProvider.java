package io.github.nickid2018.th.system.bulletdispenser.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.bullet.BulletBasicData;
import io.github.nickid2018.th.system.bulletdispenser.BulletDispenser;
import io.github.nickid2018.th.util.ResourceLocation;

public record SimpleBulletVariantProvider(BulletBasicData bulletBasicData,
                                          String variant) implements BulletVariantProvider {

    public static final Codec<SimpleBulletVariantProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.xmap(BulletBasicData.REGISTRY::get, BulletBasicData.REGISTRY::getKey)
                    .fieldOf("data").forGetter(SimpleBulletVariantProvider::bulletBasicData),
            Codec.STRING.optionalFieldOf("variant", "default").forGetter(SimpleBulletVariantProvider::variant)
    ).apply(instance, SimpleBulletVariantProvider::new));

    @Override
    public BulletBasicData getBulletBasicData(BulletDispenser dispenser) {
        return bulletBasicData;
    }

    @Override
    public String getVariant(BulletDispenser dispenser) {
        return variant;
    }

    @Override
    public String name() {
        return "simple";
    }
}
