package io.github.nickid2018.th.system.compute;

import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.system.bullet.BulletDispenser;
import io.github.nickid2018.th.system.enemy.Enemy;
import io.github.nickid2018.th.system.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Playground {

    public static final int PLAYGROUND_HEIGHT = 800;
    public static final int PLAYGROUND_WIDTH = 600;

    private final Set<Enemy> enemies = new HashSet<>();
    private final Set<BulletDispenser> bulletDispensers = new HashSet<>();
    private final Set<Bullet> bullets = new HashSet<>();
    private final Set<Tickable> toDelete = new HashSet<>();

    @Getter
    private final Player player;

    private long tickTime = 0;

    @Getter
    @Setter
    protected Random random = new Random();

    public Playground(Player selected) {
        player = selected;
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
        return false;
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
