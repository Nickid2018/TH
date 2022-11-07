package io.github.nickid2018.tiny2d.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;

import javax.annotation.Nonnull;

public class AABB {

    public static final Codec<AABB> MIN_MAX_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("minX").forGetter(AABB::getMinX),
            Codec.FLOAT.fieldOf("minY").forGetter(AABB::getMinY),
            Codec.FLOAT.fieldOf("maxX").forGetter(AABB::getMaxX),
            Codec.FLOAT.fieldOf("maxY").forGetter(AABB::getMaxY)
    ).apply(instance, AABB::newAABB));

    public static final Codec<AABB> WIDTH_HEIGHT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("minX").forGetter(AABB::getMinX),
            Codec.FLOAT.fieldOf("minY").forGetter(AABB::getMinY),
            Codec.FLOAT.fieldOf("width").forGetter(AABB::getWidth),
            Codec.FLOAT.fieldOf("height").forGetter(AABB::getHeight)
    ).apply(instance, AABB::newAABBSize));

    public static final AABB AABB_NULL = newAABB(0, 0, 0, 0);

    @Getter
    public float minX;
    @Getter
    public float minY;
    @Getter
    public float maxX;
    @Getter
    public float maxY;

    public static AABB newAABB(float minX, float minY, float maxX, float maxY) {
        return newAABB(minX, minY, maxX, maxY, false);
    }

    public static AABB newAABBSize(float minX, float minY, float width, float height) {
        return newAABB(minX, minY, minX + width, minY + height, false);
    }

    public static AABB newAABB(float minX, float minY, float maxX, float maxY, boolean checkValid) {
        AABB aabb = new AABB();
        aabb.minX = minX;
        aabb.minY = minY;
        aabb.maxX = maxX;
        aabb.maxY = maxY;
        return checkValid ? aabb.validate() : aabb;
    }

    public boolean isValid() {
        return minX <= maxX && minY <= maxY;
    }

    public AABB validate() {
        if (minX > maxX) {
            float tmp = minX;
            minX = maxX;
            maxX = tmp;
        }
        if (minY > maxY) {
            float tmp = minY;
            minY = maxY;
            maxY = tmp;
        }
        return this;
    }

    public AABB move(double x, double y) {
        minX += x;
        minY += y;
        maxX += x;
        maxY += y;
        return validate();
    }

    public AABB inflate(double x, double y) {
        validate();
        minX -= x;
        minY -= y;
        maxX += x;
        maxY += y;
        return validate();
    }

    public AABB inflate(double d) {
        return inflate(d, d);
    }

    public AABB deflate(double d) {
        return inflate(-d);
    }

    public boolean intersects(AABB other) {
        validate();
        other.validate();
        return minX < other.maxX && maxX > other.minX && minY < other.maxY && maxY > other.minY;
    }

    public AABB intersect(AABB other) {
        if (!intersects(other))
            return AABB_NULL.newCopy();
        AABB aabb = new AABB();
        aabb.minX = Math.max(minX, other.minX);
        aabb.minY = Math.max(minY, other.minY);
        aabb.maxX = Math.min(maxX, other.maxX);
        aabb.maxY = Math.min(maxY, other.maxY);
        return aabb;
    }

    public AABB boundWith(AABB other) {
        validate();
        other.validate();
        AABB aabb = new AABB();
        aabb.minX = Math.min(minX, other.minX);
        aabb.minY = Math.min(minY, other.minY);
        aabb.maxX = Math.max(maxX, other.maxX);
        aabb.maxY = Math.max(maxY, other.maxY);
        return aabb;
    }

    public boolean contains(double x, double y) {
        return minX <= x && x <= maxX && minY <= y && y <= maxY;
    }

    public float getWidth() {
        return maxX - minX;
    }

    public float getHeight() {
        return maxY - minY;
    }

    public AABB newCopy() {
        AABB aabb = new AABB();
        aabb.minX = minX;
        aabb.minY = minY;
        aabb.maxX = maxX;
        aabb.maxY = maxY;
        return aabb;
    }

    public boolean equals(@Nonnull AABB other) {
        return minX == other.minX && minY == other.minY && maxX == other.maxX && maxY == other.maxY;
    }

    public boolean equals(Object other) {
        return other instanceof AABB && equals((AABB) other);
    }
}
