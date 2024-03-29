package io.github.nickid2018.th.pack;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import io.github.nickid2018.th.util.CodecUtil;
import lombok.Getter;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class DataPack implements Closeable {

    @Getter
    private final String namespace;

    @Getter
    protected PackMetadata metadata;

    @Getter
    protected ScriptObjectData scriptObjectData;

    public DataPack(String namespace) {
        this.namespace = namespace;
    }

    public byte[] getEntry(String name) throws IOException {
        try (InputStream stream = getEntryInStream(name)) {
            if (stream == null)
                return null;
            return stream.readAllBytes();
        }
    }

    public abstract boolean hasEntry(String name);
    public abstract InputStream getEntryInStream(String name) throws IOException;

    public JsonElement getJsonEntry(String name) throws IOException {
        return JsonParser.parseString(new String(getEntry(name), StandardCharsets.UTF_8));
    }

    protected void loadMetadata() throws IOException {
        JsonElement element = getJsonEntry("pack.metadata");
        metadata = PackMetadata.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, element))
                .getOrThrow(false, CodecUtil.NOP);
    }

    public String getScriptVariableName(String path) {
        if (scriptObjectData == null) {
            try {
                loadScriptObjectData();
            } catch (IOException ignored) {
            }
        }
        return scriptObjectData.scriptObjects().get(path);
    }

    private void loadScriptObjectData() throws IOException {
        JsonElement element = getJsonEntry("script.list");
        scriptObjectData = ScriptObjectData.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, element))
                .getOrThrow(false, CodecUtil.NOP);
    }

    @Override
    public void close() throws IOException {
    }
}
