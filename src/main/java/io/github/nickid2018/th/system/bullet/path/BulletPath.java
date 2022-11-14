package io.github.nickid2018.th.system.bullet.path;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.system.bullet.PathControllingBullet;

public abstract class BulletPath {

    public static final Codec<BulletPath> CODEC = Codec.STRING.dispatch(BulletPath::getComputeType, BulletPath::getCodec);

    private static  Codec<? extends BulletPath> getCodec(String s) {
        return switch (s) {
            case "fixed_path" -> FixedFunctionBulletPath.CODEC;
            case "straight" -> StraightBulletPath.CODEC;
            default -> throw new IllegalArgumentException("Unknown compute type: " + s);
        };
    }

    public abstract float tick(long runningTick, PathControllingBullet bullet);

    public abstract Object[] createArguments(PathControllingBullet bullet);

    public abstract String getComputeType();
}
