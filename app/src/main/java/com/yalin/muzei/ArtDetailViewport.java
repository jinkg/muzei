package com.yalin.muzei;

import android.graphics.RectF;

import org.greenrobot.eventbus.EventBus;

/**
 * YaLin
 * 2016/11/10.
 */

public class ArtDetailViewport {
    private volatile RectF mViewport0 = new RectF();
    private volatile RectF mViewport1 = new RectF();
    private boolean mFromUser;

    private static ArtDetailViewport sInstance = new ArtDetailViewport();

    public static ArtDetailViewport getInstance() {
        return sInstance;
    }

    private ArtDetailViewport() {
    }

    public RectF getViewport(int id) {
        return (id == 0) ? mViewport0 : mViewport1;
    }

    public void setViewport(int id, RectF viewport, boolean fromUser) {
        setViewport(id, viewport.left, viewport.top, viewport.right, viewport.bottom,
                fromUser);
    }

    public void setViewport(int id, float left, float top, float right, float bottom,
                            boolean fromUser) {
        mFromUser = fromUser;
        getViewport(id).set(left, top, right, bottom);
        EventBus.getDefault().post(this);
    }

    public boolean isFromUser() {
        return mFromUser;
    }

    public ArtDetailViewport setDefaultViewport(int id, float bitmapAspectRatio,
                                                float screenAspectRatio) {
        mFromUser = false;
        if (bitmapAspectRatio > screenAspectRatio) {
            getViewport(id).set(
                    0.5f - screenAspectRatio / bitmapAspectRatio / 2,
                    0,
                    0.5f + screenAspectRatio / bitmapAspectRatio / 2,
                    1);
        } else {
            getViewport(id).set(
                    0,
                    0.5f - bitmapAspectRatio / screenAspectRatio / 2,
                    1,
                    0.5f + bitmapAspectRatio / screenAspectRatio / 2);
        }
        EventBus.getDefault().post(this);
        return this;
    }
}
