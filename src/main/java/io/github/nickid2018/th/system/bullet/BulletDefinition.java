package io.github.nickid2018.th.system.bullet;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.pack.PackManager;
import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.dyn.MethodHandleRepo;
import io.github.nickid2018.th.system.dyn.UserDefinedBulletMaker;
import io.github.nickid2018.th.util.ResourceLocation;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.joml.Vector2f;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public record BulletDefinition(MethodHandle constructor) {

    private static final MethodHandle PATH_CONTROL_CONSTRUCTOR;

    static {
        try {
            PATH_CONTROL_CONSTRUCTOR = MethodHandles.lookup().findConstructor(
                    PathControllingBullet.class,
                    MethodType.methodType(void.class, Playground.class, BulletBasicData.class,
                            String.class, Vector2f.class, Long2ObjectMap.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static final Codec<BulletDefinition> CODEC = Codec.STRING.xmap(
            name -> {
                if (name.equals("default"))
                    return PATH_CONTROL_CONSTRUCTOR;
                else
                    return make(ResourceLocation.fromString(name));
            },
            handle -> {
                if (handle.equals(PATH_CONTROL_CONSTRUCTOR))
                    return "default";
                else
                    return MethodHandleRepo.getHandleName(handle, MethodHandleRepo.BULLET_HANDLE).toString();
            }
    ).xmap(BulletDefinition::new, BulletDefinition::constructor);

    private static MethodHandle make(ResourceLocation location) {
        MethodHandle handle = MethodHandleRepo.getHandle(location, MethodHandleRepo.BULLET_HANDLE);
        if (handle != null)
            return handle;
        UserDefinedBulletMaker maker = PackManager.createObject(location, UserDefinedBulletMaker.CODEC);
        MethodHandleRepo.putHandle(location, maker.getConstructor(), MethodHandleRepo.BULLET_HANDLE);
        return maker.getConstructor();
    }
}
