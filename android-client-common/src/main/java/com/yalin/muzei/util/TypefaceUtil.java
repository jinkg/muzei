package com.yalin.muzei.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * YaLin
 * 2016/11/9.
 */

public class TypefaceUtil {
    private static final Map<String, Typeface> sTypefaceCache = new HashMap<>();

    public static Typeface getAndCache(Context context, String assetPath) {
        synchronized (sTypefaceCache) {
            if (!sTypefaceCache.containsKey(assetPath)) {
                Typeface tf = Typeface.createFromAsset(
                        context.getApplicationContext().getAssets(), assetPath);
                sTypefaceCache.put(assetPath, tf);
            }
            return sTypefaceCache.get(assetPath);
        }
    }
}
