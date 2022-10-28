package io.github.nickid2018.tiny2d.window;

import io.github.nickid2018.tiny2d.buffer.FrameBuffer;
import io.github.nickid2018.tiny2d.font.FontRenderer;
import io.github.nickid2018.tiny2d.font.VectorFont;
import io.github.nickid2018.tiny2d.gui.GuiRenderContext;
import io.github.nickid2018.tiny2d.gui.Screen;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.glfw.GLFW.*;

public class Window {

    private final long windowID;
    private int maxFPS;

    private int width, height;

    private double lastRecordTime;
    private int fpsCount = 0;
    private int lastFPS = 0;

    private FontRenderer fontRenderer;
    private FrameBuffer defaultFrameBuffer;

    private Consumer<FrameBuffer> postRenderer;
    private Screen currentScreen;

    public Window(String title, int width, int height, VectorFont font) {
        glfwInit();
        windowID = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        glfwMakeContextCurrent(windowID);
        GL.createCapabilities();
        addFramebufferSizeCallback(null);
        glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
        defaultFrameBuffer = new FrameBuffer(width, height);
        fontRenderer = new FontRenderer(this, font);
    }

    public void setMaxFPS(int maxFPS) {
        this.maxFPS = maxFPS;
    }

    public void setVsync(boolean vsync) {
        glfwSwapInterval(vsync ? 1 : 0);
    }

    public void run(Runnable programExtraLogic) {
        while (!glfwWindowShouldClose(windowID)) {
            double startTime = glfwGetTime();

            glClearColor(1, 1, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            defaultFrameBuffer.bind();
            glClearColor(1, 1, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            if (currentScreen != null)
                currentScreen.render(new GuiRenderContext(this, fontRenderer, defaultFrameBuffer));
            defaultFrameBuffer.unbind();

            if (postRenderer != null)
                postRenderer.accept(defaultFrameBuffer);
            else
                defaultFrameBuffer.renderToScreen();

            glfwSwapBuffers(windowID);
            glfwPollEvents();

            if (programExtraLogic != null)
                programExtraLogic.run();

            fpsCount++;
            if (glfwGetTime() - lastRecordTime >= 1) {
                lastFPS = fpsCount;
                fpsCount = 0;
                lastRecordTime = glfwGetTime();
            }

            double endTime = glfwGetTime();

            if (maxFPS > 0) {
                double time = endTime - startTime;
                double sleepTime = 1.0 / maxFPS - time;
                if (sleepTime > 0)
                    try {
                        Thread.sleep((long) (sleepTime * 1000));
                    } catch (InterruptedException ignored) {
                    }
            }
        }
    }

    public void addFramebufferSizeCallback(BiConsumer<Integer, Integer> extraListenResize) {
        glfwSetFramebufferSizeCallback(windowID, (window, w, h) -> {
            glViewport(0, 0, w, h);
            width = w;
            height = h;
            if (w != 0 && h != 0) {
                defaultFrameBuffer.delete();
                defaultFrameBuffer = new FrameBuffer(width, height);
            }
            if (currentScreen != null)
                currentScreen.onWindowResize();
            if (extraListenResize != null)
                extraListenResize.accept(w, h);
        });
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float toNDCX(float x) {
        return x / width * 2 - 1;
    }

    public float toNDCY(float y) {
        return 1 - y / height * 2;
    }

    public int getFPS() {
        return lastFPS;
    }

    public void switchScreen(Screen next) {
        if (currentScreen != null)
            currentScreen.onDispose();
        currentScreen = next;
    }

    public void setPostRenderer(Consumer<FrameBuffer> postRenderer) {
        this.postRenderer = postRenderer;
    }

    public void setFontRenderer(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
    }

    public void close() {
        glfwDestroyWindow(windowID);
        glfwTerminate();
    }

    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }
}
