package io.github.nickid2018.th.system.enemy;

import io.github.nickid2018.th.phys.Sphere;
import io.github.nickid2018.th.system.compute.Playground;

public abstract class Boss extends Enemy {

    public Boss(Playground playground, Sphere sphere, int health) {
        super(playground, sphere, health);
    }
}
