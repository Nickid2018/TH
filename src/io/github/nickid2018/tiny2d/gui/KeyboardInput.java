package io.github.nickid2018.tiny2d.gui;

public interface KeyboardInput {

    void onKeyTyped(char c);

    void onKeyPressed(int key);

    void onKeyReleased(int key);
}
