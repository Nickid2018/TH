package io.github.nickid2018.th.phys;

public class Sphere {

    public float x;
    public float y;
    public float r;

    public Sphere(float x, float y, float r){
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public boolean orthogonalWith(Sphere another) {
        float dx = another.x - x;
        float dy = another.y - y;
        return dx * dx + dy * dy < r * r + another.r * another.r;
    }
}
