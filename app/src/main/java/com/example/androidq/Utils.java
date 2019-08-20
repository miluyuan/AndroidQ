package com.example.androidq;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author wzw
 * @date 2019/8/13 15:35
 */
public class Utils {

    public static List<Uri> loadPhotoFiles(Context context) {
        List<Uri> photoUris = new ArrayList<>();
        //需要存储权限，在Android9及以下会抛异常，AndroidQ上cursor.getCount()为0
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                null, null, null);
        if (cursor == null) {
            LogUtil.log("cursor is null. ");
            return photoUris;
        }
        LogUtil.log("cursor size:" + cursor.getCount());
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Images.Media._ID));
            Uri photoUri = Uri.parse(MediaStore.Images.Media.INTERNAL_CONTENT_URI.toString()
                    + File.separator + id);
            // content://media/external/images/media/212794
            LogUtil.log("photoUri:" + photoUri);
            photoUris.add(photoUri);
        }
        cursor.close();
        return photoUris;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        final String column = "_data";
        final String[] projection = {column};
        try (Cursor cursor = context.getContentResolver()
                .query(uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }
        return null;
    }

    /**
     * 保存图片到Picture目录
     *
     * @param context
     * @param bitmap
     * @param title
     * @param description
     * @return
     */
    public static String saveBitmapToFile(Context context, Bitmap bitmap, String title, String description) {
        return MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title, description);
    }

    /**
     * 保存多媒体文件到公共集合目录
     *
     * @param uri：多媒体数据库的Uri
     * @param context
     * @param mimeType：需要保存文件的mimeType
     * @param displayName：显示的文件名字
     * @param description：文件描述信息
     * @param saveFileName：需要保存的文件名字
     * @param saveSecondaryDir：保存的二级目录
     * @param savePrimaryDir：保存的一级目录
     * @return 返回插入数据对应的uri
     */
    public static String insertMediaFile(Uri uri, Context context, String mimeType,
                                         String imgName, String description, Bitmap bmp,
                                         String saveSecondaryDir, String savePrimaryDir) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imgName);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
//        values.put(MediaStore.Images.Media.PRIMARY_DIRECTORY, savePrimaryDir);
//        values.put(MediaStore.Images.Media.SECONDARY_DIRECTORY, saveSecondaryDir);
        Uri url = null;
        String stringUrl = null;    /* value to be returned */
        ContentResolver cr = context.getContentResolver();
        try {
            url = cr.insert(uri, values);
            if (url == null) {
                return null;
            }
            ParcelFileDescriptor parcelFileDescriptor = cr.openFileDescriptor(url, "w");
            FileOutputStream fileOutputStream =
                    new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            int quality = 90;
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, bao);
            byte[] buffer = bao.toByteArray();
            fileOutputStream.write(buffer, 0, buffer.length);
            fileOutputStream.flush();
        } catch (Exception e) {
            LogUtil.log("Failed to insert media file" + e);
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }
        if (url != null) {
            stringUrl = url.toString();
        }
        return stringUrl;
    }

    private Drawable getDrawableFromUri(Uri uri, Context context) {
        final String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)
                || ContentResolver.SCHEME_FILE.equals(scheme)) {
            try {
                Resources res = context.getResources();
                ImageDecoder.Source src = ImageDecoder.createSource(context.getContentResolver(), uri);
                return ImageDecoder.decodeDrawable(src, (decoder, info, s) -> {
                    decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE);
                });
            } catch (IOException e) {
//                Log.w(LOG_TAG, "Unable to open content: " + uri, e);
            }
        } else {
            return Drawable.createFromPath(uri.toString());
        }
        return null;
    }

    /**
     * AndroidId
     *
     * @param context
     * @return
     */
    private static String generateUniqueId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String temp = androidId;

        try {
            return UUID.nameUUIDFromBytes(temp.getBytes("utf8")).toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return androidId;
    }

    public static void systemHome(Activity context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        context.startActivity(intent);

    }

    @TargetApi(Build.VERSION_CODES.O)
    public static Notification buildNotification(Context context, String title, String content) {
        createChannel(context);
        Notification.Builder builder = new Notification.Builder(context, Constants.CHANNEL_ID);
        Notification no = builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .build();
        return no;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void createChannel(Context context) {
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        NotificationChannel serviceChannel = new NotificationChannel(Constants.CHANNEL_ID,
                "service", NotificationManager.IMPORTANCE_HIGH);
        serviceChannel.setSound(null, null);
        manager.createNotificationChannel(serviceChannel);
    }


    public static String dateFormat(long date) {
        return SimpleDateFormat.getInstance().format(new Date(date));
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        ParcelFileDescriptor r = null;
        Bitmap bitmap = null;
        try {
            r = context.getContentResolver().openFileDescriptor(uri, "r");
            if (r == null) return null;
            bitmap = BitmapFactory.decodeFileDescriptor(r.getFileDescriptor());
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.log(e.toString());
        }

        return bitmap;
    }

}
