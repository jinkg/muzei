package com.yalin.muzei.event;

/**
 * YaLin
 * 2016/11/10.
 */

public class WallpaperActiveStateChangedEvent {
    private boolean mActive;

    public WallpaperActiveStateChangedEvent(boolean active) {
        mActive = active;
    }

    public boolean isActive() {
        return mActive;
    }
}
