package com.yalin.muzei;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.yalin.muzei.event.ArtDetailOpenedClosedEvent;
import com.yalin.muzei.render.ImageUtil;
import com.yalin.muzei.api.MuzeiArtSource;
import com.yalin.muzei.api.MuzeiContract;
import com.yalin.muzei.api.UserCommand;

import org.greenrobot.eventbus.EventBus;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * YaLin
 * 2016/11/10.
 */

public class NewWallpaperNotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NewWallpaperNotif";

    public static final String PREF_ENABLED = "new_wallpaper_notification_enabled";
    private static final String PREF_LAST_READ_NOTIFICATION_ARTWORK_ID
            = "last_read_notification_artwork_id";

    private static final int NOTIFICATION_ID = 1234;

    private static final String ACTION_MARK_NOTIFICATION_READ
            = "com.yalin.muzei.action.NOTIFICATION_DELETED";

    private static final String ACTION_NEXT_ARTWORK
            = "com.yalin.muzei.action.NOTIFICATION_NEXT_ARTWORK";

    private static final String ACTION_USER_COMMAND
            = "com.yalin.muzei.action.NOTIFICATION_USER_COMMAND";

    private static final String EXTRA_USER_COMMAND
            = "com.yalin.muzei.extra.USER_COMMAND";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_MARK_NOTIFICATION_READ.equals(action)) {
                markNotificationRead(context);
            } else if (ACTION_NEXT_ARTWORK.equals(action)) {
                SourceManager.sendAction(context, MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK);
            } else if (ACTION_USER_COMMAND.equals(action)) {
                triggerUserCommandFromRemoteInput(context, intent);
            }
        }
    }

    private void triggerUserCommandFromRemoteInput(Context context, Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput == null) {
            return;
        }
        String selectedCommand = remoteInput.getCharSequence(EXTRA_USER_COMMAND).toString();
        Cursor selectedSource = context.getContentResolver().query(MuzeiContract.Sources.CONTENT_URI,
                new String[]{MuzeiContract.Sources.COLUMN_NAME_COMMANDS},
                MuzeiContract.Sources.COLUMN_NAME_IS_SELECTED + "=1", null, null, null);
        if (selectedSource != null && selectedSource.moveToFirst()) {
            List<UserCommand> commands = MuzeiContract.Sources.parseCommands(selectedSource.getString(0));
            for (UserCommand action : commands) {
                if (TextUtils.equals(selectedCommand, action.getTitle())) {
                    SourceManager.sendAction(context, action.getId());
                    break;
                }
            }
        }
        if (selectedSource != null) {
            selectedSource.close();
        }
    }

    public static void markNotificationRead(Context context) {
        Cursor lastArtwork = context.getContentResolver().query(
                MuzeiContract.Artwork.CONTENT_URI, new String[]{BaseColumns._ID}, null, null, null);
        if (lastArtwork != null && lastArtwork.moveToFirst()) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putLong(PREF_LAST_READ_NOTIFICATION_ARTWORK_ID,
                    lastArtwork.getLong(0)).apply();
        }
        if (lastArtwork != null) {
            lastArtwork.close();
        }

        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.cancel(NOTIFICATION_ID);
    }

    public static void maybeShowNewArtworkNotification(Context context) {
        ArtDetailOpenedClosedEvent adoce = EventBus.getDefault().getStickyEvent(
                ArtDetailOpenedClosedEvent.class);
        if (adoce != null && adoce.isArtDetailOpened()) {
            return;
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (!sp.getBoolean(PREF_ENABLED, true)) {
            return;
        }

        ContentResolver contentResolver = context.getContentResolver();
        Cursor artwork = contentResolver.query(
                MuzeiContract.Artwork.CONTENT_URI,
                new String[]{BaseColumns._ID,
                        MuzeiContract.Artwork.COLUMN_NAME_TITLE,
                        MuzeiContract.Artwork.COLUMN_NAME_BYLINE,
                        MuzeiContract.Artwork.COLUMN_NAME_VIEW_INTENT,
                        MuzeiContract.Sources.COLUMN_NAME_SUPPORTS_NEXT_ARTWORK_COMMAND,
                        MuzeiContract.Sources.COLUMN_NAME_COMMANDS},
                null, null, null);
        if (artwork == null || !artwork.moveToFirst()) {
            if (artwork != null) {
                artwork.close();
            }
            return;
        }

        long lastReadArtworkId = sp.getLong(PREF_LAST_READ_NOTIFICATION_ARTWORK_ID, -1);
        if (lastReadArtworkId == artwork.getLong(0)) {
            artwork.close();
            return;
        }

        Bitmap largeIcon;
        Bitmap background;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(contentResolver.openInputStream(
                    MuzeiContract.Artwork.CONTENT_URI), null, options);
            int width = options.outWidth;
            int height = options.outHeight;
            int shortestLength = Math.min(width, height);
            options.inJustDecodeBounds = false;
            int largeIconHeight = context.getResources()
                    .getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
            options.inSampleSize = ImageUtil.calculateSampleSize(shortestLength, largeIconHeight);
            largeIcon = BitmapFactory.decodeStream(contentResolver.openInputStream(
                    MuzeiContract.Artwork.CONTENT_URI), null, options);

            // Use the suggested 400x400 for Android Wear background images per
            // http://developer.android.com/training/wearables/notifications/creating.html#AddWearableFeatures
            options.inSampleSize = ImageUtil.calculateSampleSize(height, 400);
            background = BitmapFactory.decodeStream(contentResolver.openInputStream(
                    MuzeiContract.Artwork.CONTENT_URI), null, options);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Unable to read artwork to show notification", e);
            return;
        }

        String artworkTitle = artwork.getString(artwork.getColumnIndex(MuzeiContract.Artwork.COLUMN_NAME_TITLE));
        String title = TextUtils.isEmpty(artworkTitle)
                ? context.getString(R.string.app_name)
                : artworkTitle;
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_muzei)
                .setColor(ContextCompat.getColor(context, R.color.notification))
                .setPriority(Notification.PRIORITY_MIN)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(context.getString(R.string.notification_new_wallpaper))
                .setLargeIcon(largeIcon)
                .setContentIntent(PendingIntent.getActivity(context, 0,
                        Intent.makeMainActivity(new ComponentName(context, MuzeiActivity.class)),
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .setDeleteIntent(PendingIntent.getBroadcast(context, 0,
                        new Intent(context, NewWallpaperNotificationReceiver.class)
                                .setAction(ACTION_MARK_NOTIFICATION_READ),
                        PendingIntent.FLAG_UPDATE_CURRENT));
        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
                .bigLargeIcon(null)
                .setBigContentTitle(title)
                .setSummaryText(artwork.getString(artwork.getColumnIndex(MuzeiContract.Artwork.COLUMN_NAME_BYLINE)))
                .bigPicture(background);
        nb.setStyle(style);

        NotificationCompat.WearableExtender extender = new NotificationCompat.WearableExtender();

        // Support Next Artwork
        if (artwork.getInt(artwork.getColumnIndex(MuzeiContract.Sources.COLUMN_NAME_SUPPORTS_NEXT_ARTWORK_COMMAND)) != 0) {
            PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 0,
                    new Intent(context, NewWallpaperNotificationReceiver.class)
                            .setAction(ACTION_NEXT_ARTWORK),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            nb.addAction(
                    R.drawable.ic_notif_next_artwork,
                    context.getString(R.string.action_next_artwork_condensed),
                    nextPendingIntent);
            // Android Wear uses larger action icons so we build a
            // separate action
            extender.addAction(new NotificationCompat.Action.Builder(
                    R.drawable.ic_notif_full_next_artwork,
                    context.getString(R.string.action_next_artwork_condensed),
                    nextPendingIntent)
                    .extend(new NotificationCompat.Action.WearableExtender().setAvailableOffline(false))
                    .build());
        }
        List<UserCommand> commands = MuzeiContract.Sources.parseCommands(
                artwork.getString(artwork.getColumnIndex(MuzeiContract.Sources.COLUMN_NAME_COMMANDS)));
        // Show custom actions as a selectable list on Android Wear devices
        if (!commands.isEmpty()) {
            String[] actions = new String[commands.size()];
            for (int h = 0; h < commands.size(); h++) {
                actions[h] = commands.get(h).getTitle();
            }
            PendingIntent userCommandPendingIntent = PendingIntent.getBroadcast(context, 0,
                    new Intent(context, NewWallpaperNotificationReceiver.class)
                            .setAction(ACTION_USER_COMMAND),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_USER_COMMAND)
                    .setAllowFreeFormInput(false)
                    .setLabel(context.getString(R.string.action_user_command_prompt))
                    .setChoices(actions)
                    .build();
            extender.addAction(new NotificationCompat.Action.Builder(
                    R.drawable.ic_notif_full_user_command,
                    context.getString(R.string.action_user_command),
                    userCommandPendingIntent).addRemoteInput(remoteInput)
                    .extend(new NotificationCompat.Action.WearableExtender().setAvailableOffline(false))
                    .build());
        }
        Intent viewIntent = null;
        try {
            String viewIntentString = artwork.getString(artwork.getColumnIndex(MuzeiContract.Artwork.COLUMN_NAME_VIEW_INTENT));
            if (!TextUtils.isEmpty(viewIntentString)) {
                viewIntent = Intent.parseUri(viewIntentString, Intent.URI_INTENT_SCHEME);
            }
        } catch (URISyntaxException ignored) {
        }
        if (viewIntent != null) {
            viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent nextPendingIntent = PendingIntent.getActivity(context, 0,
                    viewIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            nb.addAction(
                    R.drawable.ic_notif_info,
                    context.getString(R.string.action_artwork_info),
                    nextPendingIntent);
            // Android Wear uses larger action icons so we build a
            // separate action
            extender.addAction(new NotificationCompat.Action.Builder(
                    R.drawable.ic_notif_full_info,
                    context.getString(R.string.action_artwork_info),
                    nextPendingIntent)
                    .extend(new NotificationCompat.Action.WearableExtender().setAvailableOffline(false))
                    .build());
        }
        nb.extend(extender);

        // Hide the image and artwork title for the public version
        NotificationCompat.Builder publicBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_muzei)
                .setColor(ContextCompat.getColor(context, R.color.notification))
                .setPriority(Notification.PRIORITY_MIN)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.notification_new_wallpaper))
                .setContentIntent(PendingIntent.getActivity(context, 0,
                        Intent.makeMainActivity(new ComponentName(context, MuzeiActivity.class)),
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .setDeleteIntent(PendingIntent.getBroadcast(context, 0,
                        new Intent(context, NewWallpaperNotificationReceiver.class)
                                .setAction(ACTION_MARK_NOTIFICATION_READ),
                        PendingIntent.FLAG_UPDATE_CURRENT));
        nb.setPublicVersion(publicBuilder.build());


        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(NOTIFICATION_ID, nb.build());

        artwork.close();
    }
}
