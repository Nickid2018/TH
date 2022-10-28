package io.github.nickid2018.th.system.player;

import io.github.nickid2018.th.phys.Sphere;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.system.compute.Playground;
import org.joml.Vector2f;

public abstract class Player implements HittableItem {

    protected final Playground playground;

    protected final Sphere sphere;
    protected final String name;

    protected Vector2f missedPosition;
    protected int missedTimeCount = 0;

    protected int bombCount = 3;
    protected int players = 3;

    public Player(Playground playground, Sphere sphere, String name) {
        this.playground = playground;
        this.sphere = sphere;
        this.name = name;
    }

    @Override
    public Sphere getHitSphere() {
        return sphere;
    }

    public Sphere sphere() {
        return sphere;
    }

    public String name() {
        return name;
    }

    public int getBombCount() {
        return bombCount;
    }

    public int getPlayers() {
        return players;
    }

    public void setBombCount(int bombCount) {
        this.bombCount = bombCount;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public void setMissed(Vector2f missedPosition) {
        this.missedPosition = missedPosition;
    }

    public int getMissedTimeCount() {
        return missedTimeCount;
    }

    public Vector2f getMissedPosition() {
        return missedPosition;
    }

    @Override
    public String toString() {
        return "Player[" +
                "sphere=" + sphere + ", " +
                "name=" + name + ']';
    }

}
