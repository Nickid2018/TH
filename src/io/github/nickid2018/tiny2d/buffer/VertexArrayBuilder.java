package io.github.nickid2018.tiny2d.buffer;

import io.github.nickid2018.tiny2d.RenderThreadOnly;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class VertexArrayBuilder {

    private final VertexAttributeList list;
    private final IndexBufferProvider provider;
    private int now = 0;
    private int nowIndex = 0;
    private final FloatList buffer = new FloatArrayList();
    private final IntList indicesToIndex = new IntArrayList();
    private final IntList indexes = new IntArrayList();

    public VertexArrayBuilder(VertexAttributeList list, IndexBufferProvider provider) {
        this.list = list;
        this.provider = provider;
    }

    private void check(VertexAttribute attribute) {
        if (list.getAttributes()[now] != attribute)
            throw new IllegalArgumentException("The attribute is not match");
        now++;
    }

    public VertexArrayBuilder pos(float x, float y, float z) {
        check(VertexAttribute.POSITION);
        buffer.add(x);
        buffer.add(y);
        buffer.add(z);
        return this;
    }

    public VertexArrayBuilder pos(float x, float y) {
        check(VertexAttribute.POSITION_2D);
        buffer.add(x);
        buffer.add(y);
        return this;
    }

    public VertexArrayBuilder color(float r, float g, float b) {
        check(VertexAttribute.COLOR);
        buffer.add(r);
        buffer.add(g);
        buffer.add(b);
        return this;
    }

    public VertexArrayBuilder color(float r, float g, float b, float a) {
        check(VertexAttribute.COLOR_RGBA);
        buffer.add(r);
        buffer.add(g);
        buffer.add(b);
        buffer.add(a);
        return this;
    }

    public VertexArrayBuilder uv(float u, float v) {
        check(VertexAttribute.UV);
        buffer.add(u);
        buffer.add(v);
        return this;
    }

    public VertexArrayBuilder end() {
        if (now != list.getAttributes().length)
            throw new IllegalArgumentException("The attributes is not finished");
        now = 0;
        indicesToIndex.add(nowIndex);
        nowIndex++;
        return this;
    }

    public VertexArrayBuilder makeIndex() {
        if (now != 0)
            throw new IllegalArgumentException("The vertexes is not finished");
        indexes.addAll(provider.getIndicesIndexed(indicesToIndex));
        indicesToIndex.clear();
        return this;
    }

    @RenderThreadOnly
    public VertexArray build() {
        if (indicesToIndex.size() != 0)
            makeIndex();
        return new VertexArray(list, buffer, indexes);
    }
}
