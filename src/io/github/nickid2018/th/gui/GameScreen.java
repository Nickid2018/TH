package io.github.nickid2018.th.gui;

import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import io.github.nickid2018.th.pack.PackManager;
import io.github.nickid2018.th.phys.Sphere;
import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.system.bullet.BulletBasicData;
import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.player.Player;
import io.github.nickid2018.th.util.ResourceLocation;
import io.github.nickid2018.tiny2d.gui.GuiRenderContext;
import io.github.nickid2018.tiny2d.gui.Screen;
import io.github.nickid2018.tiny2d.window.Window;
import org.apache.commons.io.IOUtils;
import org.joml.Random;
import org.joml.Vector2f;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GameScreen extends Screen {

    private final PlayGroundGui playGroundGui;

    public GameScreen(Window window) throws IOException {
        super(window);
        Playground playground;
        playGroundGui = new PlayGroundGui(window, 10, 10,
                Playground.PLAYGROUND_WIDTH * 2, Playground.PLAYGROUND_HEIGHT * 2,
                playground = new Playground(), 2);
        playground.setPlayer(new Player(playground, new Sphere(0, 0, 1), "test") {
            @Override
            public void tick(long tickTime) {

            }
        });
        BulletBasicData bulletBasicData = BulletBasicData.CODEC
                .parse(new Dynamic<>(JsonOps.INSTANCE, JsonParser.parseString(IOUtils.toString(
                        PackManager.createInputStream(ResourceLocation.fromString("bullets/point.json")), StandardCharsets.UTF_8))))
                .getOrThrow(false, error -> {});

        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            playground.addBullet(new SimpleBullet(playground, bulletBasicData, "orange",
                    new Vector2f(random.nextInt(Playground.PLAYGROUND_WIDTH), random.nextInt(Playground.PLAYGROUND_HEIGHT))));
        }
    }

    private static class SimpleBullet extends Bullet {

        public SimpleBullet(Playground playground, BulletBasicData bulletBasicData, String variant, Vector2f position) {
            super(playground, bulletBasicData, variant, position);
        }

        @Override
        public void tick(long tickTime) {
        }
    }

    @Override
    public void render(GuiRenderContext context) {
        playGroundGui.render(context);
    }
}
