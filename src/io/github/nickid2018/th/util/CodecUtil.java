package io.github.nickid2018.th.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.joml.Vector2f;

import java.util.List;
import java.util.function.Consumer;

public class CodecUtil {

    public static final Codec<Vector2f> VECTOR_2F_CODEC = Codec.FLOAT.listOf().xmap(
            list -> new Vector2f(list.get(0), list.get(1)),
            vector2f -> List.of(vector2f.x, vector2f.y)
    );

    public static final Consumer<String> NOP = s -> {};

    public static <T> T fromJson(String json, Codec<T> codec) {
        return codec.parse(JsonOps.INSTANCE, JsonOps.INSTANCE.createString(json)).getOrThrow(false, NOP);
    }

    public static <T> T fromJson(JsonElement json, Codec<T> codec) {
        return codec.parse(JsonOps.INSTANCE, json).getOrThrow(false, NOP);
    }
}
