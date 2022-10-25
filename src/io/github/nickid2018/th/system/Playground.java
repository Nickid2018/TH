package io.github.nickid2018.th.system;

import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.system.player.Player;

import java.util.HashSet;
import java.util.Set;

public class Playground {

    public static final int PLAYGROUND_HEIGHT = 800;
    public static final int PLAYGROUND_WIDTH = 600;

    private final Set<Bullet> bullets = new HashSet<>();
    private final Set<Bullet> toDelete = new HashSet<>();
    private final Player player;

    public Playground(Player selected) {
        player = selected;
    }

    public void update() {
        bullets.forEach(Bullet::tick);
        bullets.removeAll(toDelete);
        toDelete.clear();
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isItemOutsidePlayground(HittableItem item) {
        return false;
    }

    public void dispose(Bullet item) {
        toDelete.add(item);
    }

}
