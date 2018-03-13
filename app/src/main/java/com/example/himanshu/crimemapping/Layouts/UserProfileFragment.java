package com.example.himanshu.crimemapping.Layouts;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.himanshu.crimemapping.ConnectivityReceiver;
import com.example.himanshu.crimemapping.MyApplication;
import com.example.himanshu.crimemapping.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class UserProfileFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {

    AdView mAdView;
    View v;
    FirebaseAuth mAuth;
    private static int RESULT_LOAD_IMAGE = 1;
    public static final String MyPREFERENCES = "MyPre";//file name
    public static final String key = "nameKey";
    SharedPreferences sharedpreferences;
    CircularImageView changedp;
    Bitmap btmap;

    private Context mContext;
    private PopupWindow popupWindow;
    RelativeLayout mRelativeLayout;

    Button changeDPicture, removeDPicture;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_user_profile, container, false);

        mContext = getContext();

        changedp = v.findViewById(R.id.UserProfileImage);

        mRelativeLayout = v.findViewById(R.id.userProfileFragment);

        sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(key)) {
            String u = sharedpreferences.getString(key, "");
            btmap = decodeBase64(u);
            changedp.setImageBitmap(btmap);
        }

        mAuth = FirebaseAuth.getInstance();
        mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        changedp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.popup_window_editimage, null);
                popupWindow = new PopupWindow(customView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.setIgnoreCheekPress();
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(false);
                popupWindow.update();
                popupWindow.setAnimationStyle(R.style.popup_window_animation_phone);
                popupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);

                changeDPicture = popupWindow.getContentView().findViewById(R.id.changeDp);
                removeDPicture = popupWindow.getContentView().findViewById(R.id.removeDp);


                changeDPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        popupWindow.dismiss();
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
                    }
                });
                removeDPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.with(getActivity()).load(R.mipmap.ic_human_round).noPlaceholder().centerCrop().fit().into(changedp);
                        popupWindow.dismiss();
                        btmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_human_round);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(key, encodeTobase64(btmap));
                        editor.apply();
                    }
                });
            }
        });

        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       
        switch (item.getItemId()) {
            case R.id.user_logout:
                saveLoginDetails(null, null);
                sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                sharedpreferences.edit().remove(key).apply();
                Intent ss = new Intent(getActivity(), LoginActivity.class);
                ss.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ss);
                break;


            case R.id.userProfileEditButton:
                Toast.makeText(getActivity(), "Edit", Toast.LENGTH_SHORT).show();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void saveLoginDetails(String email, String password) {
        new PrefManager(getActivity()).saveLoginDetails(email, password);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (!isConnected) {
            message = "No Internet Connection !";
            color = Color.RED;


            Snackbar snackbar = Snackbar.make(v.findViewById(R.id.userProfileFragment), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        getActivity().registerReceiver(connectivityReceiver, intentFilter);
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imagehere = data.getData();
            try {
                btmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imagehere);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Picasso.with(getActivity()).load(imagehere).noPlaceholder().centerCrop().fit().into(changedp);
            Toast.makeText(getActivity(), "Profile Picture Changed", Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(key, encodeTobase64(btmap));
            editor.apply();
        }

    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;

    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }


}


