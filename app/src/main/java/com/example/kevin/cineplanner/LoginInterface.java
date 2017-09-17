package com.example.kevin.cineplanner;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Kevin on 17/09/2017 for ZKY.
 */

public interface LoginInterface {
    @POST("authenticate")
    Call<LoginModel> getLogin(@Header("login") String login, @Header("password") String password);
    // @GET("whoami")
    // @Headers({"X-Appscho-ServerId:154LWU"})
    // Call<LoginModel> getLogin();
}


