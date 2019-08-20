package com.example.androidq.permission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.androidq.R;
import com.example.androidq.background.ForegroundService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class BackgroundLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_location);

        findViewById(R.id.btn1).setOnClickListener(v -> reqPermissions());
        findViewById(R.id.btn2).setOnClickListener(v -> LocationUtil.init(this));
        findViewById(R.id.btn3).setOnClickListener(v ->
                startForegroundService(new Intent(this, LocationService.class)));
    }

    private void reqPermissions() {
        requestPermissions(new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        }, 444);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "成功：" + permissions[2], Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show();
        }
    }
}
