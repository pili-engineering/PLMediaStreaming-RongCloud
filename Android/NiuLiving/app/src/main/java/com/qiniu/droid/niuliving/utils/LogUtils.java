package com.qiniu.droid.niuliving.utils;

import android.util.Log;

public class LogUtils {
    static final String TAG = "SealLive";

    public static void d(String tag, String msg){
        Log.d(TAG+"-"+tag, msg);
    }

    public static void i(String tag, String msg){
        Log.i(TAG+"-"+tag, msg);
    }


    public static void e(String tag, String msg){
        Log.i(TAG+"-"+tag, msg);
    }

    public static void d( String msg){
        Log.d(TAG, msg);
    }

    public static void i(String msg){
        Log.i(TAG, msg);
    }


    public static void e(String msg){
        Log.i(TAG, msg);
    }
}
