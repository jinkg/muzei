package com.yalin.muzei.render;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.yalin.muzei.event.BlurAmountChangedEvent;
import com.yalin.muzei.event.DimAmountChangedEvent;
import com.yalin.muzei.event.GreyAmountChangedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * YaLin
 * 2016/11/10.
 */

public abstract class RenderController {
    protected Context mContext;
    protected MuzeiBlurRenderer mRenderer;
    protected Callbacks mCallbacks;
    protected boolean mVisible;
    private BitmapRegionLoader mQueuedBitmapRegionLoader;

    public RenderController(Context context, MuzeiBlurRenderer renderer, Callbacks callbacks) {
        mRenderer = renderer;
        mContext = context;
        mCallbacks = callbacks;
        EventBus.getDefault().register(this);
    }

    public void destroy() {
        if (mQueuedBitmapRegionLoader != null) {
            mQueuedBitmapRegionLoader.destroy();
        }
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(BlurAmountChangedEvent e) {
        mRenderer.recomputeMaxPrescaledBlurPixels();
        throttledForceReloadCurrentArtwork();
    }

    @Subscribe
    public void onEventMainThread(DimAmountChangedEvent e) {
        mRenderer.recomputeMaxDimAmount();
        throttledForceReloadCurrentArtwork();
    }

    @Subscribe
    public void onEventMainThread(GreyAmountChangedEvent e) {
        mRenderer.recomputeGreyAmount();
        throttledForceReloadCurrentArtwork();
    }

    private void throttledForceReloadCurrentArtwork() {
        mThrottledForceReloadHandler.removeMessages(0);
        mThrottledForceReloadHandler.sendEmptyMessageDelayed(0, 250);
    }

    private Handler mThrottledForceReloadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            reloadCurrentArtwork(true);
        }
    };

    protected abstract BitmapRegionLoader openDownloadedCurrentArtwork(boolean forceReload);

    public void reloadCurrentArtwork(final boolean forceReload) {
        new AsyncTask<Void, Void, BitmapRegionLoader>() {
            @Override
            protected BitmapRegionLoader doInBackground(Void... voids) {
                // openDownloadedCurrentArtwork should be called on a background thread
                return openDownloadedCurrentArtwork(forceReload);
            }

            @Override
            protected void onPostExecute(final BitmapRegionLoader bitmapRegionLoader) {
                if (bitmapRegionLoader == null) {
                    return;
                }

                mCallbacks.queueEventOnGlThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mVisible) {
                            mRenderer.setAndConsumeBitmapRegionLoader(bitmapRegionLoader);
                        } else {
                            mQueuedBitmapRegionLoader = bitmapRegionLoader;
                        }
                    }
                });
            }
        }.execute((Void) null);
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
        if (visible) {
            mCallbacks.queueEventOnGlThread(new Runnable() {
                @Override
                public void run() {
                    if (mQueuedBitmapRegionLoader != null) {
                        mRenderer.setAndConsumeBitmapRegionLoader(mQueuedBitmapRegionLoader);
                        mQueuedBitmapRegionLoader = null;
                    }
                }
            });
            mCallbacks.requestRender();
        }
    }

    public interface Callbacks {
        void queueEventOnGlThread(Runnable runnable);

        void requestRender();
    }
}
