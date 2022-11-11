package io.github.nickid2018.th.system.bulletdispenser;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.system.bullet.BulletBasicData;
import io.github.nickid2018.th.system.bullet.BulletDefinition;
import lombok.SneakyThrows;
import org.joml.Vector2f;

public record SimpleBulletProvider(BulletBasicData bulletBasicData,
                                   BulletDefinition definition,
                                   String variant) implements BulletProvider {

    public static final Codec<SimpleBulletProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BulletBasicData.CODEC.fieldOf("bullet_basic_data").forGetter(SimpleBulletProvider::bulletBasicData),
            BulletDefinition.CODEC.fieldOf("bullet_definition").forGetter(SimpleBulletProvider::definition),
            Codec.STRING.optionalFieldOf("variant", "default").forGetter(SimpleBulletProvider::variant)
    ).apply(instance, SimpleBulletProvider::new));

    @Override
    @SneakyThrows
    public Bullet getBullet(BulletDispenser dispenser, Vector2f position) {
        return (Bullet) definition.getConstructor().invoke(dispenser.getPlayground(), bulletBasicData, variant, position);
    }
}
