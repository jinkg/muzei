package com.yalin.muzei;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.yalin.muzei.event.LockScreenVisibleChangedEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * YaLin
 * 2016/11/10.
 */

public class LockScreenVisibleReceiver extends BroadcastReceiver {
    public static final String PREF_ENABLED = "disable_blur_when_screen_locked_enabled";

    private boolean mRegistered = false;
    private Context mRegisterDeregisterContext;

    private SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
            if (PREF_ENABLED.equals(key)) {
                registerDeregister(sp.getBoolean(PREF_ENABLED, false));
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
                EventBus.getDefault().post(new LockScreenVisibleChangedEvent(false));
            } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                EventBus.getDefault().post(new LockScreenVisibleChangedEvent(true));
            } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                KeyguardManager kgm = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if (!kgm.inKeyguardRestrictedInputMode()) {
                    EventBus.getDefault().post(new LockScreenVisibleChangedEvent(false));
                }
            }
        }
    }

    private static IntentFilter createIntentFilter() {
        IntentFilter presentFilter = new IntentFilter();
        presentFilter.addAction(Intent.ACTION_USER_PRESENT);
        presentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        presentFilter.addAction(Intent.ACTION_SCREEN_ON);
        return presentFilter;
    }

    public void setupRegisterDeregister(Context context) {
        mRegisterDeregisterContext = context;
        PreferenceManager.getDefaultSharedPreferences(context)
                .registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
        mOnSharedPreferenceChangeListener.onSharedPreferenceChanged(
                PreferenceManager.getDefaultSharedPreferences(context), PREF_ENABLED);
    }

    private void registerDeregister(boolean register) {
        if (mRegistered == register || mRegisterDeregisterContext == null) {
            return;
        }

        if (register) {
            mRegisterDeregisterContext.registerReceiver(this, createIntentFilter());
        } else {
            mRegisterDeregisterContext.unregisterReceiver(this);
        }

        mRegistered = register;
    }

    public void destroy() {
        registerDeregister(false);
        if (mRegisterDeregisterContext != null) {
            PreferenceManager.getDefaultSharedPreferences(mRegisterDeregisterContext)
                    .unregisterOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
        }
    }
}
