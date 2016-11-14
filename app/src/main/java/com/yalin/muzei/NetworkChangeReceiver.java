package com.yalin.muzei;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.yalin.muzei.api.MuzeiContract;
import com.yalin.muzei.sync.TaskQueueService;

import static com.yalin.muzei.api.internal.ProtocolConstants.ACTION_NETWORK_AVAILABLE;

/**
 * YaLin
 * 2016/11/10.
 */

public class NetworkChangeReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean hasConnectivity = !intent.getBooleanExtra(
                ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        if (hasConnectivity) {
            // Check with components that may not currently be alive but interested in
            // network connectivity changes.
            if (Build.VERSION.SDK_INT < 21) {
                Intent retryIntent = TaskQueueService.maybeRetryDownloadDueToGainedConnectivity(context);
                if (retryIntent != null) {
                    startWakefulService(context, retryIntent);
                }
            }

            // TODO: wakeful broadcast?
            Cursor selectedSources = context.getContentResolver().query(MuzeiContract.Sources.CONTENT_URI,
                    new String[]{MuzeiContract.Sources.COLUMN_NAME_COMPONENT_NAME},
                    MuzeiContract.Sources.COLUMN_NAME_IS_SELECTED + "=1 AND " +
                            MuzeiContract.Sources.COLUMN_NAME_WANTS_NETWORK_AVAILABLE + "=1", null, null, null);
            if (selectedSources != null && selectedSources.moveToPosition(-1)) {
                while (selectedSources.moveToNext()) {
                    String componentName = selectedSources.getString(0);
                    context.startService(new Intent(ACTION_NETWORK_AVAILABLE)
                            .setComponent(ComponentName.unflattenFromString(componentName)));
                }
            }
            if (selectedSources != null) {
                selectedSources.close();
            }
        }
    }
}
