package io.github.nickid2018.th.system.compute;

import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import io.github.nickid2018.th.gui.PlayGroundGui;
import io.github.nickid2018.th.pack.PackManager;
import io.github.nickid2018.th.phys.Sphere;
import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.system.bullet.BulletBasicData;
import io.github.nickid2018.th.system.bullet.BulletDispenser;
import io.github.nickid2018.th.system.bullet.PathControllingBullet;
import io.github.nickid2018.th.system.bullet.path.BulletPath;
import io.github.nickid2018.th.system.enemy.Enemy;
import io.github.nickid2018.th.system.player.Player;
import io.github.nickid2018.th.util.ResourceLocation;
import io.github.nickid2018.tiny2d.math.AABB;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;

import java.util.*;

// 384x448
public class Playground {

    public static final int PLAYGROUND_HEIGHT = 448;
    public static final int PLAYGROUND_WIDTH = 384;

    @Getter
    private final AABB playgroundBulletAABB;

    @Getter
    private final Set<Enemy> enemies = new HashSet<>();
    @Getter
    private final Set<BulletDispenser> bulletDispensers = new HashSet<>();
    @Getter
    private final Set<Bullet> bullets = new HashSet<>();
    private final Set<Tickable> toDelete = new HashSet<>();

    @Setter
    @Getter
    private Player player;

    @Getter
    private long tickTime = 0;

    @Setter
    @Getter
    protected long initialRandomSeed;

    @Setter
    @Getter
    protected Random random = new Random();

    BulletBasicData bulletBasicData, bulletBasicData2;
    Long2ObjectMap<BulletPath> map;

    public Playground() {
        playgroundBulletAABB = AABB.newAABB(-20, -20, PLAYGROUND_WIDTH + 20, PLAYGROUND_HEIGHT + 20);

        bulletBasicData = PackManager.createObject(
                ResourceLocation.fromString("bullets/ball.json"), BulletBasicData.CODEC);
        bulletBasicData2 = PackManager.createObject(
                ResourceLocation.fromString("bullets/orbs.json"), BulletBasicData.CODEC);

        String json = """
                {
                    "0": {
                        "type": "fixed_path",
                        "time": 100,
                        "function": {
                            "x_function": "internal:bezier_2",
                            "y_function": "internal:bezier_2",
                            "control_points": [
                                [200, 100],
                                {
                                    "compute_type": "no_arg_function",
                                    "action": "player"
                                }
                            ]
                        }
                    }
                }
                """;
        map = PathControllingBullet.PATH_LIST_CODEC.parse(
                new Dynamic<>(JsonOps.INSTANCE, JsonParser.parseString(json))
        ).getOrThrow(false, System.out::println);
    }

    public void update() {
        tickTime++;

        player.tick(tickTime);

        if (tickTime % 160 > 80 * ((4000 - tickTime) / 4000.0)) {
            for (int i = 0; i < 500; i++) {
                PathControllingBullet bullet = new PathControllingBullet(this, bulletBasicData2, "green",
                        new Vector2f(random.nextInt(PLAYGROUND_WIDTH), -12), map);
                addBullet(bullet);
            }
        }

        bullets.forEach(b -> b.tick(tickTime));
        enemies.forEach(e -> e.tick(tickTime));
        bulletDispensers.forEach(d -> d.tick(tickTime));

        // --- Test Codes
        List<Bullet> bulletList = playerHitItems().stream()
                .filter(i -> i instanceof Bullet)
                .map(i -> (Bullet) i).toList();
        bulletList.forEach(this::dispose);
        if (bulletList.size() > 0) {
            player.setPlayers(player.getPlayers() - 1);
            PlayGroundGui.soundInstanceMissed.play();
        }

        playerGraze().stream().filter(b -> !b.isGrazed()).forEach(i -> {
            i.setGrazed(true);
//            PlayGroundGui.soundInstanceGraze.play();
            player.setGrazeCount(player.getGrazeCount() + 1);
        });

        bullets.removeAll(toDelete);
        enemies.removeAll(toDelete);
        bulletDispensers.removeAll(toDelete);
        toDelete.clear();
    }

    public boolean isItemOutsidePlayground(HittableItem item) {
        return !playgroundBulletAABB.intersects(item.getHitSphere().getOuterAABB());
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

    public List<HittableItem> playerHitItems() {
        List<HittableItem> list = new ArrayList<>();
        enemies.forEach(e -> {
            if (player.getHitSphere().orthogonalWith(e.getHitSphere()))
                list.add(e);
        });
        bullets.forEach(d -> {
            if (player.getHitSphere().orthogonalWith(d.getHitSphere()))
                list.add(d);
        });
        return list;
    }

    public List<Bullet> playerGraze() {
        List<Bullet> list = new ArrayList<>();
        Sphere grazeSphere = new Sphere(player.getHitSphere().getPosition(), player.getBasicData().grazeRadius());
        bullets.forEach(d -> {
            if (grazeSphere.crossWith(d.getHitSphere()))
                list.add(d);
        });
        return list;
    }
}
