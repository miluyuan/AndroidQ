package com.example.androidq.storage.image;

import android.content.Context;
import android.provider.MediaStore.Images.Media;

import androidx.loader.content.CursorLoader;

import static android.provider.MediaStore.MediaColumns.MIME_TYPE;

class PhotoDirectoryLoader extends CursorLoader {

    PhotoDirectoryLoader(Context context) {
        super(context);

        String[] IMAGE_PROJECTION = {
                Media._ID,
                Media.DATA,
                Media.BUCKET_ID,
                Media.BUCKET_DISPLAY_NAME,
                Media.DATE_ADDED,
                Media.SIZE
        };

        setProjection(IMAGE_PROJECTION);
        setUri(Media.EXTERNAL_CONTENT_URI);
        setSortOrder(Media.DATE_ADDED + " DESC");

        setSelection(MIME_TYPE + "=? or " + MIME_TYPE + "=? or " + MIME_TYPE + "=? ");
        String[] selectionArgs = new String[]{"image/jpeg", "image/png", "image/jpg"};

        setSelectionArgs(selectionArgs);
    }

}