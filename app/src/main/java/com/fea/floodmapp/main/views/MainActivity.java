package com.fea.floodmapp.main.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.ActivityMainBinding;
import com.fea.floodmapp.main.datamodels.JsonResponse;
import com.fea.floodmapp.main.dependencies.MyApp;
import com.fea.floodmapp.main.interfaces.ServiceListener;
import com.fea.floodmapp.main.utils.ApiHelper;
import com.fea.floodmapp.main.utils.ApiService;
import com.fea.floodmapp.main.utils.KeyboardUtil;
import com.fea.floodmapp.main.utils.SessionManager;
import com.fea.floodmapp.main.views.fragments.HomeFragment;
import com.fea.floodmapp.main.views.fragments.ProfileFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        //testAPILangs();
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