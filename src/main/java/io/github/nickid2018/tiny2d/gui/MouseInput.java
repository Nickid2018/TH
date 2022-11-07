package io.github.nickid2018.tiny2d.gui;

public interface MouseInput {

    void onMouseMove(int x, int y);

    void onMouseClick(int x, int y, int button);

    void onMouseRelease(int x, int y, int button);

    void onMouseScroll(int x, int y, int scroll);

}
