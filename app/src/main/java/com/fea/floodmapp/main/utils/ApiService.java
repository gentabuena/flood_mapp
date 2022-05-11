package com.fea.floodmapp.main.utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("users")
    Call<ResponseBody> testAPI(@Query("token") String token);

    @GET("auth/login")
    Call<ResponseBody> signinUser(@Query("email") String email);

    @GET("auth/register")
    Call<ResponseBody> registerUser(@Query("email") String userEmail);

    @GET("evacuations")
    Call<ResponseBody> getEvacuationSites(@Query("longitude") String longitude, @Query("latitude") String latitude);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @PUT("profile")
    Call<ResponseBody> updateProfile(@Header("Authorization") String token,
                                     @Query("name") String name,
                                     @Query("address") String address,
                                     @Query("age") int age,
                                     @Query("gender") String gender,
                                     @Query("contact") String contact);
}
