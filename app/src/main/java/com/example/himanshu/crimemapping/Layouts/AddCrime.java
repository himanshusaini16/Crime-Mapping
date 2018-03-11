package com.example.himanshu.crimemapping.Layouts;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.example.himanshu.crimemapping.CustomAdapter;
import com.example.himanshu.crimemapping.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import dmax.dialog.SpotsDialog;

public class AddCrime extends AppCompatActivity  {
    public static final int REQUEST_CODE_AddCrime = 100;
    private Double lat, lng;
    private String crime_latitude_final, crime_longitude_final, crime_marker_final, crime_images_marker_final;
    static EditText Date, Time;
    EditText cr_des;
    String format, cmmm = ".png";
    Intent s1;
    String crime_type_final, crime_date_final, crime_time_final, crime_description_final;
    private AlertDialog progressDialog;
    Bitmap bitmap_image;
    private static final String TAG = "";
    String marker_images_URL = "http://thetechnophile.000webhostapp.com/markers-images/";
    String marker_URL = "http://thetechnophile.000webhostapp.com/markers/";

    private final String SENDER_ID = "535718128844";
    private Random random = new Random();

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


        Date = (EditText) findViewById(R.id.EditDateCrime);
        Time = (EditText) findViewById(R.id.EditTimeCrime);
        cr_des = (EditText) findViewById(R.id.EditCrime);
        Spinner mSpinner = (Spinner) findViewById(R.id.spinner_crimetype);
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


            }

            @Override
            public void onNothingSelected(AdapterView adapterView) {

            }
        });


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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addcrime) {
            authenticate();
            addCrimeToDatabase();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void firebaseUpstream() {
        FirebaseMessaging fm = FirebaseMessaging.getInstance();

        RemoteMessage message = new RemoteMessage.Builder(SENDER_ID + "@gcm.googleapis.com")
                .setMessageId(Integer.toString(random.nextInt(9999)))
                .addData("Heading", "Crime Mapping")
                .addData("crime_sentence", "A new Crime is Added to The Map.")
                .addData("Crime_Type", crime_type_final)
                .build();

        if (!message.getData().isEmpty()) {
            Log.e(TAG, "UpstreamData: " + message.getData());
        }

        if (!message.getMessageId().isEmpty()) {
            Log.e(TAG, "UpstreamMessageId: " + message.getMessageId());
        }

        fm.send(message);
    }



    private void addCrimeToDatabase() {



        firebaseUpstream();

        RequestQueue queue = Volley.newRequestQueue(AddCrime.this);

        final String finalResponse = null;

        String s_URL = "http://thetechnophile.000webhostapp.com/add_crime.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, s_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                if (response.equals("Successfully Added Crime Details to Database")) {
                    progressDialog.hide();
                    s1 = new Intent(AddCrime.this, BottomNavigationHomeActivity.class);
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


                params.put("crime_type", crime_type_final);
                params.put("crime_marker", crime_marker_final);
                params.put("crime_image_marker", crime_images_marker_final);
                params.put("crime_description", crime_description_final);
                params.put("crime_date", crime_date_final);
                params.put("crime_time", crime_time_final);
                params.put("crime_latitude", crime_latitude_final);
                params.put("crime_longitude", crime_longitude_final);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }


    public void authenticate() {
        Log.d(TAG, "Add Crime");

        if (!validate()) {
            onAuthFailed();
            return;
        }

        progressDialog = new SpotsDialog(AddCrime.this, R.style.Custom2);

        progressDialog.show();
    }

    public void onAuthFailed() {
        Toast.makeText(getBaseContext(), "Error while Adding Crime", Toast.LENGTH_LONG).show();

    }


    public boolean validate() {
        boolean valid = true;


        crime_description_final = cr_des.getText().toString();

        if (crime_description_final.length()==0 ) {
            Toast.makeText(AddCrime.this, "enter valid details", Toast.LENGTH_LONG).show();
            valid = false;
        }
        if (crime_date_final.length()==0 ) {
            Toast.makeText(AddCrime.this, "enter valid details", Toast.LENGTH_LONG).show();
            valid = false;
        }

        if (crime_time_final.length()==0) {
            Toast.makeText(AddCrime.this, "enter valid details", Toast.LENGTH_LONG).show();
            valid = false;
        }

        if (crime_description_final.length()==0 && crime_date_final.length()==0 && crime_time_final.length()==0) {
            Toast.makeText(AddCrime.this, "enter valid details", Toast.LENGTH_LONG).show();
            valid = false;
        }


        return valid;
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
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

}
