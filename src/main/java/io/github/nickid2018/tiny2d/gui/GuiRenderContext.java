package io.github.nickid2018.tiny2d.gui;

import io.github.nickid2018.tiny2d.buffer.FrameBuffer;
import io.github.nickid2018.tiny2d.font.FontRenderer;
import io.github.nickid2018.tiny2d.window.Window;

public record GuiRenderContext(Window window, FontRenderer renderer, FrameBuffer currentFrameBuffer) {

}
