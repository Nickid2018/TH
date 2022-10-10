package io.github.nickid2018.th.system;

import io.github.nickid2018.th.phys.Sphere;

public interface HittableItem {

    Sphere getHitSphere();

    void updatePosition(float x, float y);

    void updateRadius(float r);
}
