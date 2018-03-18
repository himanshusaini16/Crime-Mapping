package com.example.himanshu.crimemapping.Layouts;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.himanshu.crimemapping.AppController;
import com.example.himanshu.crimemapping.ConnectivityReceiver;
import com.example.himanshu.crimemapping.Crime_UserRelated;
import com.example.himanshu.crimemapping.Custom_UserListAdapter;
import com.example.himanshu.crimemapping.MyApplication;
import com.example.himanshu.crimemapping.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class UserProfileFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {

    View v;
    FirebaseAuth mAuth;
    private static int RESULT_LOAD_IMAGE = 1;
    public static final String MyPREFERENCES = "MyPre";
    public static final String key = "nameKey";
    SharedPreferences sharedpreferences;
    CircularImageView changedp;
    Bitmap btmap;
    String userEmail;
    String deleteRow;
    ListView listView;

    private AdView mAdView;

    java.util.Date date1, date2;

    private static final String TAG = UserProfileFragment.class.getSimpleName();
    private static final String url = "http://thetechnophile.000webhostapp.com/load_crime_UserRelated.json";
    private List<Crime_UserRelated> UserCrimeList = new ArrayList<>();
    private Custom_UserListAdapter adapter;

    private ProgressBar progressBar;
    TextView upName;
    private Context mContext;
    private PopupWindow popupWindow;
    RelativeLayout mRelativeLayout;
    String currentDateForCheck, dateFromServer;

    SharedPreferences userDataSharedPreferenceSignup, userDataSharedPreferenceLogin;


    public static final String mypreferencethisislogin = "myprefLogin";
    public static final String mypreferencethisissignup = "myprefSignup";
    public static final String UserDataEmail = "emailKey";
    public static final String UserDataName = "nameKey";

    Button changeDPicture, removeDPicture;
    AlertDialog.Builder alertDialogBuilder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_user_profile, container, false);

        MobileAds.initialize(getActivity(), "ca-app-pub-4510895115386086/9905404014");

        progressBar = v.findViewById(R.id.progressBar);
        loadUserCrimeList();

        upName = v.findViewById(R.id.UserProfileName);


        userDataSharedPreferenceLogin = getContext().getSharedPreferences(mypreferencethisislogin, Context.MODE_PRIVATE);
        userDataSharedPreferenceSignup = getContext().getSharedPreferences(mypreferencethisissignup, Context.MODE_PRIVATE);


        if (userDataSharedPreferenceLogin.contains(UserDataEmail)) {
//            upName.setText(userDataSharedPreferenceLogin.getString(UserDataEmail, ""));
            userEmail = userDataSharedPreferenceLogin.getString(UserDataEmail, "");
        }

        if (userDataSharedPreferenceSignup.contains(UserDataName)) {
            upName.setText(userDataSharedPreferenceSignup.getString(UserDataName, ""));
        }
        if (userDataSharedPreferenceSignup.contains(UserDataEmail)) {
            userEmail = userDataSharedPreferenceSignup.getString(UserDataEmail, "");
        }

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy ");
        currentDateForCheck = mdformat.format(calendar.getTime());

        listView = v.findViewById(R.id.Userprofile_crimeList);
        adapter = new Custom_UserListAdapter(getActivity(), UserCrimeList);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Crime_UserRelated ss = (Crime_UserRelated) listView.getItemAtPosition(position);
                    deleteRow = ss.getDesUser();
                    dateFromServer = ss.getReportedDateUser();
                    UserCrimeList.remove(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
        registerForContextMenu(listView);


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


        JsonArrayRequest listReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        progressBar.setVisibility(View.INVISIBLE);
                        for (int i = response.length(); i >= 0; i--) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Crime_UserRelated ucrime = new Crime_UserRelated();
                                ucrime.setTitleUser(obj.getString("crime_type"));
                                ucrime.setThumbnailUrlUser(obj.getString("crime_image_marker"));
                                ucrime.setDesUser(obj.getString("crime_description"));
                                ucrime.setAddressUser(obj.getString("crime_location_address"));
                                ucrime.setReportedDateUser(obj.getString("crime_reporting_date"));

                                UserCrimeList.add(ucrime);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(listReq);
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

    private void loadUserCrimeList() {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String s_URL = "http://thetechnophile.000webhostapp.com/load_crime_UserRelated.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, s_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", userEmail);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Delete") {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            try {
                date1 = formatter.parse(dateFromServer);
                date2 = formatter.parse(currentDateForCheck);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long difference = Math.abs(date2.getTime() - date1.getTime());
            long differenceDates = difference / (24 * 60 * 60 * 1000);
            if (differenceDates > 7) {
                functionToDelete();
            } else {
                alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_DeviceDefault_Light));
                alertDialogBuilder.setTitle("You Can't Delete It");
                alertDialogBuilder.setMessage("Crimes can only be deleted after 7 days of posting.");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setIcon(R.drawable.ic_error_alert);
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialogBuilder.show();
            }


        }
        return super.onContextItemSelected(item);
    }


    private void functionToDelete() {

        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String s_URL = "http://thetechnophile.000webhostapp.com/delete_crimeUser.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, s_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("crime_description", deleteRow);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

        shuffle();
    }

    private void shuffle() {
        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, v.getId(), 1, "Delete");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.user_logout:
                saveLoginDetails(null, null);
                sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                sharedpreferences.edit().remove(key).apply();
                userDataSharedPreferenceSignup.edit().remove(UserDataName).apply();
                userDataSharedPreferenceSignup.edit().remove(UserDataEmail).apply();
                userDataSharedPreferenceLogin.edit().remove(UserDataEmail).apply();
                Intent ss = new Intent(getActivity(), LoginActivity.class);
                ss.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(ss);
                break;


            case R.id.userProfileEditButton:
                Intent op = new Intent(getActivity(), EditUserProfile.class);
                startActivity(op);
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
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


