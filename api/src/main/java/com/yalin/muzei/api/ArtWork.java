package com.yalin.muzei.api;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Date;

/**
 * YaLin
 * 2016/11/9.
 */

public class Artwork {
    public static final String FONT_TYPE_DEFAULT = "";
    public static final String FONT_TYPE_ELEGANT = "elegant";

    private static final String KEY_COMPONENT_NAME = "componentName";
    private static final String KEY_IMAGE_URI = "imageUri";
    private static final String KEY_TITLE = "title";
    private static final String KEY_BYLINE = "byline";
    private static final String KEY_ATTRIBUTION = "attribution";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_VIEW_INTENT = "viewIntent";
    private static final String KEY_DETAILS_URI = "detailsUri";
    private static final String KEY_META_FONT = "metaFont";
    private static final String KEY_DATE_ADDED = "dateAdded";

    private ComponentName mComponentName;
    private Uri mImageUri;
    private String mTitle;
    private String mByline;
    private String mAttribution;
    private String mToken;
    private Intent mViewIntent;
    private String mMetaFont;
    private Date mDateAdded;

    private Artwork() {
    }

    public ComponentName getComponentName() {
        return mComponentName;
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getByline() {
        return mByline;
    }

    public String getAttribution() {
        return mAttribution;
    }

    public String getToken() {
        return mToken;
    }

    public Intent getViewIntent() {
        return mViewIntent;
    }

    public String getMetaFont() {
        return mMetaFont;
    }

    public Date getDateAdded() {
        return mDateAdded;
    }

    public void setComponentName(Context context, Class<? extends MuzeiArtSource> source) {
        mComponentName = new ComponentName(context, source);
    }

    public void setComponentName(ComponentName source) {
        mComponentName = source;
    }

    public void setImageUri(Uri imageUri) {
        mImageUri = imageUri;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setByline(String byline) {
        mByline = byline;
    }

    public void setAttribution(String attribution) {
        mAttribution = attribution;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public void setViewIntent(Intent viewIntent) {
        mViewIntent = viewIntent;
    }

    public void setMetaFont(String metaFont) {
        mMetaFont = metaFont;
    }

    public void setDateAdded(Date dateAdded) {
        mDateAdded = dateAdded;
    }

    public static class Builder {
        private Artwork mArtwork;

        public Builder() {
            mArtwork = new Artwork();
        }

        public Builder componentName(Context context, Class<? extends MuzeiArtSource> source) {
            mArtwork.mComponentName = new ComponentName(context, source);
            return this;
        }

        public Builder componentName(ComponentName source) {
            mArtwork.mComponentName = source;
            return this;
        }

        public Builder imageUri(Uri imageUri) {
            mArtwork.mImageUri = imageUri;
            return this;
        }

        public Builder title(String title) {
            mArtwork.mTitle = title;
            return this;
        }

        public Builder byline(String byline) {
            mArtwork.mByline = byline;
            return this;
        }

        public Builder attribution(String attribution) {
            mArtwork.mAttribution = attribution;
            return this;
        }

        public Builder token(String token) {
            mArtwork.mToken = token;
            return this;
        }

        public Builder viewIntent(Intent viewIntent) {
            mArtwork.mViewIntent = viewIntent;
            return this;
        }

        public Builder metaFont(String metaFont) {
            mArtwork.mMetaFont = metaFont;
            return this;
        }

        public Builder dateAdded(Date dateAdded) {
            mArtwork.mDateAdded = dateAdded;
            return this;
        }

        public Artwork build() {
            return mArtwork;
        }
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_COMPONENT_NAME, (mComponentName != null) ? mComponentName.flattenToShortString() : null);
        bundle.putString(KEY_IMAGE_URI, (mImageUri != null) ? mImageUri.toString() : null);
        bundle.putString(KEY_TITLE, mTitle);
        bundle.putString(KEY_BYLINE, mByline);
        bundle.putString(KEY_ATTRIBUTION, mAttribution);
        bundle.putString(KEY_TOKEN, mToken);
        bundle.putString(KEY_VIEW_INTENT, (mViewIntent != null)
                ? mViewIntent.toUri(Intent.URI_INTENT_SCHEME) : null);
        bundle.putString(KEY_META_FONT, mMetaFont);
        bundle.putLong(KEY_DATE_ADDED, mDateAdded != null ? mDateAdded.getTime() : 0);
        return bundle;
    }

    public static Artwork fromBundle(Bundle bundle) {
        Builder builder = new Builder()
                .title(bundle.getString(KEY_TITLE))
                .byline(bundle.getString(KEY_BYLINE))
                .attribution(bundle.getString(KEY_ATTRIBUTION))
                .token(bundle.getString(KEY_TOKEN))
                .metaFont(bundle.getString(KEY_META_FONT))
                .dateAdded(new Date(bundle.getLong(KEY_DATE_ADDED, 0)));

        String componentName = bundle.getString(KEY_COMPONENT_NAME);
        if (!TextUtils.isEmpty(componentName)) {
            builder.componentName(ComponentName.unflattenFromString(componentName));
        }

        String imageUri = bundle.getString(KEY_IMAGE_URI);
        if (!TextUtils.isEmpty(imageUri)) {
            builder.imageUri(Uri.parse(imageUri));
        }

        try {
            String viewIntent = bundle.getString(KEY_VIEW_INTENT);
            if (!TextUtils.isEmpty(viewIntent)) {
                builder.viewIntent(Intent.parseUri(viewIntent, Intent.URI_INTENT_SCHEME));
            }
        } catch (URISyntaxException ignored) {
        }

        return builder.build();
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_COMPONENT_NAME, (mComponentName != null) ? mComponentName.flattenToShortString() : null);
        jsonObject.put(KEY_IMAGE_URI, (mImageUri != null) ? mImageUri.toString() : null);
        jsonObject.put(KEY_TITLE, mTitle);
        jsonObject.put(KEY_BYLINE, mByline);
        jsonObject.put(KEY_ATTRIBUTION, mAttribution);
        jsonObject.put(KEY_TOKEN, mToken);
        jsonObject.put(KEY_VIEW_INTENT, (mViewIntent != null)
                ? mViewIntent.toUri(Intent.URI_INTENT_SCHEME) : null);
        jsonObject.put(KEY_META_FONT, mMetaFont);
        jsonObject.put(KEY_DATE_ADDED, mDateAdded != null ? mDateAdded.getTime() : 0);
        return jsonObject;
    }

    public static Artwork fromJson(JSONObject jsonObject) throws JSONException {
        Builder builder = new Builder()
                .title(jsonObject.optString(KEY_TITLE))
                .byline(jsonObject.optString(KEY_BYLINE))
                .attribution(jsonObject.optString(KEY_ATTRIBUTION))
                .token(jsonObject.optString(KEY_TOKEN))
                .metaFont(jsonObject.optString(KEY_META_FONT))
                .dateAdded(new Date(jsonObject.optLong(KEY_DATE_ADDED, 0)));

        String componentName = jsonObject.optString(KEY_COMPONENT_NAME);
        if (!TextUtils.isEmpty(componentName)) {
            builder.componentName(ComponentName.unflattenFromString(componentName));
        }

        String imageUri = jsonObject.optString(KEY_IMAGE_URI);
        if (!TextUtils.isEmpty(imageUri)) {
            builder.imageUri(Uri.parse(imageUri));
        }

        try {
            String viewIntent = jsonObject.optString(KEY_VIEW_INTENT);
            String detailsUri = jsonObject.optString(KEY_DETAILS_URI);
            if (!TextUtils.isEmpty(viewIntent)) {
                builder.viewIntent(Intent.parseUri(viewIntent, Intent.URI_INTENT_SCHEME));
            } else if (!TextUtils.isEmpty(detailsUri)) {
                builder.viewIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(detailsUri)));
            }
        } catch (URISyntaxException ignored) {
        }

