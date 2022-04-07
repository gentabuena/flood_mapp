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
}
