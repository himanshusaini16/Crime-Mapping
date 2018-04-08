package com.example.himanshu.crimemapping.Layouts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.himanshu.crimemapping.ConnectivityReceiver;
import com.example.himanshu.crimemapping.MyApplication;
import com.example.himanshu.crimemapping.R;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;


public class EditUserProfile extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    public static final String mypreferencethisislogin = "myprefLogin";
    public static final String mypreferencethisissignup = "myprefSignup";
    public static final String UserDataEmail = "emailKey";
    public static final String UserDataName = "nameKey";
    public static final String UserDataPassword = "passwordKey";
    EditText editName, editEmail, editPass;
    Button updateinfo;
    SharedPreferences userDataSharedPreferenceSignup, userDataSharedPreferenceLogin;
    String primaryName, finalName, finalPassword, primaryEmail;
    private AlertDialog progressDialog;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

//        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        editName = findViewById(R.id.EditTextName);
        editEmail = findViewById(R.id.EditTextEmail);
        editEmail.setEnabled(false);
        editPass = findViewById(R.id.EditTextPassword);
        updateinfo = findViewById(R.id.info_update);

        userDataSharedPreferenceSignup = getSharedPreferences(mypreferencethisissignup, Context.MODE_PRIVATE);
        userDataSharedPreferenceLogin = getSharedPreferences(mypreferencethisislogin, Context.MODE_PRIVATE);

        if (userDataSharedPreferenceSignup.contains(UserDataName)) {
            editName.setText(userDataSharedPreferenceSignup.getString(UserDataName, ""));
            primaryName = userDataSharedPreferenceSignup.getString(UserDataName, "");

        }

        if (userDataSharedPreferenceLogin.contains(UserDataName)) {
            editName.setText(userDataSharedPreferenceLogin.getString(UserDataName, ""));
            primaryName = userDataSharedPreferenceLogin.getString(UserDataName, "");

        }

        if (userDataSharedPreferenceLogin.contains(UserDataEmail)) {
            editEmail.setText(userDataSharedPreferenceLogin.getString(UserDataEmail, ""));
            primaryEmail = userDataSharedPreferenceLogin.getString(UserDataEmail, "");

        }

        if (userDataSharedPreferenceSignup.contains(UserDataEmail)) {
            editEmail.setText(userDataSharedPreferenceSignup.getString(UserDataEmail, ""));
            primaryEmail = userDataSharedPreferenceSignup.getString(UserDataEmail, "");

        }

        updateinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateUserProfile();

            }
        });

    }

    private void updateUserProfile() {


        authenticate();

        RequestQueue queue = Volley.newRequestQueue(EditUserProfile.this);
        String s_URL = "http://thetechnophile.000webhostapp.com/update_profile.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, s_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response.equals("Successfully Updated")) {
                    progressDialog.hide();
                    Toast.makeText(EditUserProfile.this, "Profile Updated Successfully", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("forcheck", primaryEmail);
                params.put("username", finalName);
                params.put("passwd", finalPassword);
                params.put("repasswd", finalPassword);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editprofile, menu);
        return true;
    }


    public void authenticate() {

//        if (awesomeValidation.validate())
            finalName = editName.getText().toString();
            finalPassword = editPass.getText().toString();
        if (finalPassword.isEmpty()) {
            finalPassword = userDataSharedPreferenceSignup.getString(UserDataPassword, "");
        }

            SharedPreferences.Editor ed = userDataSharedPreferenceSignup.edit();
            ed.putString(UserDataName, finalName);
        ed.putString(UserDataPassword, finalPassword);
            ed.apply();

//        }
        progressDialog = new SpotsDialog(EditUserProfile.this, R.style.Custom4);
        progressDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.closeEditProfile) {
            EditUserProfile.this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (!isConnected) {
            message = "No Internet Connection !";
            color = Color.RED;
            Snackbar snackbar = Snackbar.make(findViewById(R.id.EditingProfile), message, Snackbar.LENGTH_LONG);
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



