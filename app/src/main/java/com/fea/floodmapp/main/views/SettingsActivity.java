package com.fea.floodmapp.main.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.ActivitySettingsBinding;
import com.fea.floodmapp.main.database.DatabaseHelper;
import com.fea.floodmapp.main.datamodels.UserInfoModel;
import com.fea.floodmapp.main.dependencies.MyApp;
import com.fea.floodmapp.main.utils.KeyboardUtil;
import com.fea.floodmapp.main.utils.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import javax.inject.Inject;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding activitySettingsBinding;
    UserInfoModel userInfoModel;
    private static final String TAG = "SettingsActivity";
    GoogleSignInClient googleSignInClient;
    DatabaseHelper databaseHelper;

    @Inject
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingsBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(activitySettingsBinding.getRoot());
        MyApp.getAppComponent().inject(this);

        overlayGradientImageViewOnStatusBar();
        setViews();

        fetchFromLocalDB();
        setImageViewToGmailDP();
        setViewsInfo();
    }

    private void overlayGradientImageViewOnStatusBar(){
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        activitySettingsBinding.ivHeaderStatusBarOverlay.getLayoutParams().height = getStatusBarHeight();
        activitySettingsBinding.ivHeaderStatusBarOverlay.requestLayout();
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
        activitySettingsBinding.incHeader.tvHeaderTitle.setText(getResources().getString(R.string.action_settings));
        activitySettingsBinding.incHeader.ivHeaderBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void fetchFromLocalDB(){
        databaseHelper = DatabaseHelper.getInstance(this);
        userInfoModel = databaseHelper.getUserInfoFromLocalDB(this);
    }

    private void setImageViewToGmailDP(){
        Glide.with(this)
                .load(userInfoModel.getUserDisplayPhoto())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        activitySettingsBinding.ivFragProfileDp.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        activitySettingsBinding.ivFragProfileDp.setVisibility(View.VISIBLE);
                        return false;
                    }
                }).into(activitySettingsBinding.ivFragProfileDp);
    }

    private void setViewsInfo(){
        activitySettingsBinding.tvSettingsUserName.setText(userInfoModel.getUserFullName());

        if (TextUtils.isEmpty(userInfoModel.getUserAddress())) activitySettingsBinding.tvSettingsUserAddress.setVisibility(View.GONE);
        else activitySettingsBinding.tvSettingsUserAddress.setText(userInfoModel.getUserAddress());

        activitySettingsBinding.tvSettingsSignout.setOnClickListener(v -> signOutAccount());

        activitySettingsBinding.rltSettingsAboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(this, AppWebviewActivity.class);
            intent.putExtra("web_view_type", 0);
            startActivity(intent);
        });

        activitySettingsBinding.rltSettingsTerms.setOnClickListener(v -> {
            Intent intent = new Intent(this, AppWebviewActivity.class);
            intent.putExtra("web_view_type", 1);
            startActivity(intent);
        });

        activitySettingsBinding.rltSettingsPrivacyPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(this, AppWebviewActivity.class);
            intent.putExtra("web_view_type", 2);
            startActivity(intent);
        });
    }

    private void signOutAccount(){
        Log.d(TAG, "Sign out account method");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.google_oauth))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent intent = new Intent(this, SigninSignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        sessionManager.clearCache();
        databaseHelper.dropTable(this);
        googleSignInClient.signOut();
        startActivity(intent);
    }
}