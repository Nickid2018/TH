package io.github.nickid2018.tiny2d.window;

import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.util.function.BiConsumer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.glfw.GLFW.*;

public class Window {

    private final long windowID;
    private int maxFPS;

    private int width, height;

    public Window(String title, int width, int height) {
        glfwInit();
        windowID = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        glfwMakeContextCurrent(windowID);
        GL.createCapabilities();
        glfwSetFramebufferSizeCallback(windowID, (window, w, h) -> glViewport(0, 0, w, h));
        glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
    }

    public void setMaxFPS(int maxFPS) {
        this.maxFPS = maxFPS;
    }

    public void setVsync(boolean vsync) {
        glfwSwapInterval(vsync ? 1 : 0);
    }

    public void run(Runnable runnable) {
        while (!glfwWindowShouldClose(windowID)) {
            long start = System.currentTimeMillis();
            runnable.run();
            glfwSwapBuffers(windowID);
            glfwPollEvents();
            if (maxFPS > 0) {
                long end = System.currentTimeMillis();
                if (end - start < 1000 / maxFPS)
                    try {
                        Thread.sleep(1000 / maxFPS - (end - start));
                    } catch (InterruptedException ignored) {
                    }
            }
        }
    }

    public void addFramebufferSizeCallback(BiConsumer<Integer, Integer> consumer) {
        glfwSetFramebufferSizeCallback(windowID, (window, w, h) -> {
            glViewport(0, 0, w, h);
            width = w;
            height = h;
            consumer.accept(w, h);
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

    public void close() {
        glfwDestroyWindow(windowID);
        glfwTerminate();
    }
}
