package com.example.androidq.permission;

import android.content.Context;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.androidq.LogUtil;

/**
 * @author wzw
 * @date 2019/8/14 19:18
 */
public class LocationUtil {

    private static AMapLocationClient sClient;

    public static void init(Context context) {
        if (sClient != null && sClient.isStarted()) {
            return;
        }
        LogUtil.log("start location");
        sClient = new AMapLocationClient(context);
        sClient.setLocationOption(getDefaultOption());
        sClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation location) {
                int errorCode = location.getErrorCode();

                if (AMapLocation.LOCATION_SUCCESS == errorCode) {
                    Toast.makeText(context, "location success", Toast.LENGTH_SHORT).show();
                    LogUtil.log("location success");
                } else {
                    LogUtil.log("errorCode =" + errorCode + " errorInfo : " + location.getErrorInfo());
                }
            }
        });

        sClient.startLocation();
    }

    public static AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();

        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(3000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用

        //        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        //        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true

        //        setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选，
        // 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP

        return mOption;
    }
}
