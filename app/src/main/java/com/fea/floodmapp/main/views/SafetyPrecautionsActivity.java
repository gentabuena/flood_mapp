package com.fea.floodmapp.main.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.ActivitySafetyPrecautionsBinding;

public class SafetyPrecautionsActivity extends AppCompatActivity {

    ActivitySafetyPrecautionsBinding activitySafetyPrecautionsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySafetyPrecautionsBinding = ActivitySafetyPrecautionsBinding.inflate(getLayoutInflater());
        setContentView(activitySafetyPrecautionsBinding.getRoot());

        overlayGradientImageViewOnStatusBar();
        setViews();
        scrollTo();
    }

    private void overlayGradientImageViewOnStatusBar(){
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        activitySafetyPrecautionsBinding.ivHeaderStatusBarOverlay.getLayoutParams().height = getStatusBarHeight();
        activitySafetyPrecautionsBinding.ivHeaderStatusBarOverlay.requestLayout();
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
        activitySafetyPrecautionsBinding.incHeader.tvHeaderTitle.setText(getResources().getString(R.string.text_safety_precautions));
        activitySafetyPrecautionsBinding.incHeader.ivHeaderBack.setOnClickListener(v -> {
            onBackPressed();
        });

        int width = getScreenWidth(this);
        activitySafetyPrecautionsBinding.cvSafetyBefore.getLayoutParams().width = width;
        activitySafetyPrecautionsBinding.cvSafetyWhenWarned.getLayoutParams().width = width;
        activitySafetyPrecautionsBinding.cvSafetyDuring.getLayoutParams().width = width;
    }

    private static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private void scrollTo(){
        HorizontalScrollView svFloodReminders = activitySafetyPrecautionsBinding.svSafetyFloodReminder;
        activitySafetyPrecautionsBinding.tvSafetyBeforeTitle.setOnClickListener(v ->{
            svFloodReminders.post(() -> svFloodReminders.smoothScrollTo(activitySafetyPrecautionsBinding.cvSafetyBefore.getLeft(), 0));
        });

        activitySafetyPrecautionsBinding.tvSafetyWarnedTitle.setOnClickListener(v ->{
            svFloodReminders.post(() -> svFloodReminders.smoothScrollTo(activitySafetyPrecautionsBinding.cvSafetyWhenWarned.getLeft(), 0));
        });

        activitySafetyPrecautionsBinding.tvSafetyDuringTitle.setOnClickListener(v ->{
            svFloodReminders.post(() -> svFloodReminders.smoothScrollTo(activitySafetyPrecautionsBinding.cvSafetyDuring.getLeft(), 0));
        });

        activitySafetyPrecautionsBinding.rltSafetyBeforeTitle.setOnClickListener(v ->{
            svFloodReminders.post(() -> svFloodReminders.smoothScrollTo(activitySafetyPrecautionsBinding.cvSafetyBefore.getLeft(), 0));
        });

        activitySafetyPrecautionsBinding.rltSafetyWarnedTitle.setOnClickListener(v ->{
            svFloodReminders.post(() -> svFloodReminders.smoothScrollTo(activitySafetyPrecautionsBinding.cvSafetyWhenWarned.getLeft(), 0));
        });

        activitySafetyPrecautionsBinding.rltSafetyDuringTitle.setOnClickListener(v ->{
            svFloodReminders.post(() -> svFloodReminders.smoothScrollTo(activitySafetyPrecautionsBinding.cvSafetyDuring.getLeft(), 0));
        });
    }
}