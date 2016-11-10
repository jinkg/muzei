package com.yalin.muzei.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yalin.muzei.R;

/**
 * YaLin
 * 2016/11/9.
 */

public class ShadowDipsTextView extends TextView {
    public ShadowDipsTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ShadowDipsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ShadowDipsTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ShadowDipsTextView, defStyle, 0);
        int shadowDx = a.getDimensionPixelSize(R.styleable.ShadowDipsTextView_shadowDx, 0);
        int shadowDy = a.getDimensionPixelSize(R.styleable.ShadowDipsTextView_shadowDy, 0);
        int shadowRadius = a.getDimensionPixelSize(R.styleable.ShadowDipsTextView_shadowRadius, 0);
        int shadowColor = a.getColor(R.styleable.ShadowDipsTextView_shadowColor, 0);
        if (shadowColor != 0) {
            setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);
        }
        a.recycle();
    }
}
