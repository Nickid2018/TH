package io.github.nickid2018.th.system.enemy;

import io.github.nickid2018.th.phys.Sphere;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.system.compute.Playground;

public abstract class Enemy implements HittableItem {

    protected final Playground playground;

    protected final Sphere hitSphere;
    protected int health;
    protected int maxHealth;

    protected boolean isDead;

    public Enemy(Playground playground, Sphere sphere, int health) {
        this.playground = playground;
        this.hitSphere = sphere;
        this.health = health;
        this.maxHealth = health;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public boolean isDead() {
        return isDead;
    }
}
