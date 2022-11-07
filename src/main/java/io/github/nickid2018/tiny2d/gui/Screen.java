package io.github.nickid2018.tiny2d.gui;

import io.github.nickid2018.tiny2d.window.Window;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;

import java.util.ArrayList;
import java.util.List;

public abstract class Screen extends RenderComponent implements KeyboardInput, MouseInput {

    protected final List<Pair<String, RenderComponent>> components = new ArrayList<>();

    protected RenderComponent nowFocus = null;

    public Screen(Window window) {
        super(window);
    }

    @Override
    public void render(GuiRenderContext context) {
        components.stream().map(Pair::right).forEach(ui -> ui.render(context));
    }

    @Override
    public void onWindowResize() {
        components.stream().map(Pair::right).forEach(RenderComponent::onWindowResize);
    }

    public void setNowFocus(RenderComponent nowFocus) {
        this.nowFocus = nowFocus;
    }

    public RenderComponent getNowFocus() {
        return nowFocus;
    }

    public void addComponent(String name, RenderComponent component) {
        components.add(new ObjectObjectMutablePair<>(name, component));
    }

    @SuppressWarnings("unchecked")
    public <T extends RenderComponent> T getComponent(String name) {
        for (Pair<String, RenderComponent> pair : components)
            if (pair.left().equals(name))
                return (T) pair.right();
        return null;
    }

    public List<Pair<String, RenderComponent>> getComponents() {
        return components;
    }

    @Override
    public void onKeyPressed(int key) {
        if (nowFocus != null && nowFocus instanceof KeyboardInput input)
            input.onKeyPressed(key);
    }

    @Override
    public void onKeyReleased(int key) {
        if (nowFocus != null && nowFocus instanceof KeyboardInput input)
            input.onKeyReleased(key);
    }

    @Override
    public void onKeyTyped(char c) {
        if (nowFocus != null && nowFocus instanceof KeyboardInput input)
            input.onKeyTyped(c);
    }

    @Override
    public void onMouseClick(int x, int y, int button) {
        //
    }

    @Override
    public void onMouseMove(int x, int y) {
        //
    }

    @Override
    public void onMouseRelease(int x, int y, int button) {
        //
    }

    @Override
    public void onMouseScroll(int x, int y, int scroll) {
        //
    }
}
