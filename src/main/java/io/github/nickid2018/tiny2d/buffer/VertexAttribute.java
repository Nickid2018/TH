package io.github.nickid2018.tiny2d.buffer;

public enum VertexAttribute {

    POSITION(3),

    POSITION_2D(2),
    COLOR(3),
    COLOR_RGBA(4),
    UV(2);

    public final int size;

    VertexAttribute(int size) {
        this.size = size;
    }
}
