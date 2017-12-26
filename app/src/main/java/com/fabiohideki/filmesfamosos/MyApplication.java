package com.fabiohideki.filmesfamosos;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by fabio.lagoa on 26/12/2017.
 */

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
