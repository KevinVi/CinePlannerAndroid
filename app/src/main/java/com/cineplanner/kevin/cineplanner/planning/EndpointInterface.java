package com.cineplanner.kevin.cineplanner.planning;

import com.cineplanner.kevin.cineplanner.event.MovieModel;
import com.cineplanner.kevin.cineplanner.team.EventModel;
import com.cineplanner.kevin.cineplanner.team.NotationModel;
import com.cineplanner.kevin.cineplanner.team.TeamModel;
import com.google.gson.JsonObject;

import java.util.List;

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
    @POST("create")
    Call<TeamModel> createTeam(@Header("token") String login, @Body JsonObject jsonObject);

    @POST("teams")
    Call<List<TeamModel>> getTeams(@Header("token") String login);

    @Headers("Content-Type: application/json")
    @POST("event/create")
    Call<EventModel> createEvent(@Header("token") String login, @Body JsonObject jsonObject);

    @Headers("Content-Type: application/json")
    @POST("event/update")
    Call<EventModel> updateEvent(@Header("token") String login, @Body JsonObject jsonObject);

    @Headers("Content-Type: application/json")
    @POST("event/search")
    Call<List<MovieModel>> searchEvent(@Header("token") String login, @Body JsonObject jsonObject);

    @Headers("Content-Type: application/json")
    @POST("notation/create")
    Call<NotationModel> notation(@Header("token") String login, @Body JsonObject jsonObject);
}
