package io.github.nickid2018.th.system.player;

import io.github.nickid2018.th.phys.Sphere;
import io.github.nickid2018.tiny2d.math.Vec2f;
import io.github.nickid2018.th.system.HittableItem;

public abstract class Player implements HittableItem {

    private final Sphere sphere;
    private final String name;

    private Vec2f missedPosition;
    private int missedTimeCount = 0;

    private int bombCount = 3;
    private int players = 3;

    public Player(Sphere sphere, String name) {
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

    public void setMissed(Vec2f missedPosition) {
        this.missedPosition = missedPosition;
    }

    public int getMissedTimeCount() {
        return missedTimeCount;
    }

    public Vec2f getMissedPosition() {
        return missedPosition;
    }

    @Override
    public String toString() {
        return "Player[" +
                "sphere=" + sphere + ", " +
                "name=" + name + ']';
    }

}
