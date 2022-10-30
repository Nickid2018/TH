package io.github.nickid2018.th.gui;

import io.github.nickid2018.th.system.bullet.Bullet;
import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.tiny2d.buffer.FrameBuffer;
import io.github.nickid2018.tiny2d.buffer.VertexArray;
import io.github.nickid2018.tiny2d.gui.GuiRenderContext;
import io.github.nickid2018.tiny2d.gui.RenderComponent;
import io.github.nickid2018.tiny2d.shader.ShaderProgram;
import io.github.nickid2018.tiny2d.shader.ShaderSource;
import io.github.nickid2018.tiny2d.shader.ShaderType;
import io.github.nickid2018.tiny2d.shader.Uniform;
import io.github.nickid2018.tiny2d.util.LazyLoadValue;
import io.github.nickid2018.tiny2d.window.Window;
import lombok.Getter;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class PlayGroundGui extends RenderComponent {

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
    }

    @Override
    public void onWindowResize() {
    }

    private void renderBackground(GuiRenderContext context) {

    }

    private void renderPlayerAndEnemy(GuiRenderContext context) {

    }

    private void renderBullets(GuiRenderContext context) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        for (Bullet bullet : playground.getBullets()) {
            Matrix4f matrix = new Matrix4f();
            float x = bullet.getHitSphere().x / Playground.PLAYGROUND_WIDTH * 2 - 1;
            float y = bullet.getHitSphere().y / Playground.PLAYGROUND_HEIGHT * 2 - 1;

            VertexArray array = bullet.getBulletBasicData().getVertexArray();

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

            bullet.getBulletBasicData().getTextureInstance().bind();
            array.draw();
        }
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void renderForeground(GuiRenderContext context) {

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
}
