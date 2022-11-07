package io.github.nickid2018.tiny2d.buffer;

import com.google.common.base.Preconditions;

import java.util.Set;

public class VertexAttributeList {

    public static final VertexAttributeList TEXTURE_2D =
            new VertexAttributeList(VertexAttribute.POSITION_2D, VertexAttribute.UV);
    public static final VertexAttributeList COLOR_TEXTURE_2D =
            new VertexAttributeList(VertexAttribute.POSITION_2D, VertexAttribute.COLOR, VertexAttribute.UV);
    public static final VertexAttributeList TEXTURE =
            new VertexAttributeList(VertexAttribute.POSITION, VertexAttribute.UV);
    public static final VertexAttributeList COLOR_TEXTURE =
            new VertexAttributeList(VertexAttribute.POSITION, VertexAttribute.COLOR, VertexAttribute.UV);

    private final VertexAttribute[] attributes;
    private final int[] offsets;
    private final int stride;

    public VertexAttributeList(VertexAttribute... attributes) {
        Preconditions.checkArgument(attributes.length > 0, "attributes must not be empty");
        Set.of(attributes);

        this.attributes = attributes;
        offsets = new int[attributes.length];
        int offset = 0;
        for (int i = 0; i < attributes.length; i++) {
            offsets[i] = offset;
            offset += attributes[i].size;
        }
        stride = offset;
    }

    public VertexAttribute[] getAttributes() {
        return attributes;
    }

    public int[] getOffsets() {
        return offsets;
    }

    public int getStride() {
        return stride;
    }
}
