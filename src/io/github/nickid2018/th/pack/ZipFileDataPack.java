package io.github.nickid2018.th.pack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileDataPack extends DataPack {

    private final ZipFile zipFile;

    public ZipFileDataPack(String file) throws IOException {
        this(new File(file));
    }

    public ZipFileDataPack(File zipFile) throws IOException {
        super(zipFile.getName());
        this.zipFile = new ZipFile(zipFile);
        loadMetadata();
        loadDataList();
    }

    public byte[] getEntry(String name) throws IOException {
        ZipEntry entry = zipFile.getEntry(name);
        if (entry == null)
            return null;
        try (InputStream inputStream = zipFile.getInputStream(entry)) {
            return inputStream.readAllBytes();
        }
    }

    @Override
    public void close() throws IOException {
        zipFile.close();
    }
}
