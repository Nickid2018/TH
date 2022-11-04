package io.github.nickid2018.th.system.player;

import io.github.nickid2018.th.phys.Sphere;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.system.compute.Playground;
import lombok.Getter;
import lombok.Setter;
import org.joml.Math;
import org.joml.Vector2f;

public abstract class Player implements HittableItem {

    public static final float SQRT_2 = Math.sqrt(2);

    @Getter
    protected final Playground playground;

    @Getter
    private final PlayerBasicData basicData;
    protected final Sphere sphere;

    // ---- Miss ----
    @Getter
    @Setter
    protected Vector2f missedPosition;
    @Getter
    @Setter
    protected int missedTimeCount = 0;
    // ---- Resource ----
    @Getter
    @Setter
    protected int bombCount = 3;
    @Getter
    @Setter
    protected int players = 3;
    @Getter
    @Setter
    protected int grazeCount = 0;
    // ---- Render And Control ----
    @Getter
    @Setter
    protected int renderTurnTick = 0;
    @Getter
    @Setter
    protected boolean slowMode = false;

    @Getter
    @Setter
    // 0: static, 1: left, 2: right, 4: up, 8: down
    protected int moveFlag = 0;

    public Player(Playground playground, PlayerBasicData playerBasicData) {
        this.playground = playground;
        this.basicData = playerBasicData;
        this.sphere = new Sphere(Playground.PLAYGROUND_WIDTH / 2f, 400, playerBasicData.hitRadius());
    }

    @Override
    public Sphere getHitSphere() {
        return sphere;
    }

    @Override
    public void tick(long tickTime) {
        switch (moveFlag & 3) {
            case 0, 3 -> {
                if (renderTurnTick != 0)
                    renderTurnTick += renderTurnTick > 0 ? -1 : 1;
            }
            case 1 -> renderTurnTick = Math.min(renderTurnTick - 1, -1);
            case 2 -> renderTurnTick = Math.max(renderTurnTick + 1, 1);
        }

        boolean validHorizontal = (((moveFlag & 2) >> 1) ^ (moveFlag & 1)) != 0;
        boolean validVertical = (((moveFlag & 8) >> 1) ^ (moveFlag & 4)) != 0;

        float speed = slowMode ? basicData.slowSpeed() : basicData.normalSpeed();
        if (validHorizontal && validVertical) {
            float hori = ((moveFlag & 1) == 0 ? 1 : -1) * speed / SQRT_2;
            float vert = ((moveFlag & 4) == 0 ? 1 : -1) * speed / SQRT_2;
            sphere.move(hori, vert);
        } else if (validHorizontal)
            sphere.move(((moveFlag & 1) == 0 ? 1 : -1) * speed, 0);
        else if (validVertical)
            sphere.move(0, ((moveFlag & 4) == 0 ? 1 : -1) * speed);

        sphere.x = Math.clamp(8, Playground.PLAYGROUND_WIDTH - 12, sphere.x);
        sphere.y = Math.clamp(12, Playground.PLAYGROUND_HEIGHT - 12, sphere.y);
    }
}
