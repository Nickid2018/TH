package io.github.nickid2018.th.system.bulletdispenser;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.util.CollectionUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import org.joml.Vector2f;

import java.util.List;

public class CountSelectBulletProvider implements BulletProvider {

    public static final Codec<CountSelectBulletProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(BulletProvider.CODEC).fieldOf("providers").forGetter(CountSelectBulletProvider::getProviders),
            Codec.INT.listOf().fieldOf("counts").forGetter(CountSelectBulletProvider::getCounts)
    ).apply(instance, CountSelectBulletProvider::new));

    @Getter
    private final List<BulletProvider> providers;
    @Getter
    private final IntList counts;

    private final int sum;

    public CountSelectBulletProvider(List<BulletProvider> providers, List<Integer> counts) {
        this.providers = providers;
        this.counts = new IntArrayList();
        Preconditions.checkArgument(providers.size() == counts.size(), "The size of providers and counts must be equal");
        int last = 0;
        for (int count : counts) {
            Preconditions.checkArgument(count > 0, "Count must be positive");
            this.counts.add(last += count);
        }
        sum = last;
    }

    @Override
    public Bullet getBullet(BulletDispenser dispenser, Vector2f position) {
        int now = dispenser.getDispenseStep() % sum;
        int index = CollectionUtil.binarySearch(counts, now);
        return providers.get(index).getBullet(dispenser, position);
    }

    @Override
    public String name() {
        return "count";
    }
}
