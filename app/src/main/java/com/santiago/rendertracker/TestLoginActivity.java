package com.santiago.rendertracker;

import android.app.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;

/**
 * A login screen that offers login via email/password.
 */
public class TestLoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_login);

            TestView testView = new TestView(this);
            testView.draw(null);
    }

}

