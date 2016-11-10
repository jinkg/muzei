package com.yalin.muzei.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.yalin.muzei.api.MuzeiArtSource;

/**
 * YaLin
 * 2016/11/10.
 */

public class GallerySetupActivity extends FragmentActivity {
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CHOOSE_IMAGES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cursor chosenUris = getContentResolver().query(GalleryContract.ChosenPhotos.CONTENT_URI,
                null, null, null, null);
        int numChosenUris = chosenUris != null ? chosenUris.getCount() : 0;
        if (chosenUris != null) {
            chosenUris.close();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED || numChosenUris > 0) {
            // If we have permission or have any previously selected images
            setResult(RESULT_OK);
            finish();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setResult(RESULT_OK);
            finish();
        } else {
            // Push the user to the GallerySettingsActivity to see inline rationale or just
            // select individual photos
            Intent intent = new Intent(this, GallerySettingsActivity.class);
            if (getIntent().getBooleanExtra(MuzeiArtSource.EXTRA_FROM_MUZEI_SETTINGS, false)) {
                intent.putExtra(MuzeiArtSource.EXTRA_FROM_MUZEI_SETTINGS, true);
            }
            startActivityForResult(intent, REQUEST_CHOOSE_IMAGES);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CHOOSE_IMAGES) {
            return;
        }
        // Pass on the resultCode from the GallerySettingsActivity onto Muzei
        setResult(resultCode);
        finish();
    }

}
