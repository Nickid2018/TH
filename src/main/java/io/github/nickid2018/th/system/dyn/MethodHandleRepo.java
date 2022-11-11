package io.github.nickid2018.th.system.dyn;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.system.bulletdispenser.BulletDispenser;
import io.github.nickid2018.th.system.compute.TimeLineRunner;
import io.github.nickid2018.th.system.enemy.Boss;
import io.github.nickid2018.th.system.enemy.Enemy;
import io.github.nickid2018.th.system.player.Player;
import io.github.nickid2018.th.util.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandle;

public class MethodHandleRepo {

    public static final int PLAYER_HANDLE = 0;
    public static final int ENEMY_HANDLE = 1;
    public static final int BULLET_HANDLE = 2;
    public static final int BULLET_DISPENSER_HANDLE = 3;
    public static final int BOSS_HANDLE = 4;
    public static final int TIMELINE_HANDLE = 5;

    public static final Logger LOGGER = LoggerFactory.getLogger("Method Handle Repo");

    private static final BiMap<ResourceLocation, MethodHandle> PLAYER_HANDLES = HashBiMap.create();
    private static final BiMap<ResourceLocation, MethodHandle> ENEMY_HANDLES = HashBiMap.create();
    private static final BiMap<ResourceLocation, MethodHandle> BULLET_HANDLES = HashBiMap.create();
    private static final BiMap<ResourceLocation, MethodHandle> BULLET_DISPENSER_HANDLES = HashBiMap.create();
    private static final BiMap<ResourceLocation, MethodHandle> BOSS_HANDLES = HashBiMap.create();
    private static final BiMap<ResourceLocation, MethodHandle> TIMELINE_HANDLES = HashBiMap.create();

    public static Player constructPlayer(ResourceLocation location, Object... args) {
        if (!PLAYER_HANDLES.containsKey(location))
            return null;
        try {
            return (Player) PLAYER_HANDLES.get(location).invokeWithArguments(args);
        } catch (Throwable e) {
            LOGGER.error("Error when constructing a player instance.", e);
            return null;
        }
    }

    public static Enemy constructEnemy(ResourceLocation location, Object... args) {
        if (!ENEMY_HANDLES.containsKey(location))
            return null;
        try {
            return (Enemy) ENEMY_HANDLES.get(location).invokeWithArguments(args);
        } catch (Throwable e) {
            LOGGER.error("Error when constructing an enemy instance.", e);
            return null;
        }
    }

    public static Bullet constructBullet(ResourceLocation location, Object... args) {
        if (!BULLET_HANDLES.containsKey(location))
            return null;
        try {
            return (Bullet) BULLET_HANDLES.get(location).invokeWithArguments(args);
        } catch (Throwable e) {
            LOGGER.error("Error when constructing a bullet instance.", e);
            return null;
        }
    }

    public static BulletDispenser constructBulletDispenser(ResourceLocation location, Object... args) {
        if (!BULLET_DISPENSER_HANDLES.containsKey(location))
            return null;
        try {
            return (BulletDispenser) BULLET_DISPENSER_HANDLES.get(location).invokeWithArguments(args);
        } catch (Throwable e) {
            LOGGER.error("Error when constructing a bullet dispenser instance.", e);
            return null;
        }
    }

    public static Boss constructBoss(ResourceLocation location, Object... args) {
        if (!BOSS_HANDLES.containsKey(location))
            return null;
        try {
            return (Boss) BOSS_HANDLES.get(location).invokeWithArguments(args);
        } catch (Throwable e) {
            LOGGER.error("Error when constructing a boss instance.", e);
            return null;
        }
    }

    public static TimeLineRunner constructTimeLineRunner(ResourceLocation location, Object... args) {
        if (!TIMELINE_HANDLES.containsKey(location))
            return null;
        try {
            return (TimeLineRunner) TIMELINE_HANDLES.get(location).invokeWithArguments(args);
        } catch (Throwable e) {
            LOGGER.error("Error when constructing a timeline runner instance.", e);
            return null;
        }
    }

    public static MethodHandle getHandle(ResourceLocation location, int type) {
        return switch (type) {
            case PLAYER_HANDLE -> PLAYER_HANDLES.get(location);
            case ENEMY_HANDLE -> ENEMY_HANDLES.get(location);
            case BULLET_HANDLE -> BULLET_HANDLES.get(location);
            case BULLET_DISPENSER_HANDLE -> BULLET_DISPENSER_HANDLES.get(location);
            case BOSS_HANDLE -> BOSS_HANDLES.get(location);
            case TIMELINE_HANDLE -> TIMELINE_HANDLES.get(location);
            default -> null;
        };
    }

    public static void putHandle(ResourceLocation location, MethodHandle handle, int type) {
        switch (type) {
            case PLAYER_HANDLE -> PLAYER_HANDLES.put(location, handle);
            case ENEMY_HANDLE -> ENEMY_HANDLES.put(location, handle);
            case BULLET_HANDLE -> BULLET_HANDLES.put(location, handle);
            case BULLET_DISPENSER_HANDLE -> BULLET_DISPENSER_HANDLES.put(location, handle);
            case BOSS_HANDLE -> BOSS_HANDLES.put(location, handle);
            case TIMELINE_HANDLE -> TIMELINE_HANDLES.put(location, handle);
        }
    }

    public static ResourceLocation getHandleName(MethodHandle handle, int type) {
        return switch (type) {
            case PLAYER_HANDLE -> PLAYER_HANDLES.inverse().get(handle);
            case ENEMY_HANDLE -> ENEMY_HANDLES.inverse().get(handle);
            case BULLET_HANDLE -> BULLET_HANDLES.inverse().get(handle);
            case BULLET_DISPENSER_HANDLE -> BULLET_DISPENSER_HANDLES.inverse().get(handle);
            case BOSS_HANDLE -> BOSS_HANDLES.inverse().get(handle);
            case TIMELINE_HANDLE -> TIMELINE_HANDLES.inverse().get(handle);
            default -> null;
        };
    }
}
