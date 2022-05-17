package com.fea.floodmapp.main.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.ActivityAppWebviewBinding;
import com.fea.floodmapp.main.dependencies.MyApp;
import com.fea.floodmapp.main.utils.CommonMethods;

import javax.inject.Inject;

public class AppWebviewActivity extends AppCompatActivity {

    ActivityAppWebviewBinding activityAppWebviewBinding;
    Intent intent;
    int appWebviewType;

    @Inject
    CommonMethods commonMethods;

    AlertDialog dialog;

    private static final int ABOUT_US_PAGE = 0;
    private static final int TERMS_PAGE = 1; // 2 is Privacy Policy

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getAppComponent().inject(this);
        activityAppWebviewBinding = ActivityAppWebviewBinding.inflate(getLayoutInflater());
        setContentView(activityAppWebviewBinding.getRoot());

        intent = getIntent();
        appWebviewType = intent.getIntExtra("web_view_type", 0);

        overlayGradientImageViewOnStatusBar();
        setViews();
        commonMethods.showProgressDialog(this);

        loadWebViewURL();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!commonMethods.isOnline(this)){
            dialog = commonMethods.getAlertDialog(this, getResources().getString(R.string.network_failure));
            dialog.show();
            try{ // Hide loading if still active
                commonMethods.hideProgressDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void overlayGradientImageViewOnStatusBar(){
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        activityAppWebviewBinding.ivHeaderStatusBarOverlay.getLayoutParams().height = getStatusBarHeight();
        activityAppWebviewBinding.ivHeaderStatusBarOverlay.requestLayout();
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
        String pageTitle = appWebviewType == ABOUT_US_PAGE ? "About Us" : appWebviewType == TERMS_PAGE ? "Terms & Conditions" : "Privacy Policy";
        activityAppWebviewBinding.incHeader.tvHeaderTitle.setText(pageTitle);
        activityAppWebviewBinding.incHeader.ivHeaderBack.setOnClickListener(v -> onBackPressed());
    }

    private void loadWebViewURL(){
        String appWebviewURL = appWebviewType == ABOUT_US_PAGE ? getResources().getString(R.string.about_url) : appWebviewType == TERMS_PAGE ? getResources().getString(R.string.terms_conditions_url) : getResources().getString(R.string.privacy_policy_url);
        activityAppWebviewBinding.wvAppWebpages.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                commonMethods.hideProgressDialog();
            }
        });
        activityAppWebviewBinding.wvAppWebpages.loadUrl(appWebviewURL);
    }

    @Override
    public void onBackPressed() {
        if (activityAppWebviewBinding.wvAppWebpages.canGoBack()) {
            activityAppWebviewBinding.wvAppWebpages.goBack();
        } else {
            super.onBackPressed();
        }
    }
}