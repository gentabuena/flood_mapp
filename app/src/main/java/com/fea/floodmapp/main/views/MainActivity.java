package com.fea.floodmapp.main.views;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fea.floodmapp.databinding.ActivityMainBinding;
import com.fea.floodmapp.main.utils.KeyboardUtil;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        overlayImageOnStatusBar();
        clickHandlers();
    }

    private void clickHandlers() {
        binding.tvSigninForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Test Click", Toast.LENGTH_SHORT).show();
        });

        binding.tvSigninLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Test Click", Toast.LENGTH_SHORT).show();
        });
    }

    private void overlayImageOnStatusBar(){
        // Handle Keyboard Issue when on Fullscreen
        new KeyboardUtil(this, binding.getRoot());

        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}