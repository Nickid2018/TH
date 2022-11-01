package io.github.nickid2018.th.gui;

import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import io.github.nickid2018.th.pack.PackManager;
import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.player.Player;
import io.github.nickid2018.th.system.player.PlayerBasicData;
import io.github.nickid2018.th.util.ResourceLocation;
import io.github.nickid2018.tiny2d.gui.GuiRenderContext;
import io.github.nickid2018.tiny2d.gui.Screen;
import io.github.nickid2018.tiny2d.gui.components.TextComponent;
import io.github.nickid2018.tiny2d.window.Window;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GameScreen extends Screen {

    private final PlayGroundGui playGroundGui;

    private TextComponent fpsText;
    private TextComponent missText;
    private TextComponent grazeText;

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

        fpsText = TextComponent.create(window, "FPS: 0", 32, 800, 10);
        missText = TextComponent.create(window, "Miss: 0", 32, 800, 50);
        grazeText = TextComponent.create(window, "Graze: 0", 32, 800, 90);

        setNowFocus(playGroundGui);

        addComponent("fps", fpsText);
        addComponent("miss", missText);
        addComponent("graze", grazeText);
    }

    @Override
    public void render(GuiRenderContext context) {
        playGroundGui.render(context);
        fpsText.setText("FPS: " + window.getFPS());
        missText.setText("Miss: " + (3 - playGroundGui.getPlayground().getPlayer().getPlayers()));
        grazeText.setText("Graze: " + playGroundGui.getPlayground().getPlayer().getGrazeCount());
        super.render(context);
    }
}
