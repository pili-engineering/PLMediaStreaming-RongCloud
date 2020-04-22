package com.qiniu.droid.niuliving;

import android.app.Application;
import android.content.Context;

import com.bugsnag.android.Bugsnag;
import com.qiniu.droid.niuliving.im.ChatroomKit;
import com.qiniu.droid.niuliving.im.DataInterface;
import com.qiniu.pili.droid.streaming.StreamingEnv;

public class StreamingApplication extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * init must be called before any other func
         */
        StreamingEnv.init(getApplicationContext());
        Bugsnag.init(this);

        DataInterface.init(this);
        ChatroomKit.init(this, DataInterface.APP_KEY);
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
}
