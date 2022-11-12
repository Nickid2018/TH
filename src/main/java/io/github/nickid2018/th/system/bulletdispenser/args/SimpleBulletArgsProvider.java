package io.github.nickid2018.th.system.bulletdispenser.args;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.system.bullet.BulletProvider;
import io.github.nickid2018.th.system.bulletdispenser.BulletDispenser;
import io.github.nickid2018.th.system.bulletdispenser.variant.BulletVariantProvider;
import io.github.nickid2018.th.system.valueprovider.ValueProvider;
import io.github.nickid2018.th.system.valueprovider.floating.FloatProvider;
import io.github.nickid2018.th.system.valueprovider.vector.Vector2fProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.joml.Vector2f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class SimpleBulletArgsProvider implements BulletArgsProvider {

    public static final Codec<SimpleBulletArgsProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Vector2fProvider.CODEC.listOf().fieldOf("positions").forGetter(SimpleBulletArgsProvider::getPositions),
            FloatProvider.CODEC.listOf().optionalFieldOf("angles", Collections.emptyList()).forGetter(SimpleBulletArgsProvider::getAngles),
            Codec.unboundedMap(
                    Codec.STRING,
                    ValueProvider.CODEC.listOf()
            ).optionalFieldOf("args", Collections.emptyMap()).forGetter(SimpleBulletArgsProvider::getExtraArgs)
    ).apply(instance, SimpleBulletArgsProvider::new));

    private final List<Vector2fProvider> positions;
    private final List<FloatProvider> angles;
    private final Map<String, List<ValueProvider<?>>> extraArgs;

    @Override
    @SneakyThrows
    public List<Bullet> getBullets(BulletDispenser dispenser, BulletVariantProvider variantProvider, BulletProvider bulletProvider) {
        List<Bullet> bullets = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            Vector2f vector = positions.get(i).getValue(dispenser);
            Bullet bullet = bulletProvider.getBullet(dispenser,
                    variantProvider.getBulletBasicData(dispenser), variantProvider.getVariant(dispenser), vector);
            if (i < angles.size())
                bullet.setAngle(angles.get(i).getValue(dispenser));
            bullets.add(bullet);
        }
        for (Map.Entry<String, List<ValueProvider<?>>> entry : extraArgs.entrySet()) {
            Field field = bullets.get(0).getClass().getDeclaredField(entry.getKey());
            field.setAccessible(true);
            for (int i = 0; i < entry.getValue().size(); i++)
                field.set(bullets.get(i), entry.getValue().get(i).getValue(dispenser));
        }
        return bullets;
    }

    @Override
    public String name() {
        return "simple";
    }
}
