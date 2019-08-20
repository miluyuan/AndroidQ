package com.example.androidq;

import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.androidq.background.BackgroundStartActivity;
import com.example.androidq.deviceId.DeviceIdActivity;
import com.example.androidq.permission.BackgroundLocationActivity;
import com.example.androidq.storage.ScopedStorageActivity;
import com.example.androidq.storage.StorageMainActivity;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn1).setOnClickListener(view ->
                startActivity(new Intent(this, StorageMainActivity.class)));

        findViewById(R.id.btn2).setOnClickListener(view ->
                startActivity(new Intent(this, DeviceIdActivity.class)));
        findViewById(R.id.btn3).setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, BackgroundStartActivity.class)));

        findViewById(R.id.btn4).setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, BackgroundLocationActivity.class)));

    }

    private void macAddress() {
        // 02:00:00:00:00:00
        Log.e(TAG, "onCreate: " + new WifiConfiguration().getRandomizedMacAddress());

        //external_primary
        Log.e(TAG, MediaStore.getExternalVolumeNames(this).toString());
        Uri uri = MediaStore.Images.Media.getContentUri(MediaStore.getExternalVolumeNames(this).iterator().next());
        Log.e(TAG, "uri: " + uri);
    }

    private void fileDirectory() {
        // 0/Android/data/com.example.androidq/files/Documents
        //这个目录下不需要申请权限
        File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        // 0/Documents
        // 不能直接访问，Permission Denied，需要FileProvider
        File dir2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        File file = new File(dir2, "wang2.txt");
        if (!file.exists()) {
            try {
                boolean succ = file.createNewFile();
                Log.e(TAG, "success: " + succ);
            } catch (IOException e) {
                Log.e(TAG, "Exception ", e);
            }
        }

        Log.e(TAG, "onCreate: " + dir.getAbsolutePath());
        Log.e(TAG, "onCreate: " + dir2.getAbsolutePath());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            Log.e(TAG, "permission: " + permissions[i] + "->" + grantResults[i]);
        }
    }

    /**
     * 通过FileProvider分享文件
     *
     * @param picFile
     */
    private void sharePicFile(File picFile) {
        try {
            // Use the FileProvider to get a content URI
            //获得分享文件的Content Uri：
            Uri fileUri = FileProvider.getUriForFile(
                    this,
                    "com.huawei.qappcompatissues.fileprovider",
                    picFile);
            Log.e(TAG, "fileUri:" + fileUri);
            if (fileUri != null) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                // Grant temporary read permission to the content URI
                //临时授予文件接收方的文件读写权限：
                intent.addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // Put the Uri and MIME type in the result Intent
                intent.setDataAndType(
                        fileUri,
                        getContentResolver().getType(fileUri));
                startActivity(Intent.createChooser(intent, "test file share"));
            } else {
                Toast.makeText(this, "share file error", Toast.LENGTH_SHORT).show();
            }
        } catch (IllegalArgumentException e) {
            Log.e("File Selector",
                    "The selected file can't be shared: " + picFile.toString());
        }
    }

    //    private void requestPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            String[] permissions = new String[]{
//                    Manifest.permission.READ_MEDIA_IMAGES,
//                    Manifest.permission.READ_MEDIA_AUDIO,
//                    Manifest.permission.READ_MEDIA_VIDEO};
//            if (ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
//                        MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES);
//            }
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // request old storage permission
//        }
//    }


}
