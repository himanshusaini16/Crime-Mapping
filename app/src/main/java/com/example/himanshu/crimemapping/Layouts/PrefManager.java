package com.example.himanshu.crimemapping.Layouts;

import android.content.Context;
import android.content.SharedPreferences;

class PrefManager {

    private Context context;

    PrefManager(Context context) {
        this.context = context;
    }

    void saveLoginDetails(String email, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Email", email);
        editor.putString("Password", password);
        editor.apply();
    }


    boolean isUserLogedOut() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        boolean isEmailEmpty = sharedPreferences.getString("Email", "").isEmpty();
        boolean isPasswordEmpty = sharedPreferences.getString("Password", "").isEmpty();
        return isEmailEmpty || isPasswordEmpty;
    }

    void saveImagePreference(String key, String path) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PictureDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("KeyHai", key);
        editor.putString("Path", path);
        editor.apply();
    }


    boolean isUserOutImageHatao() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PictureDetails", Context.MODE_PRIVATE);
        boolean isKeyEmpty = sharedPreferences.getString("KeyHai", "").isEmpty();
        boolean isPathEmpty = sharedPreferences.getString("Path", "").isEmpty();
        return isKeyEmpty || isPathEmpty;
    }
}