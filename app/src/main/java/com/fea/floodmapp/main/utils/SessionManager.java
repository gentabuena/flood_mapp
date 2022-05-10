package com.fea.floodmapp.main.utils;

import android.content.SharedPreferences;
import com.fea.floodmapp.main.dependencies.MyApp;
import javax.inject.Inject;

public class SessionManager {

    public @Inject
    SharedPreferences sharedPreferences;

    public SessionManager() {
       MyApp.getAppComponent().inject(this);
    }

    public String getToken() {
        return sharedPreferences.getString("token", "");
    }

    public void setToken(String token) {
        sharedPreferences.edit().putString("token", token).apply();
    }

    public String getBaseURL() {
        return sharedPreferences.getString("base_url", "");
    }

    public void setBaseURL(String baseUrl) {
        sharedPreferences.edit().putString("base_url", baseUrl).apply();
    }

    public boolean getIsProfileEditing() {
        return sharedPreferences.getBoolean("isProfileEditing", false);
    }

    public void setIsProfileEditing(boolean isProfileEditing) {
        sharedPreferences.edit().putBoolean("isProfileEditing", isProfileEditing).apply();
    }

    public int getIsProfileEditingv2() {
        return sharedPreferences.getInt("isProfileEditingv2", 0);
    }

    public void setIsProfileEditingv2(int isProfileEditingv2) {
        sharedPreferences.edit().putInt("isProfileEditingv2", isProfileEditingv2).apply();
    }

    public void clearCache(){
        sharedPreferences.edit().clear().apply();
    }

    public String getCurrentLat() {
        return sharedPreferences.getString("currentLat", "");
    }

    public void setCurrentLat(String currentLat) {
        if(currentLat!=null&&!currentLat.isEmpty())
            sharedPreferences.edit().putString("currentLat", currentLat).apply();
    }

    public String getCurrentLong() {
        return sharedPreferences.getString("currentLong", "");
    }

    public void setCurrentLong(String currentLong) {
        if(currentLong!=null&&!currentLong.isEmpty())
            sharedPreferences.edit().putString("currentLong", currentLong).apply();
    }

    public String getCity() {
        return sharedPreferences.getString("city", "");
    }

    public void setCity(String city) {
        sharedPreferences.edit().putString("city", city).apply();
    }
}
