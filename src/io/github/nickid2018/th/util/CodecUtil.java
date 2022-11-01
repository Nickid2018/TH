package io.github.nickid2018.th.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import java.util.function.Consumer;

public class CodecUtil {

    public static final Consumer<String> NOP = s -> {};

    public static <T> T fromJson(String json, Codec<T> codec) {
        return codec.parse(JsonOps.INSTANCE, JsonOps.INSTANCE.createString(json)).getOrThrow(false, NOP);
    }

    public static <T> T fromJson(JsonElement json, Codec<T> codec) {
        return codec.parse(JsonOps.INSTANCE, json).getOrThrow(false, NOP);
    }
}
