package com.example.himanshu.crimemapping.Layouts;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.himanshu.crimemapping.AppController;
import com.example.himanshu.crimemapping.ConnectivityReceiver;
import com.example.himanshu.crimemapping.Crime;
import com.example.himanshu.crimemapping.InfoWindowData;
import com.example.himanshu.crimemapping.MyApplication;
import com.example.himanshu.crimemapping.R;
import com.example.himanshu.crimemapping.heatmaps.Gradient;
import com.example.himanshu.crimemapping.heatmaps.HeatmapTileProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.example.himanshu.crimemapping.Layouts.SignupActivity.PREFS_NAME;


public class CrimeMapFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMyLocationButtonClickListener, ConnectivityReceiver.ConnectivityReceiverListener, GoogleMap.OnCameraChangeListener, GoogleMap.OnMapLoadedCallback {

    public static final String crimelistsharedpref = "myprefCrimeList";
    public static final String ListLat = "nameLat";
    public static final String ListLng = "nameLng";
    public static final String AddCrimesharedpref = "myprefAddCrime";
    public static final String ListLatAdded = "nameLatAdded";
    public static final String ListLngAdded = "nameLngAdded";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private static final String url = "http://thetechnophile.000webhostapp.com/load_crime.json";
    View v;
    MapView mMapView;
    GoogleMap mMap;
    boolean GpsStatus = true;
    LocationRequest mLocationRequest;
    Marker mCurrLocationMarker;
    LatLng latLngLoc, mClickPos;
    String place_searched, LatfromList, LngfromList, aal, bbl;
    LatLng center;
    AlertDialog.Builder alertDialogBuilder;
    SharedPreferences crimeListsp;
    SharedPreferences AddCrimesp;
    ProgressBar pb;
    ImagePopup imagePopup;
    String ab, picurl;
    LatLng aacha;
    ArrayList<LatLng> list = new ArrayList<>();
    boolean kismat = false;

