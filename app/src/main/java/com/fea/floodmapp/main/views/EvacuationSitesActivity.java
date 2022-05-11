package com.fea.floodmapp.main.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.ActivityEvacuationSitesBinding;
import com.fea.floodmapp.main.datamodels.EvacuationSitesModel;
import com.fea.floodmapp.main.dependencies.MyApp;
import com.fea.floodmapp.main.utils.ApiHelper;
import com.fea.floodmapp.main.utils.ApiService;
import com.fea.floodmapp.main.utils.CommonMethods;
import com.fea.floodmapp.main.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvacuationSitesActivity extends AppCompatActivity {

    private static final String TAG = "EvacuationSites";

    ActivityEvacuationSitesBinding activityEvacuationSitesBinding;

    @Inject
    ApiHelper apiHelper;
    @Inject
    SessionManager sessionManager;
    @Inject
    Gson gson;
    @Inject
    CommonMethods commonMethods;

    ArrayList<EvacuationSitesModel> evacuationSitesModelArrayList;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getAppComponent().inject(this);
        activityEvacuationSitesBinding = ActivityEvacuationSitesBinding.inflate(getLayoutInflater());
        setContentView(activityEvacuationSitesBinding.getRoot());

        overlayGradientImageViewOnStatusBar();
        setViews();
        startShimmer();
        fetchEvacuationSitesAPI();
    }

    private void overlayGradientImageViewOnStatusBar(){
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        activityEvacuationSitesBinding.ivHeaderStatusBarOverlay.getLayoutParams().height = getStatusBarHeight();
        activityEvacuationSitesBinding.ivHeaderStatusBarOverlay.requestLayout();
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setViews(){
        activityEvacuationSitesBinding.incHeader.tvHeaderTitle.setText(getResources().getString(R.string.text_evacuation_sites));
        activityEvacuationSitesBinding.incHeader.ivHeaderBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void fetchEvacuationSitesAPI(){
        ApiService apiService = apiHelper.feaappApi.create(ApiService.class);
        apiService.getEvacuationSites(sessionManager.getCurrentLong(), sessionManager.getCurrentLat()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null){
                    try {
                        String strResponse = response.body().string();
                        //Toast.makeText(EvacuationSitesActivity.this, "API Success", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Response here -- " + strResponse);
                        Type arraylistType = new TypeToken<ArrayList<EvacuationSitesModel>>(){}.getType();
                        evacuationSitesModelArrayList = gson.fromJson(strResponse, arraylistType);
                        showEvacuationSites();
                        stopShimmer();
                    } catch (IOException e) {
                        Log.d(TAG, "Error here -- " + e.getMessage());
                        stopShimmer();
                        if (!commonMethods.isOnline(EvacuationSitesActivity.this)){
                            dialog = commonMethods.getAlertDialog(EvacuationSitesActivity.this, getResources().getString(R.string.network_failure));
                        } else {
                            dialog = commonMethods.getAlertDialog(EvacuationSitesActivity.this, getResources().getString(R.string.something_went_wrong));
                        }
                        dialog.show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                stopShimmer();
                if (!commonMethods.isOnline(EvacuationSitesActivity.this)){
                    dialog = commonMethods.getAlertDialog(EvacuationSitesActivity.this, getResources().getString(R.string.network_failure));
                } else {
                    dialog = commonMethods.getAlertDialog(EvacuationSitesActivity.this, getResources().getString(R.string.something_went_wrong));
                }
                dialog.show();
                Log.d(TAG, "Response here -- " + t.getMessage());
            }
        });
    }

    private void showEvacuationSites(){
        for (EvacuationSitesModel evacuationSites : evacuationSitesModelArrayList){
            View locationView;
            locationView = LayoutInflater.from(this).inflate(R.layout.evacuation_site, null);
            activityEvacuationSitesBinding.lltEvacsiteLocationContainer.addView(locationView);

            TextView evacuationSiteName, evacuationSiteDistance, evacuationSitePopulation, evacuationSiteAddress, evacuationSiteStatus;
            RelativeLayout navigate;
            evacuationSiteName = locationView.findViewById(R.id.tv_evacsite_name);
            evacuationSiteDistance = locationView.findViewById(R.id.tv_evacsite_distance);
            evacuationSitePopulation = locationView.findViewById(R.id.tv_evacsite_population);
            evacuationSiteStatus = locationView.findViewById(R.id.tv_evacsite_status);
            evacuationSiteAddress  = locationView.findViewById(R.id.tv_evacsite_address);
            navigate = locationView.findViewById(R.id.rlt_evacsite_navigate);

            evacuationSiteName.setText(evacuationSites.getName());

            String currentStatus = "Status: " + (evacuationSites.getStatus() == 0 ? "Available" : "Full");
            evacuationSiteStatus.setText(currentStatus);
            evacuationSiteStatus.setTextColor(ContextCompat.getColor(this, (evacuationSites.getStatus() == 0 ? R.color.black : R.color.full_red)));

            String currentPopulation = "Population: " + evacuationSites.getPopulation();
            evacuationSitePopulation.setText(currentPopulation);

            String evacAddress = "Address: " + evacuationSites.getAddress();
            evacuationSiteAddress.setText(evacAddress);

            String roundedDistance = "Estimated Distance: " + String.format(Locale.US,"%.3f", Double.valueOf(evacuationSites.getDistance())) + " KM";
            evacuationSiteDistance.setText(roundedDistance);

            navigate.setOnClickListener(v -> navigateToMap(evacuationSites.getLongitude(),evacuationSites.getLatitude()));
        }
    }

    private void navigateToMap(String longitude, String latitude){
        if (commonMethods.isOnline(this)) {
            Dialog dialog = new Dialog(this, R.style.AlertDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_navigation);
            dialog.setCanceledOnTouchOutside(true);
            RelativeLayout rlt_googlemaps = dialog.findViewById(R.id.rlt_googlemaps);
            RelativeLayout rlt_waze = dialog.findViewById(R.id.rlt_waze);

            rlt_googlemaps.setOnClickListener(v -> {
                // check if google maps app is installed
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                try{
                    startActivity(mapIntent);
                    dialog.dismiss();
                } catch(Exception ex) {
                    Toast.makeText(this, "Google Maps is not installed! Install the app first and try again.", Toast.LENGTH_SHORT).show();
                }
            });

            rlt_waze.setOnClickListener(v -> {
                // check if google maps app is installed
                String wazeUrl = "https://waze.com/ul?ll=" + latitude + "%2C" + longitude + "&amp;navigate=yes";
                Uri uri = Uri.parse(wazeUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.waze");
                try {
                    startActivity(intent);
                    dialog.dismiss();
                } catch (Exception ex){
                    Toast.makeText(this, "Waze is not installed! Install the app first and try again.", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        } else {
            dialog = commonMethods.getAlertDialog(this, getResources().getString(R.string.network_failure));
            dialog.show();
        }
    }

    private void startShimmer(){
        activityEvacuationSitesBinding.svEvacsiteLocations.setVisibility(View.INVISIBLE);
        activityEvacuationSitesBinding.shimmerEvacsite.setVisibility(View.VISIBLE);
        activityEvacuationSitesBinding.shimmerEvacsite.startShimmer();
    }

    private void stopShimmer(){
        activityEvacuationSitesBinding.svEvacsiteLocations.setVisibility(View.VISIBLE);
        activityEvacuationSitesBinding.shimmerEvacsite.setVisibility(View.INVISIBLE);
        activityEvacuationSitesBinding.shimmerEvacsite.stopShimmer();
    }
}