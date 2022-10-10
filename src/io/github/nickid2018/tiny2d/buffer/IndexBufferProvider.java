package io.github.nickid2018.tiny2d.buffer;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public interface IndexBufferProvider {

    IntList getIndicesIndexed(IntList indices);

    IndexBufferProvider DEFAULT = indices -> {
        IntList list = new IntArrayList();
        for (int i = 0; i < indices.size() - 2; i++) {
            list.add(indices.getInt(i));
            list.add(indices.getInt(i + 1));
            list.add(indices.getInt(i + 2));
        }
        return list;
    };
}