    HeatmapTileProvider mProvider;
    TileOverlay mOverlay;
    Marker m;
    float zoomlvl;
    String id;
    private GoogleApiClient googleApiClient;
    private List<Crime> crimeList = new ArrayList<Crime>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_crime_map, container, false);

        pb = v.findViewById(R.id.progressBarMapLoading);

        pb.setVisibility(View.VISIBLE);

        imagePopup = new ImagePopup(getActivity());

        crimeListsp = getContext().getSharedPreferences(crimelistsharedpref, Context.MODE_PRIVATE);
        AddCrimesp = getContext().getSharedPreferences(AddCrimesharedpref, Context.MODE_PRIVATE);

        String link = "http://thetechnophile.000webhostapp.com/load_crime.php";
        new updateData().execute(link);


        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        if (!isGooglePlayServicesAvailable()) {
            getActivity().getFragmentManager().popBackStack();
        }
        mMapView = v.findViewById(R.id.mapView);
        if (mMapView != null) {

            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }


    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_item, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                place_searched = query;
                onMapSearch();
                if (searchMenuItem != null) {
                    searchMenuItem.collapseActionView();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                CrimeMapFragment.this.setItemsVisibility(menu, searchMenuItem, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                CrimeMapFragment.this.setItemsVisibility(menu, searchMenuItem, true);
                return true;
            }
        });
    }

    public void onMapSearch() {

        List<Address> addressList = null;

        if (place_searched != null || !place_searched.equals("")) {
            Geocoder geocoder = new Geocoder(getActivity());
            try {
                addressList = geocoder.getFromLocationName(place_searched, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Searched Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15), 2000, null);

        }
    }

    private void setItemsVisibility(final Menu menu, final MenuItem exception, final boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception)
                item.setVisible(visible);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {

            case R.id.action_refresh:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
                break;

            case R.id.report_incident:
                Toast.makeText(getActivity(), R.string.pressss, Toast.LENGTH_LONG).show();
                break;

            case R.id.about_app:
                viewAboutApp();
                break;

            case R.id.about_us:
                viewAboutUs();
                break;

            case R.id.Share:
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBodyText = "Crime Mapping Android Application:\nCrime Mapping is useful in generating real time Crime Maps.\n\nShare it with others to help reduce the crime " +
                        "from the society.";
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Crime Mapping");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(intent, "Choose sharing method:"));
                break;


            case R.id.Feedback:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"himanshusaini299@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "(Crime Mapping) User Feedback:");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Send Email:"));
                break;


            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void viewAboutUs() {

        new LovelyInfoDialog(getActivity())
                .setCancelable(true)
                .setTopColorRes(R.color.colorApplication)
                .setTitle(R.string.info_title_aboutus)
                .setIcon(BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.user_image_info_round))
                .setMessage(R.string.info_message_aboutus)
                .show();
    }

    private void viewAboutApp() {
        new LovelyInfoDialog(getActivity())
                .setCancelable(true)
                .setTopColorRes(R.color.black)
                .setIcon(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.icon_for_aboutus))
                .setTitle(R.string.info_title)
                .setMessage(R.string.info_message)
                .show();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {


        MapsInitializer.initialize(getActivity());
        mMap = googleMap;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                googleMap.setMyLocationEnabled(true);
            } else {
                checkLocationPermission();
            }
        } else {

            googleMap.setMyLocationEnabled(true);
        }

        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnCameraChangeListener(this);
        googleMap.setOnMapLoadedCallback(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMinZoomPreference(4.0f);
        googleMap.setMaxZoomPreference(20.0f);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);


        CameraPosition position1 = CameraPosition.builder().target(new LatLng(26.9124, 75.7873)).zoom(10).bearing(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position1));


        loadMarkersOnMap();


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mClickPos = latLng;

                Intent ed = new Intent(getActivity(), AddCrime.class);
                ed.putExtra("lat", Double.toString(latLng.latitude));
                ed.putExtra("lng", Double.toString(latLng.longitude));
                startActivityForResult(ed, 100);
            }
        });

        checkFirstRun();

        if (crimeListsp.contains(ListLat)) {
            LatfromList = crimeListsp.getString(ListLat, "");
            LngfromList = crimeListsp.getString(ListLng, "");


            LatLng latLng = new LatLng(Double.parseDouble(LatfromList), Double.parseDouble(LngfromList));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13), 2000, null);

            crimeListsp.edit().remove(ListLat).apply();
            crimeListsp.edit().remove(ListLng).apply();

        }

        if (AddCrimesp.contains(ListLatAdded)) {
            aal = AddCrimesp.getString(ListLatAdded, "");
            bbl = AddCrimesp.getString(ListLngAdded, "");


            CameraPosition position1a = CameraPosition.builder().target(new LatLng(Double.parseDouble(aal), Double.parseDouble(bbl))).zoom(10).bearing(0).build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position1a));

            AddCrimesp.edit().remove(ListLatAdded).apply();
            AddCrimesp.edit().remove(ListLngAdded).apply();

        }

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {

//                aacha = marker.getPosition();
                id = marker.getId();

                mmmm();

                imagePopup.initiatePopupWithPicasso(picurl);

                imagePopup.viewPopup();

            }
        });

    }

    public void mmmm() {
        JsonArrayRequest movieReq = new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject obj = response.getJSONObject(i);
                        String compid = "m";
                        String hai = Integer.toString(i);

//                        LatLng ppos = new LatLng(obj.getDouble("crime_latitude"),obj.getDouble("crime_longitude"));


                        if (id.equals(compid.concat(hai))) {

//                            imagePopup.initiatePopupWithPicasso(obj.getString("img_by_user"));
                            picurl = obj.getString("img_by_user");

                            break;

                        }


//                        imagePopup.viewPopup();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());


            }
        });

        AppController.getInstance().addToRequestQueue(movieReq);

    }


    public void loadMarkersOnMap() {


        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());


                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject obj = response.getJSONObject(i);

