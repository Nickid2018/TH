package io.github.nickid2018.th.pack;

import java.io.IOException;
import java.io.InputStream;

public class InternalDataPack extends DataPack {

    public InternalDataPack() throws IOException {
        super("internal");
        loadMetadata();
        loadDataList();
    }

    public InputStream getEntryInStream(String name) {
        return InternalDataPack.class.getResourceAsStream("/internal/" + name);
    }
}
