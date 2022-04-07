package com.fea.floodmapp.main.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.fea.floodmapp.databinding.ActivitySignInBinding;
import com.fea.floodmapp.main.dependencies.MyApp;
import com.fea.floodmapp.main.utils.KeyboardUtil;
import com.fea.floodmapp.main.utils.SessionManager;

import javax.inject.Inject;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding signInBinding;

    @Inject
    SessionManager sessionManager;

    static String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(signInBinding.getRoot());

        MyApp.getAppComponent().inject(this);

        clickHandlers();
        overlayImageOnStatusBar();

        Log.d(TAG, "Base URL: " + sessionManager.getBaseURL());
    }

    private void clickHandlers() {
        signInBinding.tvSigninForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Test Click", Toast.LENGTH_SHORT).show();
        });

        signInBinding.tvSigninLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Test Click", Toast.LENGTH_SHORT).show();
        });
    }

    private void overlayImageOnStatusBar(){
        // Handle Keyboard Issue when on Fullscreen
        new KeyboardUtil(this, signInBinding.getRoot());

        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}