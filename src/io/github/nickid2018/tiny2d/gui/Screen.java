package io.github.nickid2018.tiny2d.gui;

import io.github.nickid2018.tiny2d.window.Window;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;

import java.util.ArrayList;
import java.util.List;

public abstract class Screen extends RenderComponent {

    protected final List<Pair<String, RenderComponent>> components = new ArrayList<>();

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
}
