package com.xb.bubble.bubblelayout;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by admin on 2015/5/25.
 */
public class ExampleApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        enabledStrictMode();
        LeakCanary.install(this);
    }

    private void enabledStrictMode() {
        if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //
                    .detectAll() //
                    .penaltyLog() //
                    .penaltyDeath() //
                    .build());
        }
    }
}
