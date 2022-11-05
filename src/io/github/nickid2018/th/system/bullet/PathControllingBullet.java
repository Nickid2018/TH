package io.github.nickid2018.th.system.bullet;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.nickid2018.th.system.bullet.path.BulletPath;
import io.github.nickid2018.th.system.compute.Playground;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class PathControllingBullet extends Bullet {

    public static final Vector2f POSITIVE_X = new Vector2f(1, 0);

    public static final Codec<Long2ObjectMap<BulletPath>> PATH_LIST_CODEC = Codec.compoundList(
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
    );

    @Getter
    private final Long2ObjectMap<BulletPath> paths;
    private long lastTick = 0;
    private BulletPath currentPath;

    @Getter
    private Object[] storedArgs;

    public PathControllingBullet(Playground playground, BulletBasicData bulletBasicData,
                                 String variant, Vector2f position, Long2ObjectMap<BulletPath> paths) {
        super(playground, bulletBasicData, variant, position);
        this.paths = paths;
    }

    @Override
    public void tick(long tickTime) {
        if (paths.containsKey(lifeTime)) {
            currentPath = paths.get(lifeTime);
            storedArgs = currentPath.createArguments(this);
            lastTick = lifeTime;
        }
        Vector2f lastPos = sphere.getPosition();
        currentPath.tick(lifeTime - lastTick, this);
        Vector2f newPos = sphere.getPosition();
        Vector2f dir = newPos.sub(lastPos);
        angle = dir.angle(POSITIVE_X);
        super.tick(tickTime);
    }
}
