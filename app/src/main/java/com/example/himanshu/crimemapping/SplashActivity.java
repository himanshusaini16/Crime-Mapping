package com.example.himanshu.crimemapping;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ProgressBar;

public class SplashActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread tp = new Thread() {
            public void run() {
                try {
                    sleep(3000);

                } catch (Exception e) {

                } finally {

                    Intent ss = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(ss);
                    finish();
                }

            }

        };
        tp.start();
    }
}
