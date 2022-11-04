package io.github.nickid2018.th.pack;

import com.mojang.serialization.Codec;

import java.util.Map;

public record ScriptObjectData(Map<String, String> scriptObjects) {

    public static final Codec<ScriptObjectData> CODEC = Codec.unboundedMap(
            Codec.STRING, Codec.STRING
    ).xmap(ScriptObjectData::new, ScriptObjectData::scriptObjects);
}
