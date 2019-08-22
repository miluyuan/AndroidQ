package com.example.androidq.deviceId;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.example.androidq.LogUtil;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.UUID;

import androidx.core.app.ActivityCompat;

/**
 * @author wzw
 * @date 2019/8/19 10:25
 */
public class DeviceId {
    /**
     * AndroidId
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        // 这两个ID相同
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        //HW mate20: 496836e3a48d2d9d
        //Google pixel: f06c5549707adb8b
        LogUtil.log("Android_id：" + androidId);
        LogUtil.log("id：" + id);

        return androidId;
    }

    public static String makeDeviceId(Context context) {
        String deviceInfo = new StringBuilder()
                .append(Build.BOARD).append("#")
                .append(Build.BRAND).append("#")
                .append(Build.CPU_ABI).append("#")
                .append(Build.DEVICE).append("#")
                .append(Build.DISPLAY).append("#")
                .append(Build.HOST).append("#")
                .append(Build.ID).append("#")
                .append(Build.MANUFACTURER).append("#")
                .append(Build.MODEL).append("#")
                .append(Build.PRODUCT).append("#")
                .append(Build.TAGS).append("#")
                .append(Build.TYPE).append("#")
                .append(Build.USER).append("#")
                .toString();
        try {
            return UUID.nameUUIDFromBytes(deviceInfo.getBytes("utf8")).toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Settings.System.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    /**
     * 1. target>=AndroidQ时在AndroidQ系统上抛出异常
     * SecurityException: getDeviceId: The user 10196 does not meet the requirements to access device identifiers.
     * <p>
     * 2. target<Q时在Q系统上返回null
     * 3. 在小于Q的系统上返回正常
     */
    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.M)
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        //866976045561713
        LogUtil.log(tm.getDeviceId());
        return tm.getDeviceId();
    }

    /**
     * 获取无卡模式设备id
     *
     * @param context 上下文
     */
    public static String getTypeNoneDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            LogUtil.log("Have not permission to obtain DeviceId");
            return "";
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return telephonyManager.getDeviceId(TelephonyManager.PHONE_TYPE_NONE);
        } else {
            return "";
        }
    }

    /**
     * 获取GSM模式设备id
     *
     * @param context 上下文
     */
    public static String getTypeGSMDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            LogUtil.log("Have not permission to obtain DeviceId");
            return "";
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return telephonyManager.getDeviceId(TelephonyManager.PHONE_TYPE_GSM);
        } else {
            return "";
        }
    }

    /**
     * 获取CDMA模式设备id
     *
     * @param context 上下文
     */
    public static String getTypeCDMADeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            LogUtil.log("Have not permission to obtain DeviceId");
            return "";
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return telephonyManager.getDeviceId(TelephonyManager.PHONE_TYPE_CDMA);
        } else {
            return "";
        }
    }

    /**
     * 获取SIP模式设备id
     *
     * @param context 上下文
     */
    public static String getTypeSIPDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            LogUtil.log("Have not permission to obtain DeviceId");
            return "";
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return telephonyManager.getDeviceId(TelephonyManager.PHONE_TYPE_SIP);
        } else {
            return "";
        }
    }

    /**
     * 获取设备IMSI
     *
     * @param context 上下文
     */
    public static String getIMSI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            LogUtil.log("Have not permission to obtain IMSI");
            return "000000";
        }
        return Objects.requireNonNull(telephonyManager).getSubscriberId();
    }

}
