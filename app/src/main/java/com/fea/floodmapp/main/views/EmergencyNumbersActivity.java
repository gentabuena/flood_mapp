package com.fea.floodmapp.main.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.ActivityEmergencyNumbersBinding;

public class EmergencyNumbersActivity extends AppCompatActivity {

    ActivityEmergencyNumbersBinding activityEmergencyNumbersBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEmergencyNumbersBinding = ActivityEmergencyNumbersBinding.inflate(getLayoutInflater());
        setContentView(activityEmergencyNumbersBinding.getRoot());

        overlayGradientImageViewOnStatusBar();
        setViews();
    }

    private void overlayGradientImageViewOnStatusBar(){
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        activityEmergencyNumbersBinding.ivEnumbersHeaderStatusBarOverlay.getLayoutParams().height = getStatusBarHeight();
        activityEmergencyNumbersBinding.ivEnumbersHeaderStatusBarOverlay.requestLayout();
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setViews() {
        activityEmergencyNumbersBinding.incEnumbersHeader.tvHeaderTitle.setText(getResources().getString(R.string.text_emergency_numbers));
        activityEmergencyNumbersBinding.incEnumbersHeader.ivHeaderBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}