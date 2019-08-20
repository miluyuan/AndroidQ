package com.example.androidq.permission;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.example.androidq.LogUtil;
import com.example.androidq.Utils;

/**
 * @author wzw
 * @date 2019/8/15 10:39
 */
public class LocationService extends Service {
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
        LogUtil.log("location Service started.");
        startForeground(553, Utils.buildNotification(this, "测试后台定位权限",
                "测试有前台服务是否还需要申请后台定位权限"));
        LogUtil.log("is foreground: "+ serviceIsRunningInForeground(this));
//        LocationUtil.init(this);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }
}
