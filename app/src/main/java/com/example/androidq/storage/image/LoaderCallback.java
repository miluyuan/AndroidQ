package com.example.androidq.storage.image;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import com.example.androidq.LogUtil;
import com.example.androidq.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.SIZE;

/**
 * @author wzw
 * @date 2019/8/20 10:00
 */
public class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
    private Context mContext;

    interface LoadFinishedListener {
        void onLoaderFinished(List<String> data);
    }

    private LoadFinishedListener mListener;

    public LoaderCallback(Context context, LoadFinishedListener listener) {
        mContext = context;
        mListener = listener;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new PhotoDirectoryLoader(mContext);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Cursor data) {
        if (data == null) {
            return;
        }

        ArrayList<String> list = new ArrayList<>();
        while (data.moveToNext()) {
            int imageId = data.getInt(data.getColumnIndexOrThrow(_ID));
            String bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID));
            String name = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
            String path = data.getString(data.getColumnIndexOrThrow(DATA));
            long size = data.getInt(data.getColumnIndexOrThrow(SIZE));
            long dateAdded = data.getLong(data.getColumnIndexOrThrow(DATE_ADDED));
            String dateFormat = Utils.dateFormat(dateAdded);

            list.add(path);
            LogUtil.log(
                    "imageId: " + imageId
                            + ", bucketId: " + bucketId
                            + ", name: " + name
                            + ", path: " + path
                            + ", size: " + size
//                            + ", dateFormat:  " + dateFormat

            );
            if (size < 1) {
                continue;
            }
//            photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));
        }

        if (mListener != null) {
            mListener.onLoaderFinished(list);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}
