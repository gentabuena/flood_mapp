package com.fea.floodmapp.main.views;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.ActivityMainBinding;
import com.fea.floodmapp.main.utils.KeyboardUtil;
import com.fea.floodmapp.main.views.fragments.HomeFragment;
import com.fea.floodmapp.main.views.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;

    private final int tabHome = R.id.tab_home;
    private final int tabProfile = R.id.tab_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        initBottomNavigationView();
    }

    private void initBottomNavigationView() {
        HomeFragment homeFragment = new HomeFragment();
        ProfileFragment profileFragment = new ProfileFragment();

        activityMainBinding.mainNavigation.setOnItemSelectedListener(item -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
            int tabID = item.getItemId();
            switch(tabID){
                case tabHome:
                    if (currentFragment instanceof HomeFragment) {
                        return false;
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, homeFragment).commit();
                        Toast.makeText(MainActivity.this, "Home Tab was selected", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case tabProfile:
                    if (currentFragment instanceof ProfileFragment) {
                        return false;
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, profileFragment).commit();
                        Toast.makeText(MainActivity.this, "Profile Tab was selected", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            return true;
        });
    }
}