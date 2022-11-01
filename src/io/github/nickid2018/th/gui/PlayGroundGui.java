package io.github.nickid2018.th.gui;

import io.github.nickid2018.th.pack.PackManager;
import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.system.compute.HittableItem;
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
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayGroundGui extends RenderComponent implements KeyboardInput {

    public static final int SPRITE_SIZE = 128;

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

    private final float spriteScaleX;
    private final float spriteScaleY;

    public PlayGroundGui(Window window, int x, int y, int width, int height, Playground playground, int guiScale) {
        super(window, x, y, width, height);
        this.playground = playground;
        this.guiScale = guiScale;
        buffer = new FrameBuffer(Playground.PLAYGROUND_WIDTH * guiScale, Playground.PLAYGROUND_HEIGHT * guiScale);
        spriteScaleX = PlayGroundGui.SPRITE_SIZE / (float) Playground.PLAYGROUND_WIDTH * 2 * guiScale;
        spriteScaleY = PlayGroundGui.SPRITE_SIZE / (float) Playground.PLAYGROUND_HEIGHT * 2 * guiScale;

        soundInstanceMissed = SoundInstance.create();
        try {
            SoundBuffer buffer = new SoundBuffer(new OggAudioStream(PackManager.createInputStream(ResourceLocation.fromString("sounds/effect/se_pldead00.ogg"))));
            buffer.init();
            soundInstanceMissed.attachStaticBuffer(buffer);

            textureFront = new StaticTexture(Image.read(PackManager.createInputStream(ResourceLocation.fromString("textures/effect/front.png"))), 4);
            textureFront.update();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Matrix4f getHittableItemMatrix(HittableItem item) {
        Matrix4f matrix = new Matrix4f();
        float x = item.getHitSphere().x / Playground.PLAYGROUND_WIDTH * 2 - 1;
        float y = item.getHitSphere().y / Playground.PLAYGROUND_HEIGHT * 2 - 1;
        matrix.translate(x, y, 0);
        matrix.scale(spriteScaleX, spriteScaleY, 1);
        return matrix;
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
            int shouldBe = Math.min(renderTurnTick, textures.size() * 2 - 2);
            int index = shouldBe / 2;
            texture = textures.get(index);
            array = player.getBasicData().renderData().getRightVertexArrays().get(index);
            player.setRenderTurnTick(shouldBe);
        } else {
            List<Texture> textures = player.getBasicData().renderData().getLeftTextures();
            int shouldBe = Math.max(renderTurnTick, -textures.size() * 2 + 2);
            int index = Math.abs(shouldBe / 2);
            texture = textures.get(index);
            array = player.getBasicData().renderData().getLeftVertexArrays().get(index);
            player.setRenderTurnTick(shouldBe);
        }

        if (playground.getTickTime() % 60 == 0) System.out.println(window.getFPS());

        ShaderProgram program = Shaders.TEX_COLOR.get();
        program.use();
        program.getUniform("transform").setMatrix4f(getHittableItemMatrix(player));
        program.getUniform("color").set3fv(1, 1, 1);
        texture.bind();
        array.draw();

        GL11.glDisable(GL11.GL_BLEND);
    }

    private void drawInstanced(ShaderProgram program, Bullet bullet, List<Matrix4f> matrix4fs) {
        if (bullet.getBulletBasicData().isHasTint()) program.getUniform("color").set3fv(bullet.getColor());
        else program.getUniform("color").set3fv(1, 1, 1);

        while (matrix4fs.size() > 100) {
            List<Matrix4f> sub = matrix4fs.subList(0, 100);
            for (int i = 0; i < 100; i++)
                program.getUniform("transform[" + i + "]").setMatrix4f(sub.get(i));
            bullet.getBulletBasicData().getTexture(bullet.getVariant()).bind();
            bullet.getBulletBasicData().getVertexArray(bullet.getVariant()).drawInstanced(100);
            sub.clear();
        }

        for (int i = 0; i < matrix4fs.size(); i++)
            program.getUniform("transform[" + i + "]").setMatrix4f(matrix4fs.get(i));
        bullet.getBulletBasicData().getTexture(bullet.getVariant()).bind();
        bullet.getBulletBasicData().getVertexArray(bullet.getVariant()).drawInstanced(matrix4fs.size());
        matrix4fs.clear();
    }

    private void renderBullets(GuiRenderContext context) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        ShaderProgram program = Shaders.TEX_COLOR_INSTANCED.get();
        program.use();
        List<Matrix4f> matrices = new ArrayList<>();
        Bullet[] lastBullet = {null};
        playground.getBullets().stream().sorted((b1, b2) -> {
            int i = b1.getBulletBasicData().compareTo(b2.getBulletBasicData());
            if (i == 0)
                return b1.getVariant().compareTo(b2.getVariant());
            return i;
        }).forEach(bullet -> {
            if (lastBullet[0] == null)
                lastBullet[0] = bullet;
            else if (!lastBullet[0].similar(bullet)) {
                drawInstanced(program, lastBullet[0], matrices);
                lastBullet[0] = bullet;
            }
            Matrix4f matrix = getHittableItemMatrix(bullet);
            if (bullet.getBulletBasicData().isHasRenderAngle())
                matrix.rotate(bullet.getRenderAngle(), 0, 0, 1);
            matrices.add(matrix);
        });
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void renderForeground(GuiRenderContext context) {
        Player player = playground.getPlayer();
        if (player.isSlowMode()) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            Matrix4f matrix = getHittableItemMatrix(player);
            matrix.rotate((float) Math.toRadians(playground.getTickTime() * 2), 0, 0, 1);

            ShaderProgram program = Shaders.TEX_COLOR.get();
            program.use();
            program.getUniform("transform").setMatrix4f(matrix);
            program.getUniform("color").set3fv(1, 1, 1);

            textureFront.bind();

            VertexArrayBuilder builder = new VertexArrayBuilder(VertexAttributeList.TEXTURE_2D, IndexBufferProvider.DEFAULT);
            builder.pos(-0.125f, -0.125f).uv(0, 0).end();
            builder.pos(0.125f, -0.125f).uv(1, 0).end();
            builder.pos(-0.125f, 0.125f).uv(0, 1).end();
            builder.pos(0.125f, 0.125f).uv(1, 1).end();
            VertexArray array = builder.build();
            array.draw();
            array.destroy();
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
        if (key == GLFW.GLFW_KEY_LEFT_SHIFT) playground.getPlayer().setSlowMode(true);
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
        if (key == GLFW.GLFW_KEY_LEFT_SHIFT) playground.getPlayer().setSlowMode(false);
    }
}
