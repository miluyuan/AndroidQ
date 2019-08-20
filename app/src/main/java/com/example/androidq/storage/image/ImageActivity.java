package com.example.androidq.storage.image;

import android.Manifest;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidq.LogUtil;
import com.example.androidq.R;
import com.example.androidq.Utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class ImageActivity extends AppCompatActivity {

    private ImageView mImg;
    private TextView tv;
    private int index = 0;
    List<String> mData;
    List<Uri> mData2;
    private int flag = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mImg = findViewById(R.id.img);
        tv = findViewById(R.id.tv);
        mImg.setOnClickListener(v -> showImg());
        findViewById(R.id.btn1).setOnClickListener(v -> reqPermission());
        findViewById(R.id.btn2).setOnClickListener(v -> loadImage());
        findViewById(R.id.btn3).setOnClickListener(v -> loadImage2());
        findViewById(R.id.btn4).setOnClickListener(v -> saveImage());
        findViewById(R.id.btn5).setOnClickListener(v -> loadDownload());
    }

    private void saveImage() {
        Uri uri;
        if (flag == 1) {
            // 直接Uri.parse(uriStr)不行
            uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", new File(mData.get(index)));
        } else {
            uri = mData2.get(index);
        }
        Bitmap bitmap = Utils.getBitmapFromUri(this, uri);
        saveBitmap(bitmap);
        LogUtil.log("saved image uri: " + uri.toString());
    }

    /**
     * 保存图片到Picture目录
     */
    private void saveBitmap(Bitmap bitmap) {
        // content://media/external/images/media
        LogUtil.log("EXTERNAL_CONTENT_URI: " + MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //当文件名与已有图片相同时加"(1)"
        MediaStore.Images.Media.insertImage(getContentResolver(),
                bitmap, "saveImage" + index + ".jpg", "image description1");
    }

    private void showImg() {
        if (flag == 1) {
            mImg.setImageURI(Uri.parse(mData.get(index)));
        } else {
            mImg.setImageURI(mData2.get(index));
        }
        index++;
    }

    /**
     * 获取所有图片的Uri
     * 来自华为文档
     */
    private void loadImage2() {
        flag = 2;
        List<Uri> photoUris = new ArrayList<Uri>();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                null, null, null);
        LogUtil.log("cursor size:" + cursor.getCount());
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Images.Media._ID));
            Uri photoUri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
            LogUtil.log("photoUri:" + photoUri);
            photoUris.add(photoUri);
        }

        index = 0;
        mData2 = photoUris;
        mImg.setImageURI(mData2.get(index));
        index++;
    }

    /**
     * 通过MediaStore加载下载文档
     * 不靠谱
     */
    private void loadDownload() {
        List<Uri> photoUris = new ArrayList<Uri>();
        Cursor cursor = getContentResolver().query(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Downloads._ID},
                null, null, null);
        LogUtil.log("cursor size:" + cursor.getCount());
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Images.Media._ID));
            Uri photoUri = Uri.parse(MediaStore.Downloads.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
            LogUtil.log("photoUri:" + photoUri);
            photoUris.add(photoUri);
        }

        try {
            FileDescriptor r = getContentResolver().openFileDescriptor(photoUris.get(0), "r").getFileDescriptor();
            InputStreamReader input = new InputStreamReader(new FileInputStream(r));
            char[] chars = new char[1024];
            int read = input.read(chars, 0, 1024);
            tv.setText(new String(chars));
            input.close();
        } catch (IOException e) {
            //java.io.IOException: read failed: EISDIR (Is a directory)
            e.printStackTrace();
            LogUtil.log(e);
        }
    }

    /**
     * 获取图片的绝对路径,只读不可写，
     * 通过LoaderCallback可以知道图片所在的文件夹等数据
     */
    private void loadImage() {
        flag = 1;
        LoaderCallback callback = new LoaderCallback(this, (List<String> data) -> {
            LogUtil.log("img count: " + data.size());
            index = 0;
            mData = data;
            mImg.setImageURI(Uri.parse(data.get(index)));
            index++;
        });

        Loader<Cursor> cursorLoader = LoaderManager.getInstance(this)
                .initLoader(0, new Bundle(), callback);
    }

    private void reqPermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 341);
    }
}
