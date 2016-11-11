package com.api.muzei.event;

/**
 * YaLin
 * 2016/11/10.
 */

public class ArtDetailOpenedClosedEvent {
    boolean mOpened;

    public ArtDetailOpenedClosedEvent(boolean opened) {
        mOpened = opened;
    }

    public boolean isArtDetailOpened() {
        return mOpened;
    }
}
