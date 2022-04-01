package com.fea.floodmapp.main.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fea.floodmapp.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding splashBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(splashBinding.getRoot());

        goFullScreen();
        navigateToSignIn();
    }

    private void goFullScreen(){
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void navigateToSignIn() {
        // Delay 2 seconds then go to Home
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent mainActivity = new Intent(this, SignInActivity.class);
            startActivity(mainActivity);
            finish();
        }, 2000);
    }

    private void navigateToHome() {
        // Delay 2 seconds then go to Home
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
            finish();
        }, 2000);
    }
}
