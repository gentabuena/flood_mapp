package com.fea.floodmapp.main.views;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.ActivityMainBinding;
import com.fea.floodmapp.main.dependencies.MyApp;
import com.fea.floodmapp.main.utils.ApiHelper;
import com.fea.floodmapp.main.utils.CommonMethods;
import com.fea.floodmapp.main.utils.SessionManager;
import com.fea.floodmapp.main.views.fragments.HomeFragment;
import com.fea.floodmapp.main.views.fragments.ProfileFragment;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding activityMainBinding;

    private final int tabHome = R.id.tab_home;
    private final int tabProfile = R.id.tab_profile;
    boolean fragmentJustInitialized = true;

    HomeFragment homeFragment;
    ProfileFragment profileFragment;

    public @Inject
    ApiHelper apiHelper;

    @Inject
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getAppComponent().inject(this); // Make this class injectable

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        initBottomNavigationView();
        Log.d(TAG, "App Token is: " + sessionManager.getToken());
    }

    private void initBottomNavigationView() {
        homeFragment = new HomeFragment();
        profileFragment = new ProfileFragment();

        activityMainBinding.mainNavigation.setOnItemSelectedListener(item -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
            int tabID = item.getItemId();
            switch (tabID) {
                case tabHome:
                    if (currentFragment instanceof HomeFragment) {
                        return false;
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, homeFragment).commit();
                        //Toast.makeText(MainActivity.this, "Home Tab was selected", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case tabProfile:
                    if (currentFragment instanceof ProfileFragment) {
                        return false;
                    } else {
                        //if (sessionManager.getIsProfileEditing()) discardChanges();
                        //else getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, profileFragment).commit();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, profileFragment).commit();
                        //Toast.makeText(MainActivity.this, "Profile Tab was selected", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            return true;
        });

        // Chose default fragment upon initialize
        if (fragmentJustInitialized) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_fragment_container, homeFragment);
            transaction.commit();
            fragmentJustInitialized = false;
        }
    }
}