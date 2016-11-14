package com.yalin.muzei.event;

/**
 * YaLin
 * 2016/11/10.
 */

public class LockScreenVisibleChangedEvent {
    public boolean mLockScreenVisible = false;

    public LockScreenVisibleChangedEvent(boolean lockScreenVisible) {
        mLockScreenVisible = lockScreenVisible;
    }

    public boolean isLockScreenVisible() {
        return mLockScreenVisible;
    }
}
