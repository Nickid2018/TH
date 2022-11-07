package io.github.nickid2018.th.system.compute;

import io.github.nickid2018.th.phys.Sphere;

public interface HittableItem extends Tickable {

    Sphere getHitSphere();

    Playground getPlayground();
}
