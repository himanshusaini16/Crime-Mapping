package com.example.himanshu.crimemapping.Layouts;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.himanshu.crimemapping.ConnectivityReceiver;
import com.example.himanshu.crimemapping.MyApplication;
import com.example.himanshu.crimemapping.R;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {


    private static final String TAG = "";
    Button loginButton;
    EditText loginUserName, loginPassword;
    String lnemail, lnpassword;
    private AlertDialog progressDialog;
    Intent s2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUserName = (EditText) findViewById(R.id.username);
        loginPassword = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginUser);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginRequest();
            }
        });
    }


    private void loginRequest() {

        authenticate();

        saveLoginDetails(lnemail, lnpassword);

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        final String finalResponse = null;
        String URL = "http://thetechnophile.000webhostapp.com/login.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("Login")) {
                    progressDialog.hide();
                    s2 = new Intent(LoginActivity.this, BottomNavigationHomeActivity.class);
                    s2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(s2);
                    finish();

                }


            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        progressDialog.hide();
                        Log.d("ErrorResponse", finalResponse);

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
        Log.d(TAG, "Login");

        if (!validate()) {
            onAuthFailed();
            return;
        }
        progressDialog = new SpotsDialog(LoginActivity.this, R.style.Custom0);
        progressDialog.show();
    }

    public void onAuthFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

    }

    public boolean validate() {
        boolean valid = true;


        lnemail = loginUserName.getText().toString();
        lnpassword = loginPassword.getText().toString();




        if (loginUserName.getText() == null && loginPassword.getText() == null) {
            loginUserName.setError("enter a valid email address");
            loginPassword.setError("enter a valid password");
            valid = false;
        }


        if (lnemail.isEmpty() && !lnemail.contains("@")) {
            loginUserName.setError("enter a valid email address");
            valid = false;
        } else {
            loginUserName.setError(null);
        }


        if (lnpassword.isEmpty()) {
            loginPassword.setError("enter a valid password");
            valid = false;
        } else {
            loginPassword.setError(null);
        }

        return valid;
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
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
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
