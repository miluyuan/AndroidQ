package com.example.androidq.background;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.example.androidq.Constants;
import com.example.androidq.LogUtil;
import com.example.androidq.R;
import com.example.androidq.storage.ScopedStorageActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class BackgroundStartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_start);

        findViewById(R.id.btn1).setOnClickListener(v -> backgroundStartActivity());
        findViewById(R.id.btn2).setOnClickListener(v -> fromService());
        findViewById(R.id.btn3).setOnClickListener(v -> fullscreenNotification());
    }

    private void fullscreenNotification() {
        showHighNotification();
    }

    int REQ_CODE = 345;
    /**
     * 这种方式只有在设置中开启了横幅通知才有效；
     * 开启后发送通知直接启动Activity；
     * 默认都是关闭的
     */
    private void showHighNotification() {
        Intent intent = new Intent(this, ScopedStorageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Incoming call")
                .setContentText("(919) 555-1234")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                //设置全屏通知后，发送通知直接启动Activity
                .setFullScreenIntent(pendingIntent, true)
                .build();
        NotificationManager manager = getSystemService(NotificationManager.class);

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                manager.notify(445456, notification);
            }
        }, 2000);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void fromService() {
        startForegroundService(new Intent(this, ForegroundService.class));
    }


    /**
     * 在AndroidQ上启动无反应，也没有日志
     */
    private void backgroundStartActivity() {
        findViewById(R.id.btn3).postDelayed(() -> {
            LogUtil.log("start backgroundActivity");
            startActivity(new Intent(this, ScopedStorageActivity.class));
        }, 3000);

    }
}
