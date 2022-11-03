package io.github.nickid2018.th.phys;

import io.github.nickid2018.tiny2d.math.AABB;
import lombok.AllArgsConstructor;
import org.joml.Vector2f;

@AllArgsConstructor
public class Sphere {

    public float x;
    public float y;
    public float r;

    public Sphere(Vector2f position, float radius) {
        this(position.x, position.y, radius);
    }

    public boolean orthogonalWith(Sphere another) {
        float dx = another.x - x;
        float dy = another.y - y;
        return dx * dx + dy * dy < r * r + another.r * another.r;
    }

    public boolean crossWith(Sphere another) {
        float dx = another.x - x;
        float dy = another.y - y;
        return dx * dx + dy * dy < (r + another.r) * (r + another.r);
    }

    public AABB getOuterAABB() {
        return AABB.newAABB(x - r, y - r, x + r, y + r);
    }

    public void move(float hori, float vert) {
        x += hori;
        y += vert;
    }

    public void moveTo(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f getPosition() {
        return new Vector2f(x, y);
    }

    public void moveTo(Vector2f position) {
        this.x = position.x;
        this.y = position.y;
    }
}
