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

    public AABB getOuterAABB() {
        return AABB.newAABB(x - r, y - r, x + r, y + r);
    }
}
