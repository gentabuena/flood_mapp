package com.fea.floodmapp.main.views.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.ActivityWeatherForecastBinding;
import com.fea.floodmapp.databinding.FragmentHomeBinding;
import com.fea.floodmapp.main.database.DatabaseHelper;
import com.fea.floodmapp.main.dependencies.MyApp;
import com.fea.floodmapp.main.utils.CommonMethods;
import com.fea.floodmapp.main.utils.SessionManager;
import com.fea.floodmapp.main.views.EmergencyNumbersActivity;
import com.fea.floodmapp.main.views.EvacuationSitesActivity;
import com.fea.floodmapp.main.views.FloodPreparednessGuideActivity;
import com.fea.floodmapp.main.views.MainActivity;
import com.fea.floodmapp.main.views.PagasaUpdatesActivity;
import com.fea.floodmapp.main.views.SafetyPrecautionsActivity;
import com.fea.floodmapp.main.views.SigninSignupActivity;
import com.fea.floodmapp.main.views.WeatherForecastActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    FragmentHomeBinding fragmentHomeBinding;
    Context context;
    DatabaseHelper databaseHelper;
    GoogleSignInClient googleSignInClient;

    @Inject
    SessionManager sessionManager;

    @Inject
    CommonMethods commonMethods;

    AlertDialog dialog;

    int evacuationOrWeather = 0;

    // Location Related
    FusedLocationProviderClient fusedLocationProviderClient;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MyApp.getAppComponent().inject(this);
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return fragmentHomeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews();
        databaseHelper = DatabaseHelper.getInstance(context);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.google_oauth))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    private void initializeViews() {
        fragmentHomeBinding.rltFragHomeEvacuationSites.setOnClickListener(v -> {
            evacuationOrWeather = 0;
            initLocationHandler();
        });
        fragmentHomeBinding.rltFragHomeWeatherForecast.setOnClickListener(v -> {
            evacuationOrWeather = 1;
            initLocationHandler();
        });
        fragmentHomeBinding.rltFragHomeSafetyPrecautions.setOnClickListener(v -> gotoSafetyPrecautions());
        fragmentHomeBinding.rltFragHomeEmergencyNumbers.setOnClickListener(v -> gotoEmergencyNumbers());
        fragmentHomeBinding.rltFragHomeFloodPreparedness.setOnClickListener(v -> gotoGuideLines());
        fragmentHomeBinding.rltFragHomePagasaUpdates.setOnClickListener(v -> gotoPagasaUpdates());
        fragmentHomeBinding.ivFragHomeSignout.setOnClickListener(v -> signOutAccount());
    }

    private void signOutAccount() {
        Log.d(TAG, "Sign out account method");
        Intent intent = new Intent(context, SigninSignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        sessionManager.clearCache();
        databaseHelper.dropTable(context);
        googleSignInClient.signOut();
        startActivity(intent);
    }

    private void goToEvacuationSites() {
        Intent intent = new Intent(context, EvacuationSitesActivity.class);
        startActivity(intent);
    }

    private void gotoEmergencyNumbers() {
        Intent intent = new Intent(context, EmergencyNumbersActivity.class);
        startActivity(intent);
    }

    private void gotoGuideLines() {
        Intent intent = new Intent(context, FloodPreparednessGuideActivity.class);
        startActivity(intent);
    }

    private void gotoPagasaUpdates() {
        if (commonMethods.isOnline(context)){
            Intent intent = new Intent(context, PagasaUpdatesActivity.class);
            startActivity(intent);
        } else {
            dialog = commonMethods.getAlertDialog(context, getResources().getString(R.string.network_failure_access_feature));
            dialog.show();
        }
    }

    private void gotoSafetyPrecautions() {
        Intent intent = new Intent(context, SafetyPrecautionsActivity.class);
        startActivity(intent);
    }

    private void gotoWeatherUpdates() {
        Intent intent = new Intent(context, WeatherForecastActivity.class);
        startActivity(intent);
    }

    private void initLocationHandler() {
        Log.d(TAG, "Get Location method");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            Log.d(TAG, "Location here -- \n" +
                                    "Latitude: " + addresses.get(0).getLatitude() + "\n " +
                                    "Longitude: " + addresses.get(0).getLongitude() + "\n " +
                                    "Address: " + addresses.get(0).getAddressLine(0) + "\n " +
                                    "Locality: " + addresses.get(0).getLocality());

                            sessionManager.setCurrentLat(String.valueOf(addresses.get(0).getLatitude()));
                            sessionManager.setCurrentLong(String.valueOf(addresses.get(0).getLongitude()));
                            sessionManager.setCity(addresses.get(0).getLocality());

                            // GO TO this Screens after Successful fetch of location
                            if (evacuationOrWeather == 0) goToEvacuationSites();
                            else gotoWeatherUpdates();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Toast.makeText(context, "Location permission must be granted to use this feature, kindly enable it on the your phone's app permission.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    initLocationHandler();
                } else {
                    Toast.makeText(context, "Location permission must be granted to use this feature", Toast.LENGTH_SHORT).show();
                }
            });
}