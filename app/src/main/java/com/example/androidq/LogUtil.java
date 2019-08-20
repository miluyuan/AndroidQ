package com.example.androidq;

import android.util.Log;

/**
 * @author wzw
 * @date 2019/8/13 15:36
 */
public class LogUtil {
    private static final String TAG = "AndroidQ";

    public static void log(Object o) {
        Log.e(TAG, o == null ? "null" : o.toString());
    }
}