        return builder.build();
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(MuzeiContract.Artwork.COLUMN_NAME_SOURCE_COMPONENT_NAME, (mComponentName != null)
                ? mComponentName.flattenToShortString() : null);
        values.put(MuzeiContract.Artwork.COLUMN_NAME_IMAGE_URI, (mImageUri != null)
                ? mImageUri.toString() : null);
        values.put(MuzeiContract.Artwork.COLUMN_NAME_TITLE, mTitle);
        values.put(MuzeiContract.Artwork.COLUMN_NAME_BYLINE, mByline);
        values.put(MuzeiContract.Artwork.COLUMN_NAME_ATTRIBUTION, mAttribution);
        values.put(MuzeiContract.Artwork.COLUMN_NAME_TOKEN, mToken);
        values.put(MuzeiContract.Artwork.COLUMN_NAME_VIEW_INTENT, (mViewIntent != null)
                ? mViewIntent.toUri(Intent.URI_INTENT_SCHEME) : null);
        values.put(MuzeiContract.Artwork.COLUMN_NAME_META_FONT, mMetaFont);
        values.put(MuzeiContract.Artwork.COLUMN_NAME_DATE_ADDED, mDateAdded != null ?
                mDateAdded.getTime() : 0);
        return values;
    }

    public static Artwork fromCursor(Cursor cursor) {
        Builder builder = new Builder();
        int componentNameColumnIndex = cursor.getColumnIndex(MuzeiContract.Artwork.COLUMN_NAME_SOURCE_COMPONENT_NAME);
        if (componentNameColumnIndex != -1) {
            builder.componentName(ComponentName.unflattenFromString(cursor.getString(componentNameColumnIndex)));
        }
        int imageUriColumnIndex = cursor.getColumnIndex(MuzeiContract.Artwork.COLUMN_NAME_IMAGE_URI);
        if (imageUriColumnIndex != -1) {
            builder.imageUri(Uri.parse(cursor.getString(imageUriColumnIndex)));
        }
        int titleColumnIndex = cursor.getColumnIndex(MuzeiContract.Artwork.COLUMN_NAME_TITLE);
        if (titleColumnIndex != -1) {
            builder.title(cursor.getString(titleColumnIndex));
        }
        int bylineColumnIndex = cursor.getColumnIndex(MuzeiContract.Artwork.COLUMN_NAME_BYLINE);
        if (bylineColumnIndex != -1) {
            builder.byline(cursor.getString(bylineColumnIndex));
        }
        int attributionColumnIndex = cursor.getColumnIndex(MuzeiContract.Artwork.COLUMN_NAME_ATTRIBUTION);
        if (attributionColumnIndex != -1) {
            builder.attribution(cursor.getString(attributionColumnIndex));
        }
        int tokenColumnIndex = cursor.getColumnIndex(MuzeiContract.Artwork.COLUMN_NAME_TOKEN);
        if (tokenColumnIndex != -1) {
            builder.token(cursor.getString(tokenColumnIndex));
        }
        int viewIntentColumnIndex = cursor.getColumnIndex(MuzeiContract.Artwork.COLUMN_NAME_VIEW_INTENT);
        if (viewIntentColumnIndex != -1) {
            try {
                String viewIntent = cursor.getString(viewIntentColumnIndex);
                if (!TextUtils.isEmpty(viewIntent)) {
                    builder.viewIntent(Intent.parseUri(viewIntent, Intent.URI_INTENT_SCHEME));
                }
            } catch (URISyntaxException ignored) {
            }
        }
        int metaFontColumnIndex = cursor.getColumnIndex(MuzeiContract.Artwork.COLUMN_NAME_META_FONT);
        if (metaFontColumnIndex != -1) {
            builder.metaFont(cursor.getString(metaFontColumnIndex));
        }
        int dateAddedColumnIndex = cursor.getColumnIndex(MuzeiContract.Artwork.COLUMN_NAME_DATE_ADDED);
        if (dateAddedColumnIndex != -1) {
            builder.dateAdded(new Date(cursor.getLong(dateAddedColumnIndex)));
        }
        return builder.build();
    }
}