//                        imagePopup.initiatePopupWithPicasso(obj.getString("img_by_user"));

                        double lat = Double.parseDouble(obj.getString("crime_latitude"));
                        double lng = Double.parseDouble(obj.getString("crime_longitude"));
                        list.add(new LatLng(lat, lng));

                        String myurl = obj.getString("crime_type");


                        LatLng ppos = new LatLng(obj.getDouble("crime_latitude"), obj.getDouble("crime_longitude"));

                        MarkerOptions markerOptions = new MarkerOptions();

                        markerOptions.position(ppos);
                        markerOptions.draggable(false);

                        switch (myurl) {
                            case "Robbery":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_small_assault));
                                break;
                            case "Bikers gang robbery":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_small_moto));
                                break;
                            case "Motor Vehicle Theft":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_small_car));
                                break;
                            case "Hijacking":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_small_hijacking));
                                break;
                            case "Robbery and killing":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_small_robandkill));
                                break;
                            case "Drug Addicts":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_small_drugs));
                                break;
                            case "Garbage":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_small_garbage));
                                break;
                            case "Robbery at ATM":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_small_bank));
                                break;
                            case "Homicide":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_small_homicide));
                                break;
                            case "Loud Sound":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_small_loudsound));
                                break;
                            case "Sexual violence":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_small_sex));
                                break;
                            case "Vandalism":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_small_vandalism));
                                break;
                            case "Police Station":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_small_police));
                                break;
                            case "Crime Against Animals":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_small_animal));
                                break;
                            case "Dangerous place":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_small_dangerous));
                                break;
                        }

                        mMap.setInfoWindowAdapter(new InfoWindowCustom(getContext()));

                        InfoWindowData idata = new InfoWindowData();
                        idata.setHeading(obj.getString("crime_type"));
                        idata.setDescription(obj.getString("crime_description"));
                        idata.setDate(obj.getString("crime_date"));
                        idata.setTime(obj.getString("crime_time"));
                        idata.setDateReported(obj.getString("crime_reporting_date"));
                        idata.setTimeReported(obj.getString("crime_reporting_time"));

                        m = mMap.addMarker(markerOptions);

                        m.setTag(idata);
                        m.hideInfoWindow();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());


            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);


    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
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

        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();

    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {


        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        latLngLoc = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLngLoc);
        markerOptions.title("Current Position");
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngLoc));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        //stop location updates
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), status, 1).show();
            Intent i = new Intent(android.content.Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms&hl=en"));
            startActivity(i);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode) {
            case MY_PERMISSION_REQUEST_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            mMap.setMyLocationEnabled(true);
                        }

                    }
                } else {
                    Toast.makeText(getActivity(), "This app requires location permission to be granted", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public boolean onMyLocationButtonClick() {
        GPSStatus();
        if (!GpsStatus) {
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }

        return false;
    }

    public void GPSStatus() {
        LocationManager serv = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = serv.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (!isConnected) {
            message = "No Internet Connection !";
            color = Color.RED;


            Snackbar snackbar = Snackbar.make(v.findViewById(R.id.crimeMapFragment), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showFirstTimeAlert(int id) {
        alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_DeviceDefault_Light));
        alertDialogBuilder.setTitle(R.string.welcome_crime_mapping);
        alertDialogBuilder.setMessage(R.string.pressss);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.show();

        SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("accepted", true);
        // Commit the edits!
        editor.apply();

    }

    public void checkFirstRun() {
        boolean isFirstRun = getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            showFirstTimeAlert(0);
            getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        mMap.setOnMapLoadedCallback(this);

//        if (cameraPosition.zoom < 9) {
//
//           mMap.clear();
//           addHeatMap();
//        } else {
//          mMap.clear();
//          loadMarkersOnMap();
//        }


        pb.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMapLoaded() {

        zoomlvl = mMap.getCameraPosition().zoom;


        if (zoomlvl <= 9 && !kismat) {
            mMap.clear();
            addHeatMap();
            kismat = true;
        } else if (zoomlvl > 9 && kismat) {
            mMap.clear();
            loadMarkersOnMap();
            kismat = false;
        }


        pb.setVisibility(View.INVISIBLE);

    }

    private void addHeatMap() {

        // Create the gradient.
        int[] colors = {
                Color.rgb(102, 225, 0), // green
                Color.rgb(255, 0, 0)    // red
        };

        float[] startPoints = {
                0.2f, 1f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        // Create a heat map tile provider, passing it the latlngs.

        mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .gradient(gradient)
                .radius(40)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    private class updateData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null;

            try {
                URL url;
                url = new URL(params[0]);
                conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    conn.getInputStream();
                } else {
                    conn.getErrorStream();
                }
                return "Done";
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return null;
        }
    }
}

