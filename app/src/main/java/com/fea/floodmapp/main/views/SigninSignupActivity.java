package com.fea.floodmapp.main.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fea.floodmapp.databinding.ActivitySigninSignupBinding;
import com.fea.floodmapp.main.dependencies.MyApp;
import com.fea.floodmapp.main.utils.KeyboardUtil;
import com.fea.floodmapp.main.utils.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import javax.inject.Inject;

public class SigninSignupActivity extends AppCompatActivity {

    ActivitySigninSignupBinding activitySigninSignupBinding;
    private GoogleSignInClient googleSignInClient;
    static String TAG = "SignInActivity";

    @Inject
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySigninSignupBinding = ActivitySigninSignupBinding.inflate(getLayoutInflater());
        setContentView(activitySigninSignupBinding.getRoot());

        MyApp.getAppComponent().inject(this);
        overlayImageOnStatusBar();
        clickHandlers();
        setBaseURL();
    }

    private void overlayImageOnStatusBar(){
        // Handle Keyboard Issue when on Fullscreen
        new KeyboardUtil(this, activitySigninSignupBinding.getRoot());

        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void clickHandlers() {
        activitySigninSignupBinding.rltSigninupLogin.setOnClickListener(v -> openFeaHomeScreen());

        activitySigninSignupBinding.rltSigninupRegister.setOnClickListener(v -> {
            Toast.makeText(this, "Sign up was Clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void openFeaHomeScreen(){
        sessionManager.setToken("asdfghjklqwevsdgwbfvdzcxcsdxada");
        Intent mainActivity = new Intent(this, MainActivity .class);
        startActivity(mainActivity);
        finish();
    }

    private void setBaseURL(){
        sessionManager.setBaseURL("Heheh");
    }
}
