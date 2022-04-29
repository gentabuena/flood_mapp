package com.fea.floodmapp.main.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.ActivityFloodPreparednessGuideBinding;

public class FloodPreparednessGuideActivity extends AppCompatActivity {

    ActivityFloodPreparednessGuideBinding activityFloodPreparednessGuideBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFloodPreparednessGuideBinding = ActivityFloodPreparednessGuideBinding.inflate(getLayoutInflater());
        setContentView(activityFloodPreparednessGuideBinding.getRoot());

        overlayGradientImageViewOnStatusBar();
        setViews();
        setMapHeight();
    }

    private void overlayGradientImageViewOnStatusBar(){
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        activityFloodPreparednessGuideBinding.ivHeaderStatusBarOverlay.getLayoutParams().height = getStatusBarHeight();
        activityFloodPreparednessGuideBinding.ivHeaderStatusBarOverlay.requestLayout();
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
        activityFloodPreparednessGuideBinding.incHeader.tvHeaderTitle.setText(getResources().getString(R.string.text_flood_prep_guide));
        activityFloodPreparednessGuideBinding.incHeader.ivHeaderBack.setOnClickListener(v -> {
            onBackPressed();
        });
        activityFloodPreparednessGuideBinding.tvFloodGeneral.setOnClickListener(v -> goToGuidelines("general"));
        activityFloodPreparednessGuideBinding.tvFloodParticular.setOnClickListener(v -> goToGuidelines("particular"));
    }

    private void setMapHeight(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        activityFloodPreparednessGuideBinding.ivFloodGlMap.getLayoutParams().height = (int) (height * 0.55);
        activityFloodPreparednessGuideBinding.ivFloodGlMap.requestLayout();
    }

    private void goToGuidelines(String choice){
        Intent intent = new Intent(this, GuidelineActivity.class);
        intent.putExtra("guideline_choice", choice);
        startActivity(intent);
    }
}