package io.github.nickid2018.th.system.pack;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import lombok.Getter;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class DataPack implements Closeable {

    @Getter
    protected PackMetadata metadata;

    public abstract byte[] getEntry(String name) throws IOException;

    public JsonElement getJsonEntry(String name) throws IOException {
        return JsonParser.parseString(new String(getEntry(name), StandardCharsets.UTF_8));
    }

    protected void loadMetadata() throws IOException {
        JsonElement element = getJsonEntry("pack.metadata");
        metadata = PackMetadata.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, element))
                .getOrThrow(false, error -> {});
    }

    @Override
    public void close() throws IOException {
    }
}
