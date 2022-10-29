package io.github.nickid2018.th.pack;

import io.github.nickid2018.th.util.ResourceLocation;
import it.unimi.dsi.fastutil.Pair;

import java.io.IOException;
import java.io.InputStream;
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
            if (location.namespace() == null || pair.left().equals(location.namespace())) {
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
}
