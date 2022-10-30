package io.github.nickid2018.th.pack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileDataPack extends DataPack {

    private final File root;

    public FileDataPack(File root) {
        super(root.getName());
        this.root = root;
    }

    @Override
    public boolean hasEntry(String name) {
        return new File(root.getAbsolutePath() + "/" + name).exists();
    }

    @Override
    public InputStream getEntryInStream(String name) throws IOException {
        File file = new File(root.getAbsolutePath() + "/" + name);
        return new FileInputStream(file);
    }
}
