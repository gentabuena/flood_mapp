package com.fea.floodmapp.main.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.ActivityGuidelineBinding;

public class GuidelineActivity extends AppCompatActivity {

    ActivityGuidelineBinding activityGuidelineBinding;
    String choice;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityGuidelineBinding = ActivityGuidelineBinding.inflate(getLayoutInflater());
        setContentView(activityGuidelineBinding.getRoot());
        overlayGradientImageViewOnStatusBar();
        getChoice();

        setViews();
    }

    /** private static class ViewStateAdapter extends FragmentStateAdapter {

        public ViewStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
             Hardcoded in this order, you'll want to use lists and make sure the titles match
            if (position == 0) {
                return new ParticularGuidelineFragment();
            }
            return new ParticularGuidelineFragment();
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    } **/

    private void overlayGradientImageViewOnStatusBar(){
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        activityGuidelineBinding.ivHeaderStatusBarOverlay.getLayoutParams().height = getStatusBarHeight();
        activityGuidelineBinding.ivHeaderStatusBarOverlay.requestLayout();
    }
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private void getChoice(){
        Intent intent = getIntent();
        choice = intent.getStringExtra("guideline_choice");
        Log.d("GuidelineActivity", "Show " + choice + " tab");

        activityGuidelineBinding.svGuideline.setVisibility(choice.equalsIgnoreCase("general") ? View.VISIBLE : View.GONE);
        activityGuidelineBinding.rltGuideline.setVisibility(choice.equalsIgnoreCase("particular") ? View.VISIBLE : View.GONE);
    }

    private void setViews(){
        activityGuidelineBinding.incHeader.tvHeaderTitle.setText(getResources().getString(choice.equals("general") ? R.string.text_general_gl : R.string.text_particular_gl));
        activityGuidelineBinding.incHeader.ivHeaderBack.setOnClickListener(v -> {
            onBackPressed();
        });

        /** ViewStateAdapter sa = new ViewStateAdapter(getSupportFragmentManager(), getLifecycle());
        sa.createFragment(0);
        sa.createFragment(1);
        activityGuidelineBinding.vpGuideline2.setAdapter(sa); */

        setGeneralGuidelinesWidthPerItem();

        floodLevelListener();
    }

    private void setGeneralGuidelinesWidthPerItem(){
        int width = getScreenWidth(this);
        activityGuidelineBinding.rltGuideline1.getLayoutParams().width = width;
        activityGuidelineBinding.rltGuideline2.getLayoutParams().width = width;
        activityGuidelineBinding.rltGuideline3.getLayoutParams().width = width;
        activityGuidelineBinding.rltGuideline4.getLayoutParams().width = width;
        activityGuidelineBinding.rltGuideline5.getLayoutParams().width = width;
        activityGuidelineBinding.rltGuideline6.getLayoutParams().width = width;
    }

    private void floodLevelListener(){
        activityGuidelineBinding.rltParticularGlLevel3.setOnClickListener(v ->{
            v.setEnabled(false); // disable for .5 seconds
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                v.setEnabled(true); // re-enable here
            }, 700);

            activityGuidelineBinding.svGlLevel3Container.setVisibility(View.VISIBLE);
            animation = AnimationUtils.loadAnimation(this, R.anim.slidein_left);
            activityGuidelineBinding.svGlLevel3Container.setAnimation(animation);
        });

        activityGuidelineBinding.rltParticularGlLevel2.setOnClickListener(v -> {
            v.setEnabled(false); // disable for .5 seconds
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                v.setEnabled(true); // re-enable here
            }, 700);

            activityGuidelineBinding.svGlLevel2Container.setVisibility(View.VISIBLE);
            animation = AnimationUtils.loadAnimation(this, R.anim.slidein_left);
            activityGuidelineBinding.svGlLevel2Container.setAnimation(animation);
        });

        activityGuidelineBinding.rltParticularGlLevel1.setOnClickListener(v -> {
            v.setEnabled(false); // disable for .5 seconds
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                v.setEnabled(true); // re-enable here
            }, 700);

            activityGuidelineBinding.svGlLevel1Container.setVisibility(View.VISIBLE);
            animation = AnimationUtils.loadAnimation(this, R.anim.slidein_left);
            activityGuidelineBinding.svGlLevel1Container.setAnimation(animation);
        });

        activityGuidelineBinding.lltGlLevel3Container.setOnClickListener(v -> {
            v.setEnabled(false); // disable for .5 seconds
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                v.setEnabled(true); // re-enable here
            }, 700);

            activityGuidelineBinding.svGlLevel3Container.setVisibility(View.GONE);
            animation = AnimationUtils.loadAnimation(this, R.anim.slideout_right);
            activityGuidelineBinding.svGlLevel3Container.setAnimation(animation);
        });

        activityGuidelineBinding.lltGlLevel2Container.setOnClickListener(v -> {
            v.setEnabled(false); // disable for .5 seconds
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                v.setEnabled(true); // re-enable here
            }, 700);

            activityGuidelineBinding.svGlLevel2Container.setVisibility(View.GONE);
            animation = AnimationUtils.loadAnimation(this, R.anim.slideout_right);
            activityGuidelineBinding.svGlLevel2Container.setAnimation(animation);
        });

        activityGuidelineBinding.lltGlLevel1Container.setOnClickListener(v -> {
            v.setEnabled(false); // disable for .5 seconds
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                v.setEnabled(true); // re-enable here
            }, 700);

            activityGuidelineBinding.svGlLevel1Container.setVisibility(View.GONE);
            animation = AnimationUtils.loadAnimation(this, R.anim.slideout_right);
            activityGuidelineBinding.svGlLevel1Container.setAnimation(animation);
        });
    }
}