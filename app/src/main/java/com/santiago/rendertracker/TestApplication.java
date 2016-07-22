package com.santiago.rendertracker;

import android.app.Application;

import com.santiago.RenderTrackerInjector;

/**
 * Created by santi on 21/07/16.
 */
public class TestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RenderTrackerInjector.init(getApplicationContext(), TestView.class);
    }
}
