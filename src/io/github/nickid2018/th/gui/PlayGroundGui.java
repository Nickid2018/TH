package io.github.nickid2018.th.gui;

import io.github.nickid2018.th.pack.PackManager;
import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.player.Player;
import io.github.nickid2018.th.util.ResourceLocation;
import io.github.nickid2018.tiny2d.buffer.*;
import io.github.nickid2018.tiny2d.gui.GuiRenderContext;
import io.github.nickid2018.tiny2d.gui.KeyboardInput;
import io.github.nickid2018.tiny2d.gui.RenderComponent;
import io.github.nickid2018.tiny2d.shader.ShaderProgram;
import io.github.nickid2018.tiny2d.shader.ShaderSource;
import io.github.nickid2018.tiny2d.shader.ShaderType;
import io.github.nickid2018.tiny2d.shader.Uniform;
import io.github.nickid2018.tiny2d.sound.OggAudioStream;
import io.github.nickid2018.tiny2d.sound.SoundBuffer;
import io.github.nickid2018.tiny2d.sound.SoundInstance;
import io.github.nickid2018.tiny2d.texture.Image;
import io.github.nickid2018.tiny2d.texture.StaticTexture;
import io.github.nickid2018.tiny2d.texture.Texture;
import io.github.nickid2018.tiny2d.util.LazyLoadValue;
import io.github.nickid2018.tiny2d.window.Window;
import lombok.Getter;
import org.joml.Matrix2f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class PlayGroundGui extends RenderComponent implements KeyboardInput {

    // test
    public static SoundInstance soundInstanceMissed;
    public static Texture textureFront;

    public static final LazyLoadValue<ShaderProgram> MISSED = new LazyLoadValue<>(() -> {
        ShaderProgram program = new ShaderProgram();
        try {
            program.attachShader(ShaderSource.createShader(ShaderType.VERTEX, "/shaders/tex_color.vsh"));
            program.attachShader(ShaderSource.createShader(ShaderType.FRAGMENT, "/shaders/missed.fsh"));
            program.link();
            program.addUniform("transform");
            program.addUniform("missedPosition");
            program.addUniform("missedTimeCount");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return program;
    });

    private final FrameBuffer buffer;
    private final Playground playground;

    @Getter
    private final int guiScale;

    public PlayGroundGui(Window window, int x, int y, int width, int height, Playground playground, int guiScale) {
        super(window, x, y, width, height);
        this.playground = playground;
        this.guiScale = guiScale;
        buffer = new FrameBuffer(Playground.PLAYGROUND_WIDTH * guiScale, Playground.PLAYGROUND_HEIGHT * guiScale);
        soundInstanceMissed = SoundInstance.create();
        try {
            SoundBuffer buffer = new SoundBuffer(new OggAudioStream(PackManager.createInputStream(
                    ResourceLocation.fromString("sounds/effect/se_pldead00.ogg"))));
            buffer.init();
            soundInstanceMissed.attachStaticBuffer(buffer);

            textureFront = new StaticTexture(Image.read(PackManager.createInputStream(
                    ResourceLocation.fromString("textures/effect/front.png"))), 4);
            textureFront.update();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onWindowResize() {
    }

    private void renderBackground(GuiRenderContext context) {

    }

    private void renderPlayerAndEnemy(GuiRenderContext context) {
        // Player
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Player player = playground.getPlayer();
        Matrix4f matrix = new Matrix4f();
        float x = player.getHitSphere().x / Playground.PLAYGROUND_WIDTH * 2 - 1;
        float y = player.getHitSphere().y / Playground.PLAYGROUND_HEIGHT * 2 - 1;
        matrix.translate(x, y, 0);
        matrix.scale(guiScale, guiScale, 1);

        int renderTurnTick = player.getRenderTurnTick();
        Texture texture;
        VertexArray array;
        if (renderTurnTick == 0) {
            List<Texture> textures = player.getBasicData().renderData().getStaticTextures();
            int index = (int) ((playground.getTickTime() / 8) % textures.size());
            texture = textures.get(index);
            array = player.getBasicData().renderData().getStaticVertexArrays().get(index);
        } else if (renderTurnTick > 0) {
            List<Texture> textures = player.getBasicData().renderData().getRightTextures();
            int shouldBe = renderTurnTick % (textures.size() * 2);
            int index = (renderTurnTick / 2) % textures.size();
            texture = textures.get(index);
            array = player.getBasicData().renderData().getRightVertexArrays().get(index);
            player.setRenderTurnTick(shouldBe);
        } else {
            List<Texture> textures = player.getBasicData().renderData().getLeftTextures();
            int shouldBe = renderTurnTick % (textures.size() * 2);
            int index = Math.abs((renderTurnTick / 2) % textures.size());
            texture = textures.get(index);
            array = player.getBasicData().renderData().getLeftVertexArrays().get(index);
            player.setRenderTurnTick(shouldBe);
        }

        ShaderProgram program = Shaders.TEX_COLOR.get();
        program.use();
        program.getUniform("transform").setMatrix4f(matrix);
        program.getUniform("color").set3fv(1, 1, 1);
        texture.bind();
        array.draw();

        GL11.glDisable(GL11.GL_BLEND);
    }

    private void renderBullets(GuiRenderContext context) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        playground.getBullets().stream()
                .sorted(Comparator.comparing(Bullet::getBulletBasicData))
                .forEach(bullet -> {
            Matrix4f matrix = new Matrix4f();
            float x = bullet.getHitSphere().x / Playground.PLAYGROUND_WIDTH * 2 - 1;
            float y = bullet.getHitSphere().y / Playground.PLAYGROUND_HEIGHT * 2 - 1;

            if (bullet.getBulletBasicData().isHasRenderAngle())
                matrix.rotate(bullet.getRenderAngle(), 0, 0, 1);
            matrix.translate(x, y, 0);
            matrix.scale(guiScale, guiScale, 1);

            ShaderProgram program = Shaders.TEX_COLOR.get();
            program.use();
            program.getUniform("transform").setMatrix4f(matrix);
            if (bullet.getBulletBasicData().isHasTint())
                program.getUniform("color").set3fv(bullet.getColor());
            else
                program.getUniform("color").set3fv(1, 1, 1);

            bullet.getBulletBasicData().getTexture(bullet.getVariant()).bind();
            bullet.getBulletBasicData().getVertexArray(bullet.getVariant()).draw();
        });
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void renderForeground(GuiRenderContext context) {
        Player player = playground.getPlayer();
        if (player.isSlowMode()) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            Matrix4f matrix = new Matrix4f();
            float x = player.getHitSphere().x / Playground.PLAYGROUND_WIDTH * 2 - 1;
            float y = player.getHitSphere().y / Playground.PLAYGROUND_HEIGHT * 2 - 1;
            matrix.translate(x, y, 0);
            matrix.scale(guiScale, guiScale, 1);

            ShaderProgram program = Shaders.TEX_COLOR.get();
            program.use();
            program.getUniform("transform").setMatrix4f(matrix);
            program.getUniform("color").set3fv(1, 1, 1);

            textureFront.bind();

            Vector2f x1y1 = new Vector2f(-32f, -32f);
            Vector2f x1y2 = new Vector2f(-32f, 32f);
            Vector2f x2y2 = new Vector2f(32f, 32f);
            Vector2f x2y1 = new Vector2f(32f, -32f);

            Matrix2f transform = new Matrix2f();
            transform.rotate((float) Math.toRadians(playground.getTickTime() * 2));
            x1y1.mul(transform);
            x1y2.mul(transform);
            x2y2.mul(transform);
            x2y1.mul(transform);

            VertexArrayBuilder builder = new VertexArrayBuilder(VertexAttributeList.TEXTURE_2D, IndexBufferProvider.DEFAULT);
            builder.pos(x1y1.x / Playground.PLAYGROUND_WIDTH, x1y1.y / Playground.PLAYGROUND_HEIGHT).uv(0, 0).end();
            builder.pos(x2y1.x / Playground.PLAYGROUND_WIDTH, x2y1.y / Playground.PLAYGROUND_HEIGHT).uv(1, 0).end();
            builder.pos(x1y2.x / Playground.PLAYGROUND_WIDTH, x1y2.y / Playground.PLAYGROUND_HEIGHT).uv(0, 1).end();
            builder.pos(x2y2.x / Playground.PLAYGROUND_WIDTH, x2y2.y / Playground.PLAYGROUND_HEIGHT).uv(1, 1).end();
            builder.build().draw();
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    private void useShader() {
        // Normal -> tex_color
        // Miss -> missed
        // Pause -> blur
        if (playground.getPlayer().getMissedPosition() != null) {
            ShaderProgram program = MISSED.get();
            program.use();
            program.getUniform("transform").setMatrix4f(Uniform.IDENTITY_MATRIX);
            program.getUniform("missedPosition").set2fv(playground.getPlayer().getMissedPosition());
            program.getUniform("missedTimeCount").setFloat(playground.getPlayer().getMissedTimeCount());
        } else {
            ShaderProgram program = Shaders.TEX_COLOR.get();
            program.use();
            program.getUniform("transform").setMatrix4f(Uniform.IDENTITY_MATRIX);
            program.getUniform("color").set3fv(1, 1, 1);
        }
    }

    @Override
    public void render(GuiRenderContext context) {
        playground.update();

        buffer.bind();
        buffer.clear();
        renderBackground(context);
        renderPlayerAndEnemy(context);
        renderBullets(context);
        renderForeground(context);
        buffer.unbind();

        context.currentFrameBuffer().bind();
        useShader();
        buffer.bindTexture();
        VertexArray array = createWindow2DTexture(context.window());
        array.draw();
        array.destroy();
    }

    @Override
    public void onDispose() {
        buffer.delete();
    }

    @Override
    public void onKeyTyped(char c) {
    }

    @Override
    public void onKeyPressed(int key) {
        playground.getPlayer().setMoveFlag(switch (key) {
            case GLFW.GLFW_KEY_LEFT -> playground.getPlayer().getMoveFlag() | 1;
            case GLFW.GLFW_KEY_RIGHT -> playground.getPlayer().getMoveFlag() | 2;
            case GLFW.GLFW_KEY_UP -> playground.getPlayer().getMoveFlag() | 4;
            case GLFW.GLFW_KEY_DOWN -> playground.getPlayer().getMoveFlag() | 8;
            default -> playground.getPlayer().getMoveFlag();
        });
        if (key == GLFW.GLFW_KEY_LEFT_SHIFT)
            playground.getPlayer().setSlowMode(true);
    }

    @Override
    public void onKeyReleased(int key) {
        playground.getPlayer().setMoveFlag(switch (key) {
            case GLFW.GLFW_KEY_LEFT -> playground.getPlayer().getMoveFlag() & ~1;
            case GLFW.GLFW_KEY_RIGHT -> playground.getPlayer().getMoveFlag() & ~2;
            case GLFW.GLFW_KEY_UP -> playground.getPlayer().getMoveFlag() & ~4;
            case GLFW.GLFW_KEY_DOWN -> playground.getPlayer().getMoveFlag() & ~8;
            default -> playground.getPlayer().getMoveFlag();
        });
        if (key == GLFW.GLFW_KEY_LEFT_SHIFT)
            playground.getPlayer().setSlowMode(false);
    }
}
