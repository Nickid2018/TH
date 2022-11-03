package io.github.nickid2018.th.system.bullet;

import io.github.nickid2018.th.system.bullet.path.BulletPath;
import io.github.nickid2018.th.system.compute.Playground;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.joml.Vector2f;

public class PathControllingBullet extends Bullet {

    private final Long2ObjectMap<BulletPath> paths;
    private long lastTick = 0;
    private BulletPath currentPath;

    public PathControllingBullet(Playground playground, BulletBasicData bulletBasicData,
                                 String variant, Vector2f position, Long2ObjectMap<BulletPath> paths) {
        super(playground, bulletBasicData, variant, position);
        this.paths = paths;
    }

    @Override
    public void tick(long tickTime) {
        if (paths.containsKey(lifeTime)) {
            currentPath = paths.get(lifeTime);
            lastTick = lifeTime;
        }
        currentPath.tick(lifeTime - lastTick, this);
        super.tick(tickTime);
    }
}
