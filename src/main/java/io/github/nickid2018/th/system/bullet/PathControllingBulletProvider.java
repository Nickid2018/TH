package io.github.nickid2018.th.system.bullet;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.bullet.path.BulletPath;
import io.github.nickid2018.th.system.bulletdispenser.BulletDispenser;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public record PathControllingBulletProvider(Long2ObjectMap<BulletPath> pathList) implements BulletProvider {

    public static final Codec<PathControllingBulletProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.compoundList(
                    Codec.STRING, BulletPath.CODEC
            ).xmap(
                    list -> {
                        Long2ObjectMap<BulletPath> map = new Long2ObjectOpenHashMap<>();
                        for (Pair<String, BulletPath> longBulletPathPair : list)
                            map.put(Long.parseLong(longBulletPathPair.getFirst()), longBulletPathPair.getSecond());
                        return map;
                    },
                    map -> {
                        List<Pair<String, BulletPath>> list = new ArrayList<>();
                        map.forEach((l, p) -> list.add(new Pair<>(Long.toString(l), p)));
                        return list;
                    }
            ).fieldOf("path_list").forGetter(PathControllingBulletProvider::pathList)
    ).apply(instance, PathControllingBulletProvider::new));

    @Override
    public Bullet getBullet(BulletDispenser dispenser, BulletBasicData data, String variant, Vector2f position) {
        return new PathControllingBullet(dispenser.getPlayground(), data, variant, position, pathList);
    }

    @Override
    public String name() {
        return "path";
    }
}
