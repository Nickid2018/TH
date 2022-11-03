package io.github.nickid2018.th.system.valueprovider.floating;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.system.valueprovider.ValueProvider;

public interface FloatProvider extends ValueProvider<Float> {

     default String getValueProviderType() {
        return "float";
     }

    Codec<FloatProvider> CODEC = Codec.STRING.xmap(s -> null, FloatProvider::toString);
}
