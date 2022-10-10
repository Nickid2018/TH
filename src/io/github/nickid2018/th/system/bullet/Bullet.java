package io.github.nickid2018.th.system.bullet;

import io.github.nickid2018.th.phys.Sphere;
import io.github.nickid2018.th.system.HittableItem;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;

public final class Bullet implements HittableItem {

    private final Sphere sphere;
    private final Object2FloatMap<String> properties;
    private long lifeTime;
    private Path path;

    public Bullet(Sphere sphere, Object2FloatMap<String> properties) {
        this.sphere = sphere;
        this.properties = properties;
    }

    @Override
    public Sphere getHitSphere() {
        return sphere;
    }

    @Override
    public void updatePosition(float x, float y) {
        sphere.x = x;
        sphere.y = y;
    }

    @Override
    public void updateRadius(float r) {
        sphere.r = r;
    }

    public Sphere sphere() {
        return sphere;
    }

    public Object2FloatMap<String> properties() {
        return properties;
    }

    public void update() {
        lifeTime++;

    }

    public long getLifeTime() {
        return lifeTime;
    }

    @Override
    public String toString() {
        return "Bullet@" + hashCode() + "[" +
                "sphere=" + sphere + ", " +
                "properties=" + properties + ']';
    }

}
