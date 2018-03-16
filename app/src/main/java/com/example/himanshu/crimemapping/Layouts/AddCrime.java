package com.example.himanshu.crimemapping.Layouts;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
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
import com.example.himanshu.crimemapping.CustomAdapter;
import com.example.himanshu.crimemapping.LocationAddress;
import com.example.himanshu.crimemapping.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import dmax.dialog.SpotsDialog;

public class AddCrime extends AppCompatActivity {
    private String crime_latitude_final, crime_longitude_final, crime_marker_final, crime_images_marker_final, crime_location_address_final, crime_email_final;
    EditText Date, Time;
    EditText cr_des;
    String format, cmmm = ".png";
    Intent s1;

    AlertDialog.Builder alertDialogBuilder;
    private AwesomeValidation awesomeValidation;
    String crime_type_final, crime_date_final, crime_time_final, crime_description_final, currentDate;
    private AlertDialog progressDialog;
    private static final String TAG = "";
    String marker_images_URL = "http://thetechnophile.000webhostapp.com/markers-images/";
    String marker_URL = "http://thetechnophile.000webhostapp.com/markers/";

    SharedPreferences userDataSharedPreferenceSignup, userDataSharedPreferenceLogin;
    public static final String mypreferencethisislogin = "myprefLogin";
    public static final String mypreferencethisissignup = "myprefSignup";
    public static final String UserDataEmail = "emailKey";

    private Random random = new Random();

    private java.util.Date currentLocalTime;
    private String localTime;

    int crime_images[] = {R.drawable.icon_big_robbery, R.drawable.icon_big_moto, R.drawable.icon_big_car, R.drawable.icon_big_hijacking,
            R.drawable.icon_big_robandkill, R.drawable.icon_big_drugs
            , R.drawable.icon_big_garbage, R.drawable.icon_big_bank, R.drawable.icon_big_homicide, R.drawable.icon_big_loudsound
            , R.drawable.icon_big_sex, R.drawable.icon_big_vandalism, R.drawable.icon_big_police, R.drawable.icon_big_animal,
            R.drawable.icon_big_dangerous_place};

    int crime_marker[] = {R.drawable.icon_small_assault, R.drawable.icon_small_moto, R.drawable.icon_small_car, R.drawable.icon_small_hijacking
            , R.drawable.icon_small_robandkill, R.drawable.icon_small_drugs, R.drawable.icon_small_garbage, R.drawable.icon_small_bank
            , R.drawable.icon_small_homicide, R.drawable.icon_small_loudsound, R.drawable.icon_small_sex, R.drawable.icon_small_vandalism
            , R.drawable.icon_small_police, R.drawable.icon_small_animal, R.drawable.icon_small_dangerous};

    int[] crime_heading = {R.string.crime_assalt, R.string.crime_moto, R.string.crime_car, R.string.crime_hijack, R.string.crime_robandkill
            , R.string.crime_drugs, R.string.crime_garbage, R.string.crime_bank, R.string.crime_homicide, R.string.crime_loudsound
            , R.string.crime_sex, R.string.crime_vandalism, R.string.crime_police, R.string.crime_animal, R.string.crime_danplace};
    int[] crime_description = {R.string.crime_assalt_desc, R.string.crime_moto_desc, R.string.crime_car_desc, R.string.crime_hijack_desc
            , R.string.crime_robandkill_desc, R.string.crime_drugs_desc, R.string.crime_garbage_desc, R.string.crime_bank_desc
            , R.string.crime_homicide_desc, R.string.crime_loudsound_desc, R.string.crime_sex_desc, R.string.crime_vandalism_desc
            , R.string.crime_police_desc, R.string.crime_animal_desc, R.string.crime_danplace_desc};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crime);

        Intent intent2 = getIntent();

        crime_latitude_final = intent2.getStringExtra("lat");
        crime_longitude_final = intent2.getStringExtra("lng");
        LocationAddress.getAddressFromLocation(Double.parseDouble(crime_latitude_final), Double.parseDouble(crime_longitude_final),
                getApplicationContext(), new GeocoderHandler());

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        userDataSharedPreferenceLogin = getSharedPreferences(mypreferencethisislogin, Context.MODE_PRIVATE);
        userDataSharedPreferenceSignup = getSharedPreferences(mypreferencethisissignup, Context.MODE_PRIVATE);


        if (userDataSharedPreferenceLogin.contains(UserDataEmail)) {
            crime_email_final = userDataSharedPreferenceLogin.getString(UserDataEmail, "");
        }

        if (userDataSharedPreferenceSignup.contains(UserDataEmail)) {
            crime_email_final = userDataSharedPreferenceSignup.getString(UserDataEmail, "");
        }


        Date = findViewById(R.id.EditDateCrime);
        Time = findViewById(R.id.EditTimeCrime);
        cr_des = findViewById(R.id.EditCrime);
        Spinner mSpinner = findViewById(R.id.spinner_crimetype);
        CustomAdapter mCustomAdapter = new CustomAdapter(AddCrime.this, crime_images, crime_marker, crime_heading, crime_description);
        mSpinner.setAdapter(mCustomAdapter);

        Date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    openDatePicker(v);
                }
            }
        });

        Time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    openTimePicker(v);
                }
            }
        });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int i, long l) {
                crime_marker_final = (marker_URL + (i + 1)).concat(cmmm);
                crime_images_marker_final = (marker_images_URL + (i + 1)).concat(cmmm);

                crime_type_final = getResources().getString(crime_heading[i]);

//Select Current Date of Reporting the Crime
                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy ");
                currentDate = mdformat.format(calendar.getTime());

//Select Current Time of Reporting the Crime
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                currentLocalTime = cal.getTime();
                @SuppressLint("SimpleDateFormat") DateFormat date = new SimpleDateFormat("KK:mm a");
                date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                localTime = date.format(currentLocalTime);


            }

            @Override
            public void onNothingSelected(AdapterView adapterView) {

            }
        });
    }

    private void addValidationToViews() {

        awesomeValidation.addValidation(this, R.id.EditCrime, RegexTemplate.NOT_EMPTY, R.string.invalid_crime_description);
        awesomeValidation.addValidation(this, R.id.EditDateCrime, RegexTemplate.NOT_EMPTY, R.string.invalid_date);
        awesomeValidation.addValidation(this, R.id.EditTimeCrime, RegexTemplate.NOT_EMPTY, R.string.invalid_time);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_addcrime, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_addcrime) {

            alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(AddCrime.this, android.R.style.Theme_DeviceDefault_Light));
            alertDialogBuilder.setTitle("Are you Sure?");
            alertDialogBuilder.setMessage("Once Crime is posted on map, it cannot be deleted within a period of 1 week.");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AddCrime.this.finish();

                }
            });
            alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    AddCrime.this.addCrimeToDatabase();
                }
            });
            alertDialogBuilder.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


