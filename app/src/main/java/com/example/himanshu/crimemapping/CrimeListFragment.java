package com.example.himanshu.crimemapping;


import android.app.AlertDialog;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;


public class CrimeListFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {

    View v;

    private static final String TAG = CrimeListFragment.class.getSimpleName();

    // Movies json url
    private static final String url = "http://thetechnophile.000webhostapp.com/load_crime.json";
    private AlertDialog progressDialog;
    private List<Crime> crimeList = new ArrayList<Crime>();
    private ListView listView;
    private CustomListAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_crime_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorApplication);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shuffle();
                    }
                }, 2500);
            }
        });

        listView = (ListView)v.findViewById(R.id.lv_crime);
        adapter = new CustomListAdapter(getActivity(), crimeList);
        listView.setAdapter(adapter);


        progressDialog = new SpotsDialog(getActivity(), R.style.Custom3);

        progressDialog.show();

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        progressDialog.hide();

                        // Parsing json
                        for (int i = response.length(); i >=0; i--) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Crime movie = new Crime();
                                movie.setTitle(obj.getString("crime_type"));
                                movie.setThumbnailUrl(obj.getString("crime_image_marker"));
                                movie.setDes(obj.getString("crime_description"));
                                movie.setDate(obj.getString("crime_date"));
                                movie.setTime(obj.getString("crime_time"));
                                movie.setLat(obj.getString("crime_latitude"));
                                movie.setLng(obj.getString("crime_longitude"));
                                // adding movie to movies arraymovie.setTime(obj.getString("crime_time"));
                                crimeList.add(movie);



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressDialog.hide();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
        return v;
    }

    private void shuffle() {


        adapter = new CustomListAdapter(getActivity(), crimeList);
        listView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }


    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (!isConnected) {
            message = "No Internet Connection !";
            color = Color.RED;


            Snackbar snackbar = Snackbar.make(v.findViewById(R.id.crimeListFragment), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        } }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progressDialog.hide();
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
}
