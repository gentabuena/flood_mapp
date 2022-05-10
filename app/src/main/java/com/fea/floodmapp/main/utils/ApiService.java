package com.fea.floodmapp.main.utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("users")
    Call<ResponseBody> testAPI(@Query("token") String token);

    @GET("auth/login")
    Call<ResponseBody> signinUser(@Query("token") String token);

    @GET("auth/register")
    Call<ResponseBody> registerUser(@Query("token") String token);

    @GET("evacuations")
    Call<ResponseBody> getEvacuationSites(@Query("longitude") String longitude, @Query("latitude") String latitude);
}
