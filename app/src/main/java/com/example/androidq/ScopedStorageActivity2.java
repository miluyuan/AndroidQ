package com.example.androidq;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ScopedStorageActivity2 extends AppCompatActivity {
    private static final String TAG = "ScopedStorageActivity2";
    private static final int WRITE_REQUEST_CODE = 55;
    private static final int READ_REQUEST_CODE = 44;
    private static final int REQ_CODE_PERMISSIONS = 33;

    String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private TextView tv;
    private ImageView iv;
    private String mText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoped_storage2);

        findViewById(R.id.btn0).setOnClickListener(v -> systemPermissionActivity());
        findViewById(R.id.btn1).setOnClickListener(v -> reqPermissions());
        findViewById(R.id.btn2).setOnClickListener(v -> readFilterView());
        findViewById(R.id.btn3).setOnClickListener(v -> writeFilterView());
        findViewById(R.id.btn4).setOnClickListener(v -> readMediaFile());
        findViewById(R.id.btn5).setOnClickListener(v -> writeMediaFile());
        findViewById(R.id.btn6).setOnClickListener(v -> readOtherFile());

        tv = findViewById(R.id.tv);
        iv = findViewById(R.id.iv);
    }

    private void readOtherFile() {
        performFileSearch();
    }

    /**
     * 打开目录让用户自己去选择指定类型文件
     */
    public void performFileSearch() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private void writeMediaFile() {
//        systemCreateFile();
//        saveMediaImage1();
        saveMediaImage2();
    }

    /**
     * 保存多媒体文件方式2
     */
    private void saveMediaImage2() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.guide_image_upload_receipt);
        //把一个图片文件保存到/sdcard/dcim/test/下面
        String uri = Utils.insertMediaFile(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, this,
                "image/jpeg", "insert_test_img",
                "test img save use insert", bitmap,
                "test", Environment.DIRECTORY_DCIM);
        // content://media/external/images/media/214000
        LogUtil.log(uri);
        // /storage/emulated/0/Pictures/insert_test_img.jpg
        LogUtil.log(Utils.getDataColumn(this, Uri.parse(uri), null, null));
    }

    /**
     * 保存多媒体文件方式1
     */
    private void saveMediaImage1() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.guide_image_upload_receipt);
        String uri = Utils.saveBitmapToFile(this, bitmap, "wang.png", "Stephen Description.");
        // content://media/external/images/media/213998
        LogUtil.log(uri);
        // /storage/emulated/0/Pictures/wang.png.jpg
        LogUtil.log(Utils.getDataColumn(this, Uri.parse(uri), null, null));
    }

    /**
     * 创建文件，这种方式由用户指定保存路径，返回uri。
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void systemCreateFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        // "text/plain"、"image/png"
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "android-Q.txt");
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    private void readMediaFile() {
        List<Uri> uris = Utils.loadPhotoFiles(this);
        Uri uri = uris.get(0);
        // content://media/external/images/media/24111
        LogUtil.log("uri: " + uri);
        // /storage/emulated/0/Pictures/Screenshots/Screenshot_20181006-094912.png
        LogUtil.log("absolutePath: " + Utils.getDataColumn(this, uri, null, null));
        iv.setImageURI(uri);
    }

    /**
     * 读写过滤视图中文件不用申请权限
     */
    private void writeFilterView() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_ALARMS);
        File file = new File(dir, "wang.txt");
        if (!file.exists()) {
            try {
                boolean success = file.createNewFile();
                mText = success ? "文件创建成功!" : "文件创建失败!";
                //0/Android/data/com.example.androidq/files/Alarms/wang.txt
                mText += ("\n" + file.getAbsolutePath());
                tv.setText(mText);
            } catch (IOException e) {
                e.printStackTrace();
                tv.setText(e.toString());
            }
        } else {
            tv.setText("文件已存在!");
        }
    }

    private void readFilterView() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_ALARMS);
        File file = new File(dir, "wang.txt");
        if (!file.exists()) {
            mText += ("\n no file:" + file.getName());
        } else {
            mText += ("\n has file:" + file.getName());
        }
        tv.setText(mText);
    }

    private void systemPermissionActivity() {
        Intent panelIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        panelIntent.setData(uri);
        startActivity(panelIntent);
    }

    private void reqPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, REQ_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                Toast.makeText(this, "permissions granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (WRITE_REQUEST_CODE == requestCode) {
            Uri uri = data.getData();
            iv.setImageURI(uri);
            // content://com.android.externalstorage.documents/document/home%3Aandroid-Q.txt
            LogUtil.log("uri: " + uri);
            // null
            LogUtil.log("abstractPath: " + Utils.getDataColumn(this, uri, null, null));
        } else if (READ_REQUEST_CODE == requestCode) {
            Uri uri = data.getData();
            iv.setImageURI(uri);
            // content://com.android.externalstorage.documents/document/primary%3ADCIM%2F1555739210230.jpg
            LogUtil.log("uri: " + uri);
            // null
            LogUtil.log("abstractPath: " + Utils.getDataColumn(this, uri, null, null));
        }
    }
}
