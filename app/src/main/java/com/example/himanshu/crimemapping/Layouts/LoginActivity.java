package com.example.himanshu.crimemapping.Layouts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.himanshu.crimemapping.ConnectivityReceiver;
import com.example.himanshu.crimemapping.MyApplication;
import com.example.himanshu.crimemapping.R;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;


public class LoginActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {


    Button loginButton;
    EditText loginUserName, loginPassword;
    String lnemail, lnpassword;
    private AlertDialog progressDialog;
    Intent s2;
    private AwesomeValidation awesomeValidation;
    SharedPreferences userDataSharedPreferenceLogin;

    public static final String mypreferencethisislogin = "myprefLogin";
    public static final String UserDataEmail = "emailKey";
    public static final String UserDataPassword = "passwordKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userDataSharedPreferenceLogin = getSharedPreferences(mypreferencethisislogin, Context.MODE_PRIVATE);

        loginUserName = findViewById(R.id.username);
        loginPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginUser);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginRequest();
            }
        });
    }

    private void addValidationToViews() {

        awesomeValidation.addValidation(this, R.id.username, Patterns.EMAIL_ADDRESS, R.string.invalid_email);
        String regexPassword = ".{6,}";
        awesomeValidation.addValidation(this, R.id.password, regexPassword, R.string.invalid_password);

    }

    private void loginRequest() {

        addValidationToViews();
        authenticate();
        saveLoginDetails(lnemail, lnpassword);

        final RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        String URL = "http://thetechnophile.000webhostapp.com/login.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("Login")) {
                    progressDialog.hide();
                    loginUserName.setText(null);
                    loginPassword.setText(null);
                    s2 = new Intent(LoginActivity.this, BottomNavigationHomeActivity.class);
                    s2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(s2);
                    finish();

                }


            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.loginLayout), "Invalid Username/Password", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(getResources().getColor(R.color.red));
                        snackbar.show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", lnemail);
                params.put("passwd", lnpassword);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }

    public void authenticate() {

        if (awesomeValidation.validate()) {
            lnemail = loginUserName.getText().toString();
            lnpassword = loginPassword.getText().toString();

            SharedPreferences.Editor ed = userDataSharedPreferenceLogin.edit();
            ed.putString(UserDataEmail, lnemail);
            ed.putString(UserDataPassword, lnpassword);
            ed.apply();
        }
        progressDialog = new SpotsDialog(LoginActivity.this, R.style.Custom0);
        progressDialog.show();
    }


    private void saveLoginDetails(String email, String password) {
        new PrefManager(this).saveLoginDetails(email, password);
    }

    public void SingUpNow(View view) {
        Intent e = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(e);
        LoginActivity.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);

    }


    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (!isConnected) {
            message = "No Internet Connection !";
            color = Color.RED;


            Snackbar snackbar = Snackbar.make(findViewById(R.id.loginLayout), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}
