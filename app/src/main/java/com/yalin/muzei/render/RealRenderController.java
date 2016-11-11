package com.yalin.muzei.render;

import android.content.Context;
import android.database.ContentObserver;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.yalin.muzei.api.MuzeiContract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * YaLin
 * 2016/11/10.
 */

public class RealRenderController extends RenderController {
    private static final String TAG = "RealRenderController";

    private ContentObserver mContentObserver;

    public RealRenderController(Context context, MuzeiBlurRenderer renderer,
                                Callbacks callbacks) {
        super(context, renderer, callbacks);
        mContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                reloadCurrentArtwork(false);
            }
        };
        context.getContentResolver().registerContentObserver(MuzeiContract.Artwork.CONTENT_URI,
                true, mContentObserver);
        if (MuzeiContract.Artwork.getCurrentArtwork(context) != null) {
            reloadCurrentArtwork(false);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        mContext.getContentResolver().unregisterContentObserver(mContentObserver);
    }

    @Override
    protected BitmapRegionLoader openDownloadedCurrentArtwork(boolean forceReload) {
        // Load the stream
        try {
            // Check if there's rotation
            int rotation = 0;
            try {
                InputStream in = mContext.getContentResolver().openInputStream(MuzeiContract.Artwork.CONTENT_URI);
                ExifInterface exifInterface;
                if (Build.VERSION.SDK_INT >= 24) {
                    exifInterface = new ExifInterface(in);
                } else {
                    exifInterface = new ExifInterface(writeArtworkToFile(in).getAbsolutePath());
                }
                int orientation = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotation = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotation = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotation = 270;
                        break;
                }
            } catch (IOException e) {
                Log.w(TAG, "Couldn't open EXIF interface on artwork", e);
            }
            return BitmapRegionLoader.newInstance(
                    mContext.getContentResolver().openInputStream(MuzeiContract.Artwork.CONTENT_URI), rotation);
        } catch (IOException e) {
            Log.e(TAG, "Error loading image", e);
            return null;
        }
    }

    private File writeArtworkToFile(InputStream in) throws IOException {
        File file = new File(mContext.getCacheDir(), "temp_artwork");
        FileOutputStream out = new FileOutputStream(file);
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException ignored) {
            }
        }
        return file;
    }
}
