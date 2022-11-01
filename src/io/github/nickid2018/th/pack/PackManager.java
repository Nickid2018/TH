package io.github.nickid2018.th.pack;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import io.github.nickid2018.th.util.ResourceLocation;
import it.unimi.dsi.fastutil.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PackManager {

    private static final List<Pair<String, DataPack>> PACK_LIST = new ArrayList<>();

    public static void setPackList(List<DataPack> list) {
        PACK_LIST.clear();
        for (DataPack pack : list)
            PACK_LIST.add(Pair.of(pack.getNamespace(), pack));
    }

    public static InputStream createInputStream(ResourceLocation location) {
        for (Pair<String, DataPack> pair : PACK_LIST) {
            if (pair.right().hasEntry(location.path()) &&
                    (location.namespace() == null || pair.left().equals(location.namespace()))) {
                try {
                    InputStream stream = pair.right().getEntryInStream(location.path());
                    if (stream != null)
                        return stream;
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

    public static String getNamespaceDefaultSelect(String path) {
        for (Pair<String, DataPack> pair : PACK_LIST) {
            if (pair.right().hasEntry(path))
                return pair.left();
        }
        return null;
    }

    public static JsonElement createJSON(ResourceLocation location) {
        try (InputStream stream = createInputStream(location)) {
            if (stream == null)
                return JsonNull.INSTANCE;
            return JsonParser.parseString(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException ignored) {
            return JsonNull.INSTANCE;
        }
    }

    public static <T> T createObject(ResourceLocation location, Codec<T> codec) {
        return codec.parse(new Dynamic<>(JsonOps.INSTANCE, createJSON(location))).getOrThrow(false, error -> {});
    }
}
