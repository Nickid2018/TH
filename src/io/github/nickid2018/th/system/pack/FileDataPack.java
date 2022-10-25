package io.github.nickid2018.th.system.pack;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

public class FileDataPack extends DataPack {

    private final ZipFile zipFile;

    public FileDataPack(String file) throws IOException {
        this(new ZipFile(file));
    }

    public FileDataPack(ZipFile zipFile) throws IOException {
        this.zipFile = zipFile;
        loadMetadata();
    }

    public byte[] getEntry(String name) throws IOException {
        try (InputStream inputStream = zipFile.getInputStream(zipFile.getEntry(name))) {
            return inputStream.readAllBytes();
        }
    }

    @Override
    public void close() throws IOException {
        zipFile.close();
    }
}
