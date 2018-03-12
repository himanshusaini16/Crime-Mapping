package com.example.himanshu.crimemapping.Layouts;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.example.himanshu.crimemapping.R;
import com.google.android.gms.ads.MobileAds;


public class BottomNavigationHomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_home);

        MobileAds.initialize(this, "ca-app-pub-4510895115386086~4521143649");

        setupNavigationView();


    }

  /*  @Override
    public void sendData(String m1, String m2) {
        FragmentManager fm = getFragmentManager();
        CrimeMapFragment frag = (CrimeMapFragment) fm.findFragmentById(R.id.crimeMapFragment);

        frag.recieveData(m1,m2);
    }*/


    private void setupNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {

            // Select first menu item by default and show Fragment accordingly.
            Menu menu = bottomNavigationView.getMenu();
            selectFragment(menu.getItem(0));

            // Set action to perform when any menu-item is selected.
            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            selectFragment(item);
                            return true;
                        }
                    });
        }
    }

    protected void selectFragment(MenuItem item) {

        item.setChecked(true);

        switch (item.getItemId()) {

            case R.id.navigation_crimemap:
                pushFragment(new CrimeMapFragment());
                getSupportActionBar().setTitle("Crime Map");
                break;

            case R.id.navigation_crimelist:
                pushFragment(new CrimeListFragment());
                getSupportActionBar().setTitle("Crime List");
                break;

            case R.id.navigation_profile:
                pushFragment(new UserProfileFragment());
                getSupportActionBar().setTitle("Profile");
                break;

        }
    }

    protected void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.rootLayout, fragment);
                ft.addToBackStack("");
                ft.commit();
            }
        }
    }


}
