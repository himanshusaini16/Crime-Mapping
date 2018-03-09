package com.example.himanshu.crimemapping.Layouts;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.example.himanshu.crimemapping.UserData;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;


public class SignupActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = "";
    EditText signUpEmail, signUpUserName, signUpPassword, signUpRePassword;
    Button signUpButton, signupWithGoogle;
    String uemail, uname, upasswd, urepasswd;

    private static final int RC_SIGN_IN = 234;
    private static final String TAG2 = "crimemapping";
    GoogleSignInClient mGoogleSignInClient;


    private AlertDialog progressDialog;
    Intent s1;
    FirebaseAuth mAuth;

    GoogleSignInAccount account11;
    DatabaseReference mDatabase;

    SharedPreferences sp;
    SharedPreferences.Editor ep;
    public static final String PREFS_NAME = "MyPrefsFile";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("UserDetails");



        sp=getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        ep=sp.edit();
        ep.apply();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        signupWithGoogle = (Button) findViewById(R.id.startWithGoogle);
        signUpButton = (Button) findViewById(R.id.singupUser);
        signUpEmail = (EditText) findViewById(R.id.emailSignup);
        signUpUserName = (EditText) findViewById(R.id.usernameSignup);
        signUpPassword = (EditText) findViewById(R.id.passwordSignup);
        signUpRePassword = (EditText) findViewById(R.id.rePasswordSignup);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupRequest();
            }
        });

        signupWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        //getting the google signin intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    private void signupRequest() {

        authenticate();

        saveLoginDetails(uemail, upasswd);

        String id = mDatabase.push().getKey();

        UserData ud = new UserData(id, uemail, upasswd);

        //Saving the Artist
        mDatabase.child(id).setValue(ud);


        RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);

        final String finalResponse = null;

        String s_URL = "http://thetechnophile.000webhostapp.com/signup.php";


        StringRequest postRequest = new StringRequest(Request.Method.POST, s_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                if (response.equals("Successfully Signed In")) {
                    progressDialog.hide();
                    s1 = new Intent(SignupActivity.this, BottomNavigationHomeActivity.class);
                    s1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(s1);
                    finish();

                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        // error
                        Log.d("ErrorResponse", finalResponse);


                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("email", uemail);
                params.put("username", uname);
                params.put("passwd", upasswd);
                params.put("repasswd", urepasswd);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }



    private void saveLoginDetails(String email, String password) {
        new PrefManager(this).saveLoginDetails(email, password);
    }


    public void authenticate() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onAuthFailed();
            return;
        }

        progressDialog = new SpotsDialog(SignupActivity.this, R.style.Custom);

        progressDialog.show();
    }

    public void onAuthFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();

    }


    public boolean validate() {
        boolean valid = true;

        uemail = signUpEmail.getText().toString();
        uname = signUpUserName.getText().toString();
        upasswd = signUpPassword.getText().toString();
        urepasswd = signUpRePassword.getText().toString();




        if (uemail.isEmpty()) {
            signUpEmail.setError("enter a valid email address");
            valid = false;
        } else {
            signUpEmail.setError(null);
        }

        if (uname.isEmpty()) {
            signUpUserName.setError("enter a valid username");
            valid = false;
        } else {
            signUpUserName.setError(null);
        }

        if (upasswd.isEmpty() || upasswd.length() < 4 || upasswd.length() > 15) {
            signUpPassword.setError("between 4 and 10 characters");
            valid = false;
        } else {
            signUpPassword.setError(null);
        }

        if (urepasswd.isEmpty() || !urepasswd.equals(upasswd)) {
            signUpRePassword.setError("password does not match");
            valid = false;
        } else {
            signUpRePassword.setError(null);
        }

        return valid;
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (!isConnected) {
            message = "No Internet Connection !";
            color = Color.RED;


            Snackbar snackbar = Snackbar.make(findViewById(R.id.signupLayout), message, Snackbar.LENGTH_LONG);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                account11 = result.getSignInAccount();

                String aa=account11.getEmail();
                String bb=account11.getDisplayName();
                saveLoginDetails(aa,bb);



                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG2, "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG2, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();



                            s1 = new Intent(SignupActivity.this, BottomNavigationHomeActivity.class);
                            s1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(s1);



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG2, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }
}