package io.github.nickid2018.th.pack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileDataPack extends DataPack {

    private final File root;

    public FileDataPack(File root) {
        super(root.getName());
        this.root = root;
    }

    @Override
    public byte[] getEntry(String name) throws IOException {
        File file = new File(root.getAbsolutePath() + "/" + name);
        if (!file.exists())
            return null;
        try (FileInputStream fis = new FileInputStream(file)) {
            return fis.readAllBytes();
        }
    }
}
