package io.github.nickid2018.tiny2d.gui;

import io.github.nickid2018.tiny2d.math.AABB;
import io.github.nickid2018.tiny2d.window.Window;

public enum ComponentResizePolicy {

    NO_RESIZE_XY_FIXED {
        @Override
        public AABB getAABB(float x, float y, float width, float height, Window window) {
            return AABB.newAABB(
                    window.toNDCX(x),
                    window.toNDCY(y),
                    window.toNDCX(x + width),
                    window.toNDCY(y + height)
            );
        }
    },

    NO_RESIZE_CENTER_FIXED {
        @Override
        public AABB getAABB(float x, float y, float width, float height, Window window) {
            return AABB.newAABB(
                    window.toNDCX(x - width / 2),
                    window.toNDCY(y - height / 2),
                    window.toNDCX(x + width / 2),
                    window.toNDCY(y + height / 2)
            );
        }
    },

    RESIZE_X {
        @Override
        public AABB getAABB(float x, float y, float width, float height, Window window) {
            return AABB.newAABB(
                    x,
                    window.toNDCY(y),
                    x + width,
                    window.toNDCY(y + height)
            );
        }
    },

    RESIZE_Y {
        @Override
        public AABB getAABB(float x, float y, float width, float height, Window window) {
            return AABB.newAABB(
                    window.toNDCX(x),
                    y,
                    window.toNDCX(x + width),
                    y + height
            );
        }
    },

    RESIZE_XY;

    public AABB getAABB(float x, float y, float width, float height, Window window) {
        return AABB.newAABB(x, y, x + width, y + height);
    }
}
