/*
 * Copyright 2021 ISAM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.isam.render;

import com.github.isam.input.KeyboardInputListener;
import com.github.isam.input.MouseInputListener;
import com.github.isam.render.window.Window;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

public class SimpleRenderer implements Renderer, MouseInputListener, KeyboardInputListener {

    private final Window window;
    private final RootComponent component;

    private String glVersion;
    private GLCapabilities capabilities;

    public SimpleRenderer(Window window) {
        this.window = window;
        component = new RootComponent(this);
        window.setMouse(this);
        // Infos
        glVersion = GL11.glGetString(GL11.GL_VERSION);
        capabilities = GL.getCapabilities();
    }

    @Override
    public Window getWindow() {
        return window;
    }

    @Override
    public float getXPosition(int px) {
        return 2.0f * px / window.getWidth() - 1;
    }

    @Override
    public float getYPosition(int px) {
        return 1 - 2.0f * px / window.getHeight();
    }

    @Override
    public float getHorizonLength(int px) {
        return 2.0f * px / window.getWidth();
    }

    @Override
    public float getVerticalLength(int px) {
        return 2.0f * px / window.getHeight();
    }

    @Override
    public String getGLVersionString() {
        return glVersion;
    }

    public RootComponent getRoot() {
        return component;
    }

    public void render() {
        component.render();
    }

    @Override
    public void onMouse(long window, int button, int action, int mods) {

    }

    @Override
    public void onScroll(long window, double xoffset, double yoffset) {

    }

    @Override
    public void onDrop(long window, int count, long names) {

    }

    @Override
    public void onMove(long window, double xpos, double ypos) {

    }

    @Override
    public void onKeyInput(long window, int key, int scancode, int action, int mods) {

    }

    @Override
    public void onCharInput(long window, int codepoint) {

    }

    @Override
    public void onCharModInput(long window, int codepoint, int mods) {

    }
}
