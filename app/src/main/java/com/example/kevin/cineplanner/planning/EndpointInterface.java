package com.example.kevin.cineplanner.planning;

import com.example.kevin.cineplanner.login.LoginModel;
import com.example.kevin.cineplanner.team.TeamModel;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Kevin on 20/09/2017 for ZKY.
 */

public interface EndpointInterface {
    @Headers("Content-Type: application/json")
    @POST("team/create")
    Call<TeamModel> createTeam(@Header("token") String login, @Body JsonObject jsonObject);

}
