package com.example.androidq.background;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.example.androidq.LogUtil;
import com.example.androidq.Utils;
import com.example.androidq.storage.ScopedStorageActivity;

/**
 * @author wzw
 * @date 2019/8/14 13:59
 */
public class ForegroundService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.log("Service started");
        startForeground(22, Utils.buildNotification(this, "后台启动Activity",
                "测试通过前台服务启动Activity是否成功"));

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //通过前台服务的形式后台启动Activity也是无效的
                LogUtil.log("start backgroundActivity on service");
                Intent intent1 = new Intent(ForegroundService.this, ScopedStorageActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            }
        }, 3000);

        return super.onStartCommand(intent, flags, startId);
    }


}
