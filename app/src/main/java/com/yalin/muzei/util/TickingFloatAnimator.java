package com.yalin.muzei.util;

import android.animation.TimeInterpolator;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * YaLin
 * 2016/11/10.
 */

public class TickingFloatAnimator {
    private float mStartValue = 0;
    private float mCurrentValue;
    private float mEndValue;
    private boolean mRunning = false;
    private long mStartTime;
    private int mDuration = 1000;
    private Runnable mEndCallback;
    private TimeInterpolator mInterpolator = new AccelerateDecelerateInterpolator();

    public static TickingFloatAnimator create() {
        return new TickingFloatAnimator();
    }

    public TickingFloatAnimator from(float startValue) {
        cancel();
        mStartValue = startValue;
        mCurrentValue = startValue;
        return this;
    }

    public TickingFloatAnimator to(float endValue) {
        mEndValue = endValue;
        return this;
    }

    public TickingFloatAnimator withDuration(int duration) {
        mDuration = Math.max(0, duration);
        return this;
    }

    public TickingFloatAnimator withInterpolator(TimeInterpolator interpolator) {
        mInterpolator = interpolator;
        return this;
    }

    public TickingFloatAnimator withEndListener(Runnable listener) {
        mEndCallback = listener;
        return this;
    }

    public void cancel() {
        mRunning = false;
        mEndValue = mCurrentValue;
    }

    public boolean tick() {
        if (!mRunning) {
            return false;
        }

        float t;
        if (mDuration <= 0) {
            t = 1;
        } else {
            t = (float) (SystemClock.elapsedRealtime() - mStartTime) * 1f / mDuration;
            if (t >= 1) {
                t = 1;
            }
        }

        if (t >= 1) {
            // Ended
            mRunning = false;
            mCurrentValue = mEndValue;
            if (mEndCallback != null) {
                mEndCallback.run();
            }
            return false;
        }

        // Still running; compute value
        mCurrentValue = mStartValue + mInterpolator.getInterpolation(t) * (mEndValue - mStartValue);
        mRunning = true;
        return true;
    }

    public void start() {
        mRunning = true;
        mStartValue = mCurrentValue;
        mStartTime = SystemClock.elapsedRealtime();
        tick();
    }

    public boolean isRunning() {
        return mRunning;
    }

    public float currentValue() {
        return mCurrentValue;
    }

    private TickingFloatAnimator() {
    }
}
