package io.github.nickid2018.th.system.compute;

import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.system.bullet.BulletDispenser;
import io.github.nickid2018.th.system.enemy.Enemy;
import io.github.nickid2018.th.system.player.Player;
import io.github.nickid2018.tiny2d.math.AABB;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

// 384x448
public class Playground {

    public static final int PLAYGROUND_HEIGHT = 448;
    public static final int PLAYGROUND_WIDTH = 384;

    @Getter
    private final AABB playgroundAABB;

    @Getter
    private final Set<Enemy> enemies = new HashSet<>();
    @Getter
    private final Set<BulletDispenser> bulletDispensers = new HashSet<>();
    @Getter
    private final Set<Bullet> bullets = new HashSet<>();
    private final Set<Tickable> toDelete = new HashSet<>();

    @Getter
    private final Player player;

    @Getter
    private long tickTime = 0;

    @Setter
    @Getter
    protected long initialRandomSeed;

    @Setter
    @Getter
    protected Random random = new Random();

    public Playground(Player selected) {
        player = selected;
        playgroundAABB = AABB.newAABB(0, 0, PLAYGROUND_WIDTH, PLAYGROUND_HEIGHT);
    }

    public void update() {
        tickTime++;

        player.tick(tickTime);

        bullets.forEach(b -> b.tick(tickTime));
        enemies.forEach(e -> e.tick(tickTime));
        bulletDispensers.forEach(d -> d.tick(tickTime));

        bullets.removeAll(toDelete);
        enemies.removeAll(toDelete);
        bulletDispensers.removeAll(toDelete);
        toDelete.clear();
    }

    public boolean isItemOutsidePlayground(HittableItem item) {
        return !playgroundAABB.intersects(item.getHitSphere().getOuterAABB());
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public void addBulletDispenser(BulletDispenser dispenser) {
        bulletDispensers.add(dispenser);
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public void dispose(Tickable item) {
        toDelete.add(item);
    }
}
