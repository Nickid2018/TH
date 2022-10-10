package io.github.nickid2018.tiny2d.math;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public class AABB {

    public static final AABB AABB_NULL = newAABB(0, 0, 0, 0);
    public double minX;
    public double minY;
    public double maxX;
    public double maxY;

    public static AABB newAABB(double minX, double minY, double maxX, double maxY) {
        return newAABB(minX, minY, maxX, maxY, false);
    }

    public static AABB newAABB(double minX, double minY, double maxX, double maxY, boolean checkValid) {
        AABB aabb = new AABB();
        aabb.minX = minX;
        aabb.minY = minY;
        aabb.maxX = maxX;
        aabb.maxY = maxY;
        if (checkValid)
            Preconditions.checkArgument(!aabb.isValid(), "Invalid AABB");
        return aabb.validate();
    }

    public boolean isValid() {
        return minX <= maxX && minY <= maxY;
    }

    public AABB validate() {
        if (minX > maxX) {
            double tmp = minX;
            minX = maxX;
            maxX = tmp;
        }
        if (minY > maxY) {
            double tmp = minY;
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

    public double getWidth() {
        validate();
        return maxX - minX;
    }

    public double getHeight() {
        validate();
        return maxY - minY;
    }

    public AABB newCopy() {
        validate();
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
