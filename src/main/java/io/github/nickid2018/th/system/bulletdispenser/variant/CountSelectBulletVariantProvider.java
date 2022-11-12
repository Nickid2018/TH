package io.github.nickid2018.th.system.bulletdispenser.variant;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.bullet.BulletBasicData;
import io.github.nickid2018.th.system.bulletdispenser.BulletDispenser;
import io.github.nickid2018.th.util.CollectionUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;

import java.util.List;

public class CountSelectBulletVariantProvider implements BulletVariantProvider {

    public static final Codec<CountSelectBulletVariantProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BulletVariantProvider.CODEC.listOf().fieldOf("providers").forGetter(CountSelectBulletVariantProvider::getProviders),
            Codec.INT.listOf().fieldOf("counts").forGetter(CountSelectBulletVariantProvider::getCounts)
    ).apply(instance, CountSelectBulletVariantProvider::new));

    @Getter
    private final List<BulletVariantProvider> providers;
    @Getter
    private final IntList counts;

    private final int sum;

    public CountSelectBulletVariantProvider(List<BulletVariantProvider> providers, List<Integer> counts) {
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
    public BulletBasicData getBulletBasicData(BulletDispenser dispenser) {
        int index = CollectionUtil.binarySearch(counts, dispenser.getDispenseStep() % sum);
        return providers.get(index).getBulletBasicData(dispenser);
    }

    @Override
    public String getVariant(BulletDispenser dispenser) {
        int index = CollectionUtil.binarySearch(counts, dispenser.getDispenseStep() % sum);
        return providers.get(index).getVariant(dispenser);
    }

    @Override
    public String name() {
        return "count";
    }
}
