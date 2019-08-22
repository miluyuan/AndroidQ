package com.example.androidq.deviceId;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.example.androidq.LogUtil;
import com.example.androidq.R;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 官方建议文档：
 * https://developer.android.google.cn/training/articles/user-data-ids
 */
public class DeviceIdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_id);

//        printRandomUUID();

//        printDeviceId();
//        getAndroidId(this);

        LogUtil.log("deviceId：" + getDeviceId());
        LogUtil.log("makeDeviceId：" + makeDeviceId());
        LogUtil.log("AndroidId：" + DeviceId.getAndroidId(this));

    }

    private String makeDeviceId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 33);
            return "Permission Denied.";
        }
        return DeviceId.makeDeviceId(this);
    }

    /**
     * 1. target>=AndroidQ时在AndroidQ系统上抛出异常
     * SecurityException: getDeviceId: The user 10196 does not meet the requirements to access device identifiers.
     * <p>
     * 2. target<Q时在Q系统上返回null
     * 3. 在小于Q的系统上返回正常
     */
    @TargetApi(Build.VERSION_CODES.M)
    private String getDeviceId() {
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 33);
            return "Permission Denied.";
        } else {
            //866976045561713
            return DeviceId.getDeviceId(this);
        }
    }


    /**
     * UUID.randomUUID()每次返回的值都不一样
     */
    private void printRandomUUID() {
        for (int i = 0; i < 10; i++) {
            String uniqueID = UUID.randomUUID().toString();
            LogUtil.log("randomUUID: " + uniqueID);
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            LogUtil.log(tm.getDeviceId());
        }
    }


}
