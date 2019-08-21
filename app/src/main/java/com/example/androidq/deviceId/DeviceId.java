package com.example.androidq.deviceId;

import android.Manifest;
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

    private static String makeDeviceId(Context context) {
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
        String androidId = Settings.System.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public static String makeDeviceId() {
        try {
            String s = makeDeviceInfo();
            LogUtil.log("makeDeviceInfo: " + s);
            return UUID.nameUUIDFromBytes(s.getBytes("utf8")).toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String makeDeviceInfo() {
        //Build.getSerial()也不再可用
//        String serial = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 ? Build.getSerial() : Build.SERIAL;
        String serial = Build.SERIAL;
        StringBuilder builder = new StringBuilder()
                .append(Build.BOARD).append("\n")//HMA
                .append(Build.BRAND).append("\n")//HUAWEI
                .append(Build.CPU_ABI).append("\n")//arm64-v8a
                .append(Build.DEVICE).append("\n")//HWHMA
                .append(Build.DISPLAY).append("\n")//HMA-AL00 9.0.0.200(C00E200R1P21)
                .append(Build.HOST).append("\n")//szvjk004cna
                .append(Build.ID).append("\n")//HUAWEIHMA-AL00
                .append(Build.MANUFACTURER).append("\n")//HUAWEI
                .append(Build.MODEL).append("\n")//HMA-AL00
                .append(Build.PRODUCT).append("\n")//HMA-AL00
                .append(Build.TAGS).append("\n")//release-keys
                .append(Build.TYPE).append("\n")//user
                .append(Build.USER).append("\n");//test
//                .append(serial);//66J0218B19000977
        return builder.toString();
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
