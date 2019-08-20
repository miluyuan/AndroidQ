package com.example.androidq;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author wzw
 * @date 2019/8/19 15:20
 */
public class SPUtil {
    private static final String NAME = "AndroidQ";

    public static String getValue(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void setValue(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }
}