//    private void firebaseUpstream() {
//        FirebaseMessaging fm = FirebaseMessaging.getInstance();
//
//        String SENDER_ID = "535718128844";
//        RemoteMessage message = new RemoteMessage.Builder(SENDER_ID + "@gcm.googleapis.com")
//                .setMessageId(Integer.toString(random.nextInt(9999)))
//                .addData("Heading", "Crime Mapping")
//                .addData("crime_sentence", "A new Crime is Added to The Map.")
//                .addData("Crime_Type", crime_type_final)
//                .build();
//
//        if (!message.getData().isEmpty()) {
//            Log.e(TAG, "UpstreamData: " + message.getData());
//        }
//
//        if (!message.getMessageId().isEmpty()) {
//            Log.e(TAG, "UpstreamMessageId: " + message.getMessageId());
//        }
//
//        fm.send(message);
//    }


    public void addCrimeToDatabase() {
        addValidationToViews();
        authenticate();

//        firebaseUpstream();

        RequestQueue queue = Volley.newRequestQueue(AddCrime.this);

        String s_URL = "http://thetechnophile.000webhostapp.com/add_crime.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, s_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response.equals("Added")) {
                    progressDialog.hide();
                    s1 = new Intent(AddCrime.this, BottomNavigationHomeActivity.class);
                    s1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(s1);
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
                params.put("email", crime_email_final);
                params.put("crime_type", crime_type_final);
                params.put("crime_marker", crime_marker_final);
                params.put("crime_image_marker", crime_images_marker_final);
                params.put("crime_description", crime_description_final);
                params.put("crime_date", crime_date_final);
                params.put("crime_time", crime_time_final);
                params.put("crime_latitude", crime_latitude_final);
                params.put("crime_longitude", crime_longitude_final);
                params.put("crime_location_address", crime_location_address_final);
                params.put("crime_reporting_date", currentDate);
                params.put("crime_reporting_time", localTime);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }


    public void authenticate() {
        if (awesomeValidation.validate()) {
            crime_description_final = cr_des.getText().toString();
        }
        progressDialog = new SpotsDialog(AddCrime.this, R.style.Custom2);
        progressDialog.show();
    }

    public void openDatePicker(View v) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        Date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        crime_date_final = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.setCancelable(false);
        datePickerDialog.setTitle("Select the date:");
        datePickerDialog.show();
    }

    public void openTimePicker(View v) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        if (hourOfDay == 0) {
                            hourOfDay += 12;
                            format = "AM";
                        } else if (hourOfDay == 12) {
                            format = "PM";
                        } else if (hourOfDay > 12) {
                            hourOfDay -= 12;
                            format = "PM";
                        } else {
                            format = "AM";
                        }
                        Time.setText(hourOfDay + ":" + minute + " " + format);
                        crime_time_final = hourOfDay + ":" + minute + " " + format;
                    }
                }, mHour, mMinute, false);
        timePickerDialog.setCancelable(false);
        timePickerDialog.setTitle("Select the time:");
        timePickerDialog.show();
    }


    @SuppressLint("HandlerLeak")
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            crime_location_address_final = locationAddress;
        }
    }

}
