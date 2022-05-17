package com.fea.floodmapp.main.views;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding activityMainBinding;

    private final int tabHome = R.id.tab_home;
    private final int tabProfile = R.id.tab_profile;
    boolean fragmentJustInitialized = true;

    HomeFragment homeFragment;
    ProfileFragment profileFragment;
    boolean mainIsInitialized = false;

    @Inject
    SessionManager sessionManager;
    @Inject
    CommonMethods commonMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getAppComponent().inject(this); // Make this class injectable

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        initBottomNavigationView();
        Log.d(TAG, "App Token is: " + sessionManager.getToken());
        //initLocationHandler();
        requestLocationAccess();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mainIsInitialized) {
            if (!commonMethods.isGPSEnabled(this)){
                Toast.makeText(this, "GPS must be turned on to get your location! Kindly turn it on.", Toast.LENGTH_SHORT).show();
            }
        }
        else mainIsInitialized = true;
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

    private void requestLocationAccess(){
        //locationPermissionRequest.launch(new String[] {Manifest.permission.ACCESS_FINE_LOCATION});
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location permission already granted.");
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void initLocationHandler() {
        Log.d(TAG, "Get Location method");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location permission already granted.");
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        int deniedCount = sessionManager.getLocationPermissionDeniedCount();
                        if (deniedCount >= 1){
                            Toast.makeText(this, "Location permission must be granted to use this feature, kindly enable it on the your phone's app permission.", Toast.LENGTH_SHORT).show();
                        } else {
                            sessionManager.setLocationPermissionDeniedCount(deniedCount + 1);
                            reRunAllowAccessLocation();
                        }
                    } else {
                        reRunAllowAccessLocation();
                    }
                } else {
                    sessionManager.setLocationPermissionDeniedCount(0);
                    isGPSEnabled();
                }
            });

    /** ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    fineLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                }
                if (fineLocationGranted == null && !fineLocationGranted) {
                    // Precise location access granted.
                    reRunAllowAccessLocation();
                }
            }); **/

    private void reRunAllowAccessLocation(){
        Dialog dialog = new Dialog(this, R.style.AlertDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_yes_no);
        dialog.setCanceledOnTouchOutside(false);

        RelativeLayout rlt_no = dialog.findViewById(R.id.rlt_dialog_no);
        RelativeLayout rlt_yes = dialog.findViewById(R.id.rlt_dialog_yes);
        TextView dialogMessage = dialog.findViewById(R.id.tv_dialog_message);

        dialogMessage.setText(getResources().getString(R.string.location_access));

        rlt_no.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                int deniedCount = sessionManager.getLocationPermissionDeniedCount();
                if (deniedCount >= 1){
                    Toast.makeText(this, "Location permission must be granted to use this feature, kindly enable it on the your phone's app permission.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Location permission must be granted to use this feature.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Location permission must be granted to use this feature.", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        rlt_yes.setOnClickListener(v -> {
            requestLocationAccess();
            dialog.dismiss();
        });
        dialog.show();
    }

    private void gotoGPSSettings(){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private void isGPSEnabled(){
        if (!commonMethods.isGPSEnabled(this)){
            Toast.makeText(this, "GPS must be turned on to get your location!", Toast.LENGTH_SHORT).show();
            gotoGPSSettings();
        }
    }
}