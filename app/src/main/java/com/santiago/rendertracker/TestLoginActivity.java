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

        try {
            ((ViewGroup) findViewById(android.R.id.content)).addView((View) TestView.class.getClassLoader().loadClass("com.santiago.rendertracker.TestView").getConstructor(Context.class).newInstance(this));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}

