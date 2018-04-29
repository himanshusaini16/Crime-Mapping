package com.example.himanshu.crimemapping.Layouts;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import dmax.dialog.SpotsDialog;

public class AddCrime extends AppCompatActivity {
    public static final String mypreferencethisislogin = "myprefLogin";
    public static final String mypreferencethisissignup = "myprefSignup";
    public static final String UserDataEmail = "emailKey";
    public static final String AddCrimesharedpref = "myprefAddCrime";
    public static final String ListLatAdded = "nameLatAdded";
    public static final String ListLngAdded = "nameLngAdded";
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private static final String TAG = "";
    private static final int RESULT_SELECT_IMAGE = 1;
    private static final int REQUEST_CODE = 123;
    public String SERVER = "http://thetechnophile.000webhostapp.com/saveImage.php";
    EditText Date, Time;
    EditText cr_des;
    String format, cmmm = ".png";
    Intent s1;
    AlertDialog.Builder alertDialogBuilder;
    String crime_type_final, crime_date_final, crime_time_final, crime_description_final, currentDate, img_by_user;
    String marker_images_URL = "http://thetechnophile.000webhostapp.com/markers-images/";
    String marker_URL = "http://thetechnophile.000webhostapp.com/markers/";
    SharedPreferences userDataSharedPreferenceSignup, userDataSharedPreferenceLogin;
    SharedPreferences AddCrimesp;
    String AddCrimeLat, AddCrimeLng;
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
    Bitmap bitmap = null;
    String filename, ba1, timestamp, im_name;
    PopupWindow mPopupWindow;
    RelativeLayout rl1;
    LayoutInflater layoutInflater;
    View customView;
    Button retry, done;
    Uri fpath;
    ImageView selectedImageV;
    String selectedImagePath;
    private String crime_latitude_final, crime_longitude_final, crime_marker_final, crime_images_marker_final, crime_location_address_final, crime_email_final;
    private InterstitialAd mInterstitialAd;
    private AwesomeValidation awesomeValidation;
    private AlertDialog progressDialog;
    private Random random = new Random();
    private java.util.Date currentLocalTime;
    private String localTime, tag_uemail;
    private Intent pictureActionIntent = null;
    private ArrayList<String> mResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crime);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        rl1 = findViewById(R.id.view_addcrimeactivity);
        layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        customView = LayoutInflater.from(this).inflate(R.layout.image_popup_addcrime, null);

        retry = customView.findViewById(R.id.retry_image);
        done = customView.findViewById(R.id.done_image);
        selectedImageV = customView.findViewById(R.id.im_popup_image);

        AddCrimesp = getSharedPreferences(AddCrimesharedpref, Context.MODE_PRIVATE);

        Intent intent2 = getIntent();

        crime_latitude_final = intent2.getStringExtra("lat");
        crime_longitude_final = intent2.getStringExtra("lng");

        AddCrimeLat = intent2.getStringExtra("lat");
        AddCrimeLng = intent2.getStringExtra("lng");

        LocationAddress.getAddressFromLocation(Double.parseDouble(crime_latitude_final), Double.parseDouble(crime_longitude_final),
                getApplicationContext(), new GeocoderHandler());

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        MobileAds.initialize(this, "ca-app-pub-4510895115386086~4521143649");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4510895115386086/3390244359");

        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);


        userDataSharedPreferenceLogin = getSharedPreferences(mypreferencethisislogin, Context.MODE_PRIVATE);
        userDataSharedPreferenceSignup = getSharedPreferences(mypreferencethisissignup, Context.MODE_PRIVATE);


        if (userDataSharedPreferenceLogin.contains(UserDataEmail)) {
            crime_email_final = userDataSharedPreferenceLogin.getString(UserDataEmail, "");
            tag_uemail = userDataSharedPreferenceLogin.getString(UserDataEmail, "");
        }

        if (userDataSharedPreferenceSignup.contains(UserDataEmail)) {
            crime_email_final = userDataSharedPreferenceSignup.getString(UserDataEmail, "");
            tag_uemail = userDataSharedPreferenceSignup.getString(UserDataEmail, "");
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

        if (id == R.id.selectImage) {
            imageSelector();
        }

        if (id == R.id.action_addcrime) {

            alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(AddCrime.this, android.R.style.Theme_DeviceDefault_Light));
            alertDialogBuilder.setTitle("Are you Sure?");
            alertDialogBuilder.setMessage("Once crime is posted on the map, it cannot be deleted within period of 7 days.");
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
                    SharedPreferences.Editor ed = AddCrimesp.edit();
                    ed.putString(ListLatAdded, AddCrimeLat);
                    ed.putString(ListLngAdded, AddCrimeLng);
                    ed.apply();

                    AddCrime.this.addCrimeToDatabase();
                }
            });
            alertDialogBuilder.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void addCrimeToDatabase() {
        addValidationToViews();
        authenticate();


        if (bitmap == null) {
            img_by_user = "http://thetechnophile.000webhostapp.com/images_uploaded/noimage.jpg";
        } else {
            im_name = "IMG_" + timestamp;
            new Upload(bitmap, im_name).execute();
            img_by_user = "http://thetechnophile.000webhostapp.com/images_uploaded/IMG_" + timestamp + ".jpg";
        }

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

                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }

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
                params.put("img_by_user", img_by_user);

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

        Calendar calendar = Calendar.getInstance();
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
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

                    @SuppressLint("DefaultLocale")
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
                        Time.setText(String.format("%02d:%02d ", hourOfDay, minute) + format);
                        crime_time_final = (String.format("%02d:%02d ", hourOfDay, minute) + format);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.setCancelable(false);
        timePickerDialog.setTitle("Select the time:");
        timePickerDialog.show();
    }

    public void imageSelector() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        myAlertDialog.setTitle("Upload Picture Option:");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pictureActionIntent = null;

                        pictureActionIntent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(
                                pictureActionIntent, GALLERY_PICTURE);


                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment
                                .getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(f));

                        startActivityForResult(intent,
                                CAMERA_REQUEST);
                    }
                });
        myAlertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        bitmap = null;
        selectedImagePath = null;

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            Long tsLong = System.currentTimeMillis() / 1000;
            timestamp = tsLong.toString();

            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    Toast.makeText(getBaseContext(), "Image Selected", Toast.LENGTH_SHORT).show();
                    break;
                }
            }

            if (!f.exists()) {
                Toast.makeText(getBaseContext(), "Error while capturing image", Toast.LENGTH_LONG).show();
                return;
            }

            try {

                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
                selectedImageV.setImageBitmap(bitmap);
                popupWindowMethod();
                int rotate = 0;
                try {
                    ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate = 90;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {
                Toast.makeText(getBaseContext(), "Image Selected", Toast.LENGTH_SHORT).show();

                Long tsLong = System.currentTimeMillis() / 1000;
                timestamp = tsLong.toString();

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedImagePath = c.getString(columnIndex);
                bitmap = BitmapFactory.decodeFile(selectedImagePath);
                bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
                selectedImageV.setImageBitmap(bitmap);
                popupWindowMethod();

                c.close();


            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public void popupWindowMethod() {
        mPopupWindow = new PopupWindow(customView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setIgnoreCheekPress();
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.update();
        mPopupWindow.setAnimationStyle(R.style.popup_window_animation_phone);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                imageSelector();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.showAtLocation(rl1, Gravity.CENTER, 0, -50);
        if (mPopupWindow.isShowing()) {
            rl1.setVisibility(View.VISIBLE);
        }

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

    //async task to upload image
    private class Upload extends AsyncTask<Void, Void, String> {
        private Bitmap image;
        private String name;

        public Upload(Bitmap image, String name) {
            this.image = image;
            this.name = name;
        }

        @Override
        protected String doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //compress the image to jpg format
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            /*
            * encode image to base64 so that it can be picked by saveImage.php file
            * */
            String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            //generate hashMap to store encodedImage and the name
            HashMap<String, String> detail = new HashMap<>();
            detail.put("name", name);
            detail.put("image", encodeImage);

            try {
                //convert this HashMap to encodedUrl to send to php file
                String dataToSend = hashMapToUrl(detail);
                //make a Http request and send data to saveImage.php file

                //return the response
                return RequestA.post(SERVER, dataToSend);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "ERROR  " + e);
                return null;
            }
        }


        @Override
        protected void onPostExecute(String s) {
            //show image uploaded
            Toast.makeText(getApplicationContext(), "Crime Added Successfully", Toast.LENGTH_SHORT).show();
        }
    }


}
