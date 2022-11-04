package io.github.nickid2018.th.pack;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import io.github.nickid2018.th.util.CodecUtil;
import io.github.nickid2018.th.util.ResourceLocation;
import it.unimi.dsi.fastutil.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PackManager {

    public static final Logger LOGGER = LoggerFactory.getLogger("Pack Manager");

    private static final List<Pair<String, DataPack>> PACK_LIST = new ArrayList<>();

    public static void setPackList(List<DataPack> list) {
        PACK_LIST.clear();
        for (DataPack pack : list)
            PACK_LIST.add(Pair.of(pack.getNamespace(), pack));
    }

    public static DataPack getPack(String namespace) {
        for (Pair<String, DataPack> pair : PACK_LIST)
            if (pair.left().equals(namespace))
                return pair.right();
        return null;
    }

    public static InputStream createInputStream(ResourceLocation location) {
        for (Pair<String, DataPack> pair : PACK_LIST) {
            if (pair.right().hasEntry(location.path()) &&
                    (location.namespace() == null || pair.left().equals(location.namespace()))) {
                try {
                    InputStream stream = pair.right().getEntryInStream(location.path());
                    if (stream != null)
                        return stream;
                } catch (IOException e) {
                    LOGGER.warn("Failed to create entry stream in data pack %s: %s"
                            .formatted(location.namespace(), location.path()), e);
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
        return CodecUtil.fromJson(createJSON(location), codec);
    }
}
