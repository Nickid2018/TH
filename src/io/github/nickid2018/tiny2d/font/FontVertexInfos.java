package io.github.nickid2018.tiny2d.font;

public class FontVertexInfos {

    public int codepoint;

    public FontAtlas atlas;

    /* Font Texture Position in [0,1] */
    public float minU;
    public float minV;
    public float maxU;
    public float maxV;

    /* Offset in pixel */
    public float leftBearing;
    public float advanceWidth;
    public float topSide;
    public int width;
    public int height;

    public FontVertexInfos() {
    }

    public FontVertexInfos(int codepoint, FontAtlas atlas, float minU, float minV, float maxU, float maxV,
                           float leftBearing, float advanceWidth, float topSide,
                           int width, int height) {
        this.codepoint = codepoint;
        this.atlas = atlas;
        this.minU = minU;
        this.minV = minV;
        this.maxU = maxU;
        this.maxV = maxV;
        this.leftBearing = leftBearing;
        this.advanceWidth = advanceWidth;
        this.topSide = topSide;
        this.width = width;
        this.height = height;
    }
}

