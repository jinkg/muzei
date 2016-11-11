package com.yalin.muzei.event;

/**
 * YaLin
 * 2016/11/10.
 */

public class SwitchingPhotosStateChangedEvent {
    private boolean mSwitchingPhotos;
    private int mId;

    public SwitchingPhotosStateChangedEvent(int id, boolean switchingPhotos) {
        mId = id;
        mSwitchingPhotos = switchingPhotos;
    }

    public int getCurrentId() {
        return mId;
    }

    public boolean isSwitchingPhotos() {
        return mSwitchingPhotos;
    }
}
