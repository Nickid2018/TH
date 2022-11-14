package io.github.nickid2018.th.system.compute;

import io.github.nickid2018.th.gui.PlayGroundGui;
import io.github.nickid2018.th.pack.PackManager;
import io.github.nickid2018.th.phys.Sphere;
import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.system.bullet.BulletBasicData;
import io.github.nickid2018.th.system.bulletdispenser.AimedShotBulletDispenser;
import io.github.nickid2018.th.system.bulletdispenser.BulletDispenser;
import io.github.nickid2018.th.system.bulletdispenser.RoundShotBulletDispenser;
import io.github.nickid2018.th.system.bulletdispenser.variant.SimpleBulletVariantProvider;
import io.github.nickid2018.th.system.enemy.Enemy;
import io.github.nickid2018.th.system.player.Player;
import io.github.nickid2018.th.system.valueprovider.floating.ConstantFloat;
import io.github.nickid2018.th.system.valueprovider.floating.FunctionFloat;
import io.github.nickid2018.th.system.valueprovider.integer.ConstantInt;
import io.github.nickid2018.th.util.ResourceLocation;
import io.github.nickid2018.tiny2d.math.AABB;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

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

    BulletDispenser dispenser;
    AimedShotBulletDispenser aimedShotBulletDispenser;
    AimedShotBulletDispenser aimedShotBulletDispenser2;

    public Playground() {
        playgroundBulletAABB = AABB.newAABB(-20, -20, PLAYGROUND_WIDTH + 20, PLAYGROUND_HEIGHT + 20);

        BulletBasicData bulletBasicData = PackManager.createObject(
                ResourceLocation.fromString("bullets/ball.json"), BulletBasicData.CODEC);
        BulletBasicData bulletBasicData2 = PackManager.createObject(
                ResourceLocation.fromString("bullets/jellybean.json"), BulletBasicData.CODEC);

        aimedShotBulletDispenser = new AimedShotBulletDispenser(this,
                new SimpleBulletVariantProvider(bulletBasicData, "blue", 10),
                new ConstantInt(51),
                new ConstantFloat((float) (2 * Math.PI * (50f / 51f))),
                new ConstantFloat(2f),
                null);
        aimedShotBulletDispenser.setDispenseInterval(80);
        aimedShotBulletDispenser.moveTo(50, 100);
        addBulletDispenser(aimedShotBulletDispenser);
        aimedShotBulletDispenser2 = new AimedShotBulletDispenser(this,
                new SimpleBulletVariantProvider(bulletBasicData, "blue", 10),
                new ConstantInt(51),
                new ConstantFloat((float) (2 * Math.PI * (50f / 51f))),
                new ConstantFloat(2.5f),
                null);
        aimedShotBulletDispenser2.setDispenseInterval(80);
        addBulletDispenser(aimedShotBulletDispenser2);

        dispenser = new RoundShotBulletDispenser(this,
                new SimpleBulletVariantProvider(bulletBasicData, "red"),
                new ConstantFloat(50),
                new ConstantInt(20),
                new ConstantFloat(1.5f),
                new FunctionFloat(FunctionFloat.RANDOM, List.of(new ConstantFloat(0f), new ConstantFloat((float) Math.PI))),
                null);
        dispenser.setDispenseInterval(10);
        addBulletDispenser(dispenser);

        RoundShotBulletDispenser dispenser2 = new RoundShotBulletDispenser(this,
                new SimpleBulletVariantProvider(bulletBasicData2, "blue"),
                new ConstantFloat(50),
                new ConstantInt(24),
                new ConstantFloat(5f),
                new ConstantFloat((float) (Math.PI / 24)),
                null);
        dispenser2.setDispenseInterval(10);
        dispenser2.moveTo(PLAYGROUND_WIDTH / 2f, 100);
        addBulletDispenser(dispenser2);
    }

    @SneakyThrows
    public void update() {
        tickTime++;

        player.tick(tickTime);

        dispenser.moveTo(PLAYGROUND_WIDTH / 2f + random.nextFloat(-20, 20), 100 + random.nextFloat(-20, 20));
        aimedShotBulletDispenser.moveTo(40 + random.nextFloat(-20, 20), 150 + random.nextFloat(-40, 40));
        aimedShotBulletDispenser2.moveTo(PLAYGROUND_WIDTH - 40 + random.nextFloat(-20, 20), 150 + random.nextFloat(-40, 40));

        bullets.forEach(b -> b.tick(tickTime));
        enemies.forEach(e -> e.tick(tickTime));
        bulletDispensers.forEach(d -> d.tick(tickTime));

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
