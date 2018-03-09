package com.example.himanshu.crimemapping;


import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        Thread tp = new Thread() {
            public void run() {
                try {
                    sleep(3000);

                } catch (Exception ignored) {

                } finally {



                    if (!new PrefManager(SplashActivity.this).isUserLogedOut()) {
                        Intent ss=new Intent(SplashActivity.this,BottomNavigationHomeActivity.class);
                        ss.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(ss);
                        finish();
                    }
                    else {
                        Intent ss=new Intent(SplashActivity.this,LoginActivity.class);
                        ss.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(ss);
                        finish();
                    }

                }

            }

        };
        tp.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);


        MyApplication.getInstance().setConnectivityListener(this);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (!isConnected) {
            message = "No Internet Connection !";
            color = Color.RED;

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.splashlayout), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

}
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        showSnack(isConnected);
    }
}
