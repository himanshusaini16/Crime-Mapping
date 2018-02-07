package com.example.himanshu.crimemapping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void SingUpNow(View view) {
        Intent e=new Intent(LoginActivity.this,SignupActivity.class);
        startActivity(e);
        LoginActivity.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);

    }
}
