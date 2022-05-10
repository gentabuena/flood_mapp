package com.fea.floodmapp.main.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.ActivityWeatherForecastBinding;
import com.fea.floodmapp.main.dependencies.MyApp;
import com.fea.floodmapp.main.utils.SessionManager;

import javax.inject.Inject;

public class WeatherForecastActivity extends AppCompatActivity {

    ActivityWeatherForecastBinding activityWeatherForecastBinding;
    @Inject
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWeatherForecastBinding = ActivityWeatherForecastBinding.inflate(getLayoutInflater());
        setContentView(activityWeatherForecastBinding.getRoot());
        MyApp.getAppComponent().inject(this);

        overlayGradientImageViewOnStatusBar();
        setViews();
        loadWebViewURL();
    }

    private void overlayGradientImageViewOnStatusBar(){
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        activityWeatherForecastBinding.ivHeaderStatusBarOverlay.getLayoutParams().height = getStatusBarHeight();
        activityWeatherForecastBinding.ivHeaderStatusBarOverlay.requestLayout();
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
        activityWeatherForecastBinding.incHeader.tvHeaderTitle.setText(getResources().getString(R.string.text_weather_forecast));
        activityWeatherForecastBinding.incHeader.ivHeaderBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void loadWebViewURL(){
        activityWeatherForecastBinding.wvWeatherForecast.setWebViewClient(new WebViewClient());
        activityWeatherForecastBinding.wvWeatherForecast.loadUrl("https://fea-app.herokuapp.com/weathers?lat=" + sessionManager.getCurrentLat() +"&long=" + sessionManager.getCurrentLong());
    }

    @Override
    public void onBackPressed() {
        if (activityWeatherForecastBinding.wvWeatherForecast.canGoBack()) {
            activityWeatherForecastBinding.wvWeatherForecast.goBack();
        } else {
            super.onBackPressed();
        }
    }
}