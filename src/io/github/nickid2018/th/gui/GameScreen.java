package io.github.nickid2018.th.gui;

import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import io.github.nickid2018.th.pack.PackManager;
import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.system.bullet.BulletBasicData;
import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.player.Player;
import io.github.nickid2018.th.system.player.PlayerBasicData;
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

        PlayerBasicData playerBasicData = PlayerBasicData.CODEC
                .parse(new Dynamic<>(JsonOps.INSTANCE, JsonParser.parseString(IOUtils.toString(
                        PackManager.createInputStream(ResourceLocation.fromString("players/player_0.json")), StandardCharsets.UTF_8))))
                .getOrThrow(false, error -> {});

        playground.setPlayer(new Player(playground, playerBasicData) {
        });

        BulletBasicData bulletBasicData = PackManager.createObject(
                ResourceLocation.fromString("bullets/pellet.json"), BulletBasicData.CODEC);

        BulletBasicData bulletBasicData2 = PackManager.createObject(
                ResourceLocation.fromString("bullets/tiny_petal.json"), BulletBasicData.CODEC);

        BulletBasicData bulletBasicData3 = PackManager.createObject(
                ResourceLocation.fromString("bullets/ball.json"), BulletBasicData.CODEC);

        BulletBasicData bulletBasicData4 = PackManager.createObject(
                ResourceLocation.fromString("bullets/orbs.json"), BulletBasicData.CODEC);

        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            playground.addBullet(new SimpleBullet(playground, bulletBasicData, "yellow",
                    new Vector2f(random.nextInt(Playground.PLAYGROUND_WIDTH), random.nextInt(Playground.PLAYGROUND_HEIGHT))));
            playground.addBullet(new SimpleBullet(playground, bulletBasicData2, "green",
                    new Vector2f(random.nextInt(Playground.PLAYGROUND_WIDTH), random.nextInt(Playground.PLAYGROUND_HEIGHT))));
            playground.addBullet(new SimpleBullet(playground, bulletBasicData3, "red",
                    new Vector2f(random.nextInt(Playground.PLAYGROUND_WIDTH), random.nextInt(Playground.PLAYGROUND_HEIGHT))));
            playground.addBullet(new SimpleBullet(playground, bulletBasicData4, "blue",
                    new Vector2f(random.nextInt(Playground.PLAYGROUND_WIDTH), random.nextInt(Playground.PLAYGROUND_HEIGHT))));
        }

        setNowFocus(playGroundGui);
    }

    private static class SimpleBullet extends Bullet {

        public SimpleBullet(Playground playground, BulletBasicData bulletBasicData, String variant, Vector2f position) {
            super(playground, bulletBasicData, variant, position);
            renderAngle = (float) Math.random();
        }

        @Override
        public void tick(long tickTime) {
            sphere.move((float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1);
            renderAngle += 0.1;
        }
    }

    @Override
    public void render(GuiRenderContext context) {
        playGroundGui.render(context);
    }
}
