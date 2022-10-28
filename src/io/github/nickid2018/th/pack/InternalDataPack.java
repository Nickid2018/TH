package io.github.nickid2018.th.pack;

import java.io.IOException;
import java.io.InputStream;

public class InternalDataPack extends DataPack {

    public InternalDataPack() throws IOException {
        super("internal");
        loadMetadata();
        loadDataList();
    }

    public byte[] getEntry(String name) throws IOException {
        try (InputStream inputStream = InternalDataPack.class.getResourceAsStream("/internal/" + name)) {
            return inputStream == null ? null : inputStream.readAllBytes();
        }
    }
}
