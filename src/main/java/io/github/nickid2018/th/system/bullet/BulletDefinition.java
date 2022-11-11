package io.github.nickid2018.th.system.bullet;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.dyn.MethodHandleRepo;
import io.github.nickid2018.th.util.ResourceLocation;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joml.Vector2f;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

@AllArgsConstructor
@Getter
public record BulletDefinition(MethodHandle constructor) {

    private static final MethodHandle PATH_CONTROLLER_CONSTRUCTOR;

    static {
        try {
            PATH_CONTROLLER_CONSTRUCTOR = MethodHandles.lookup().findConstructor(
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
                    return PATH_CONTROLLER_CONSTRUCTOR;
                else
                    return MethodHandleRepo.getHandle(ResourceLocation.fromString(name), MethodHandleRepo.BULLET_HANDLE);
            },
            handle -> {
                if (handle.equals(PATH_CONTROLLER_CONSTRUCTOR))
                    return "default";
                else
                    return MethodHandleRepo.getHandleName(handle, MethodHandleRepo.BULLET_HANDLE).toString();
            }
    ).xmap(BulletDefinition::new, BulletDefinition::getConstructor);
}
