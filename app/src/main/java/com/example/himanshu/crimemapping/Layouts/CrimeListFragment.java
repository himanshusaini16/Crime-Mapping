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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.example.himanshu.crimemapping.Crime;
import com.example.himanshu.crimemapping.CustomListAdapter;
import com.example.himanshu.crimemapping.MyApplication;
import com.example.himanshu.crimemapping.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    String crime_type;
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

        loadList();

        setHasOptionsMenu(true);
        return v;
    }


    public void loadList() {
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


    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_crimelist, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.FilterByRobbery:
                crime_type = "Robbery";
                listView.setAdapter(null);
                runVolleyKaMethod();
                loadList();
                break;

            case R.id.FilterByBikersGang:
                crime_type = "Bikers gang robbery";
                listView.setAdapter(null);
                runVolleyKaMethod();
                loadList();
                break;

            case R.id.FilterByMotorVehicle:
                crime_type = "Motor Vehicle Theft";
                listView.setAdapter(null);
                runVolleyKaMethod();
                loadList();
                break;

            case R.id.FilterByHijacking:
                crime_type = "Hijacking";
                listView.setAdapter(null);
                runVolleyKaMethod();
                loadList();
                break;

            case R.id.FilterByRobandKill:
                crime_type = "Robbery and killing";
                listView.setAdapter(null);
                runVolleyKaMethod();
                loadList();
                break;

            case R.id.FilterByDrug:
                crime_type = "Drug Addicts";
                listView.setAdapter(null);
                runVolleyKaMethod();
                loadList();
                break;

            case R.id.FilterByGarbage:
                crime_type = "Garbage";
                listView.setAdapter(null);
                runVolleyKaMethod();
                loadList();
                break;

            case R.id.FilterByAtm:
                crime_type = "Robbery at ATM";
                listView.setAdapter(null);
                runVolleyKaMethod();
                loadList();
                break;

            case R.id.FilterByHomicide:
                crime_type = "Homicide";
                listView.setAdapter(null);
                runVolleyKaMethod();
                loadList();
                break;

            case R.id.FilterByLoudSound:
                crime_type = "Loud Sound";
                listView.setAdapter(null);
                runVolleyKaMethod();
                loadList();
                break;
            case R.id.FilterBySexual:
                crime_type = "Sexual violence";
                listView.setAdapter(null);
                runVolleyKaMethod();
                loadList();
                break;

            case R.id.FilterByVandalism:
                crime_type = "Vandalism";
                listView.setAdapter(null);
                runVolleyKaMethod();
                loadList();
                break;
            case R.id.FilterByPolice:
                crime_type = "Police Station";
                listView.setAdapter(null);
                runVolleyKaMethod();
                loadList();
                break;

            case R.id.FilterByAnimals:
                crime_type = "Crime Against Animals";
                listView.setAdapter(null);
                runVolleyKaMethod();
                loadList();
                break;
            case R.id.FilterByDangerousPlace:
                crime_type = "Dangerous place";
                listView.setAdapter(null);
                runVolleyKaMethod();
                loadList();
                break;


//            case R.id.sortbyLatestCrime:
//                listView.setAdapter(null);
//
//                String link = "http://thetechnophile.000webhostapp.com/load_crime.php";
//                new CrimeListFragment.updateData().execute(link);
//                loadList();
//                break;
//
//            case R.id.sortbyOldestCrime:
//                listView.setAdapter(null);
//
//                String link2 = "http://thetechnophile.000webhostapp.com/load_crime.php";
//                new CrimeListFragment.updateData().execute(link2);
//
//                loadListOldest();
//                break;


            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void runVolleyKaMethod() {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String filter_url = "http://thetechnophile.000webhostapp.com/load_crime_filtered.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, filter_url, new Response.Listener<String>() {

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
                params.put("crime_type", crime_type);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }


//    public void loadListOldest() {
//        progressDialog = new SpotsDialog(getActivity(), R.style.Custom3);
//        progressDialog.show();
//
//        JsonArrayRequest movieReq = new JsonArrayRequest(url,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        Log.d(TAG, response.toString());
//                        progressDialog.hide();
//
//                        // Parsing json
//                        for (int i = 0; i <= response.length(); i--) {
//                            try {
//                                JSONObject obj = response.getJSONObject(i);
//                                Crime movie = new Crime();
//                                movie.setTitle(obj.getString("crime_type"));
//                                movie.setThumbnailUrl(obj.getString("crime_image_marker"));
//                                movie.setDes(obj.getString("crime_description"));
//                                movie.setDate(obj.getString("crime_date"));
//                                movie.setTime(obj.getString("crime_time"));
//                                movie.setLat(obj.getString("crime_latitude"));
//                                movie.setLng(obj.getString("crime_longitude"));
//                                movie.setAddress(obj.getString("crime_location_address"));
//
//                                crimeList.add(movie);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                progressDialog.hide();
//
//            }
//        });
//
//        AppController.getInstance().addToRequestQueue(movieReq);
//    }


//    private class updateData extends AsyncTask<String, String, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            HttpURLConnection conn = null;
//
//            try {
//                URL url;
//                url = new URL(params[0]);
//                conn = (HttpURLConnection) url.openConnection();
//                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                    conn.getInputStream();
//                } else {
//                    conn.getErrorStream();
//                }
//                return "Done";
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if (conn != null) {
//                    conn.disconnect();
//                }
//            }
//            return null;
//        }
//    }
//

}
