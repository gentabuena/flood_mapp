package com.fea.floodmapp.main.utils;

import com.fea.floodmapp.main.dependencies.MyApp;
import com.google.gson.Gson;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiHelper {

    @Inject
    Gson gson;
    public Retrofit feaappApi;

    @Inject
    public ApiHelper(){
        MyApp.getAppComponent().inject(this);
        if (feaappApi == null) {
            feaappApi = new Retrofit.Builder().baseUrl("https://fea-app.herokuapp.com/api/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(loggingInterceptor);
        }
    }
}
