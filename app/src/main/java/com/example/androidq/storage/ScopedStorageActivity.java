package com.example.androidq.storage;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import com.example.androidq.LogUtil;
import com.example.androidq.R;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

/**
 * Android Q 隐私权变更：分区存储
 */
public class ScopedStorageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoped_storage);

        findViewById(R.id.btn1).setOnClickListener(v -> reqPermissions());
        findViewById(R.id.btn2).setOnClickListener(v -> writeFile());
        findViewById(R.id.btn3).setOnClickListener(v -> {
            try {
                readFile();
            } catch (IOException e) {
                LogUtil.log(e);
            }
        });
    }

    private void readFile() throws IOException {
        File extDir = Environment.getExternalStorageDirectory();
        File trtms = new File(extDir, "trtms");
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", trtms);
        LogUtil.log("trtms uri: " + uri);
        LogUtil.log("trtms path: " + trtms.getAbsolutePath());
//        InputStream input = getContentResolver().openInputStream(uri);
//        int read = input.read();
//        LogUtil.log("read int: " + read);
//        LogUtil.log("read char: " + (char) read);

        if (trtms.isDirectory()) {
            // 返回的files为null
            File[] files = trtms.listFiles();
            if (files == null) {
                LogUtil.log("no files.");
                return;
            }
            for (File file : files) {
                LogUtil.log("trtms file: " + file.getAbsolutePath());
            }
        }
    }

    private void writeFile() {

    }

    private void reqPermissions() {
        requestPermissions(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, 333);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
            LogUtil.log(permissions[0] + " PERMISSION_GRANTED");
        }
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
