package com.yalin.muzei.event;

/**
 * YaLin
 * 2016/11/10.
 */

public class ArtworkLoadingStateChangedEvent {
    private boolean mLoading;
    private boolean mError;

    public ArtworkLoadingStateChangedEvent(boolean loading, boolean error) {
        mLoading = loading;
        mError = error;
    }

    public boolean isLoading() {
        return mLoading;
    }

    public boolean hadError() {
        return mError;
    }
}
