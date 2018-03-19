package com.example.himanshu.crimemapping.Layouts;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.himanshu.crimemapping.AppController;
import com.example.himanshu.crimemapping.ConnectivityReceiver;
import com.example.himanshu.crimemapping.Crime;
import com.example.himanshu.crimemapping.CustomListAdapter;
import com.example.himanshu.crimemapping.MyApplication;
import com.example.himanshu.crimemapping.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;


public class CrimeListFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {

    public static final String crimelistsharedpref = "myprefCrimeList";
    public static final String ListLat = "nameLat";
    public static final String ListLng = "nameLng";
    private static final String TAG = CrimeListFragment.class.getSimpleName();
    private static final String url = "http://thetechnophile.000webhostapp.com/load_crime.json";
    View v;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String latHai, lngHai;
    SharedPreferences crimeListsp;
    private AlertDialog progressDialog;
    private List<Crime> crimeList = new ArrayList<>();
    private ListView listView;
    private CustomListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        crimeListsp = getContext().getSharedPreferences(crimelistsharedpref, Context.MODE_PRIVATE);

        mSwipeRefreshLayout = v.findViewById(R.id.swipeToRefresh);
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

        listView = v.findViewById(R.id.lv_crime);
        adapter = new CustomListAdapter(getActivity(), crimeList);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Crime ss = (Crime) listView.getItemAtPosition(position);

                    latHai = ss.getLat();
                    lngHai = ss.getLng();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;

            }
        });
        registerForContextMenu(listView);
        progressDialog = new SpotsDialog(getActivity(), R.style.Custom3);
        progressDialog.show();

        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        progressDialog.hide();

                        // Parsing json
                        for (int i = response.length(); i >= 0; i--) {
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
                                movie.setAddress(obj.getString("crime_location_address"));

                                crimeList.add(movie);

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
                progressDialog.hide();

            }
        });

        AppController.getInstance().addToRequestQueue(movieReq);
        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, v.getId(), 1, "Find on Map");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Find on Map") {
            SharedPreferences.Editor ed = crimeListsp.edit();
            ed.putString(ListLat, latHai);
            ed.putString(ListLng, lngHai);
            ed.apply();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.rootLayout, new CrimeMapFragment());
            fragmentManager.popBackStack();
            ft.commit();

        }
        return super.onContextItemSelected(item);
    }



    private void shuffle() {
        adapter = new CustomListAdapter(getActivity(), crimeList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

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
