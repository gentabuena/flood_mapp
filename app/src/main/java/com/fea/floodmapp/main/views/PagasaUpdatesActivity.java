package com.fea.floodmapp.main.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.ActivityPagasaUpdatesBinding;

public class PagasaUpdatesActivity extends AppCompatActivity {

    ActivityPagasaUpdatesBinding activityPagasaUpdatesBinding;
    private static final String baseURl = "http://twitter.com";
    private static final String widgetInfo = "<a class=\"twitter-timeline\" href=\"https://twitter.com/PAGASAFFWS?ref_src=twsrc%5Etfw\">Tweets by PAGASAFFWS</a> <script async src=\"https://platform.twitter.com/widgets.js\" charset=\"utf-8\"></script>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPagasaUpdatesBinding = ActivityPagasaUpdatesBinding.inflate(getLayoutInflater());
        setContentView(activityPagasaUpdatesBinding.getRoot());

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

        activityPagasaUpdatesBinding.ivHeaderStatusBarOverlay.getLayoutParams().height = getStatusBarHeight();
        activityPagasaUpdatesBinding.ivHeaderStatusBarOverlay.requestLayout();
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
        activityPagasaUpdatesBinding.incHeader.tvHeaderTitle.setText(getResources().getString(R.string.text_pagasa_title));
        activityPagasaUpdatesBinding.incHeader.ivHeaderBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }
    private void loadWebViewURL(){
        activityPagasaUpdatesBinding.wvPagasaTwitter.setWebViewClient(new WebViewClient());
        activityPagasaUpdatesBinding.wvPagasaTwitter.loadUrl("https://mobile.twitter.com/pagasaffws");

        // Embedded Link for Twitter
        //activityPagasaUpdatesBinding.wvPagasaTwitter.loadDataWithBaseURL(baseURl, widgetInfo, "text/html", "UTF-8", null);

        WebSettings webSettings = activityPagasaUpdatesBinding.wvPagasaTwitter.getSettings();
        webSettings.setJavaScriptEnabled(true);

        /** Disable touch
        activityPagasaUpdatesBinding.wvPagasaTwitter.setLongClickable(false);
        activityPagasaUpdatesBinding.wvPagasaTwitter.setOnTouchListener((arg0, arg1) -> arg1.getAction() == MotionEvent.ACTION_UP); **/
    }
}