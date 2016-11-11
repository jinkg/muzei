package com.api.muzei.event;

/**
 * YaLin
 * 2016/11/10.
 */

public class WallpaperSizeChangedEvent {
    private int mWidth;
    private int mHeight;

    public WallpaperSizeChangedEvent(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }
}
