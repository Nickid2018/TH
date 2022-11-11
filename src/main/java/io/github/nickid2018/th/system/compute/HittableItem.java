package io.github.nickid2018.th.system.compute;

import io.github.nickid2018.th.phys.Sphere;

public interface HittableItem extends Tickable {

    Sphere getHitSphere();

    Playground getPlayground();

    default void disposeIfOutside() {
        if (getPlayground().isItemOutsidePlayground(this))
            dispose();
    }

    default void move(float x, float y) {
        getHitSphere().move(x, y);
    }

    default void dispose() {
        getPlayground().dispose(this);
    }
}
