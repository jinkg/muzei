package com.api.muzei.render;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * YaLin
 * 2016/11/10.
 */

public class ImageUtil {
    // Make sure input images are very small!
    public static float calculateDarkness(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int totalLum = 0;
        int n = 0;
        int x, y, color;
        for (y = 0; y < height; y++) {
            for (x = 0; x < width; x++) {
                ++n;
                color = bitmap.getPixel(x, y);
                totalLum += (0.21f * Color.red(color)
                        + 0.71f * Color.green(color)
                        + 0.07f * Color.blue(color));
            }
        }

        return (totalLum / n) / 256f;
    }

    private ImageUtil() {
    }

    public static int calculateSampleSize(int rawSize, int targetSize) {
        int sampleSize = 1;
        while (rawSize / (sampleSize << 1) > targetSize) {
            sampleSize <<= 1;
        }
        return sampleSize;
    }
}
