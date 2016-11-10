package com.yalin.muzei.gallery;

import android.net.Uri;
import android.provider.BaseColumns;

import com.yalin.muzei.BuildConfig;

/**
 * YaLin
 * 2016/11/10.
 */

public class GalleryContract {
    static final String AUTHORITY = BuildConfig.GALLERY_AUTHORITY;
    private static final String SCHEME = "content://";

    private GalleryContract() {
    }


    public static final class ChosenPhotos implements BaseColumns {
        /**
         * Column name of the chosen photo's URI.
         * <p>Type: TEXT (URI)
         */
        public static final String COLUMN_NAME_URI = "uri";
        /**
         * Column name for the flag indicating the URI is a tree URI
         * <p>Type: INTEGER (boolean)
         */
        public static final String COLUMN_NAME_IS_TREE_URI = "is_tree_uri";
        /**
         * The MIME type of {@link #CONTENT_URI} providing chosen photos.
         */
        static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.android.apps.muzei.gallery.chosen_photos";
        /**
         * The MIME type of {@link #CONTENT_URI} providing a single chosen photo.
         */
        static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.android.apps.muzei.gallery.chosen_photos";
        /**
         * The default sort order for this table
         */
        static final String DEFAULT_SORT_ORDER = BaseColumns._ID;
        /**
         * The table name offered by this provider.
         */
        static final String TABLE_NAME = "chosen_photos";

        private ChosenPhotos() {
        }

        public static final Uri CONTENT_URI = Uri.parse(GalleryContract.SCHEME + GalleryContract.AUTHORITY
                + "/" + ChosenPhotos.TABLE_NAME);
    }

    static final class MetadataCache implements BaseColumns {
        /**
         * Column name of the photo's URI.
         * <p>Type: TEXT (URI)
         */
        static final String COLUMN_NAME_URI = "uri";
        /**
         * Column name for when this photo was taken
         * <p>Type: LONG (in milliseconds)
         */
        static final String COLUMN_NAME_DATETIME = "datetime";
        /**
         * Column name for the photo's location.
         * <p>Type: TEXT
         */
        static final String COLUMN_NAME_LOCATION = "location";
        /**
         * The MIME type of {@link #CONTENT_URI} providing metadata.
         */
        static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.android.apps.muzei.gallery.metadata_cache";
        /**
         * The default sort order for this table
         */
        static final String DEFAULT_SORT_ORDER = BaseColumns._ID + " DESC";
        /**
         * The table name offered by this provider.
         */
        static final String TABLE_NAME = "metadata_cache";

        private MetadataCache() {
        }

        static final Uri CONTENT_URI = Uri.parse(GalleryContract.SCHEME + GalleryContract.AUTHORITY
                + "/" + MetadataCache.TABLE_NAME);
    }
}
