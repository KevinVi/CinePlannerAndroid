package com.cineplanner.kevin.cineplanner.suggestion;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cineplanner.kevin.cineplanner.BoxLoading;
import com.cineplanner.kevin.cineplanner.BuildConfig;
import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.event.MovieModel;
import com.cineplanner.kevin.cineplanner.login.AccountModel;
import com.cineplanner.kevin.cineplanner.login.LoginInterface;
import com.cineplanner.kevin.cineplanner.login.LoginTools;
import com.cineplanner.kevin.cineplanner.movie.RecyclerDialogAdapter;
import com.cineplanner.kevin.cineplanner.planning.EndpointInterface;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.cineplanner.kevin.cineplanner.login.LoginActivity.getToken;
import static com.cineplanner.kevin.cineplanner.util.NetworkUtils.setUiInProgress;

/**
 * Created by Kevin on 03/10/2017 for ZKY.
 */

public class DialogLearning extends DialogFragment {

    private RecyclerView mRecyclerView;
    private RecyclerDialogAdapter adapter;
    private static final String TAG = "DialogLearning";
    private List<MovieModel> movieLearning;
    private static Dialog myDialog;
    private BoxLoading alert;
    public static HashMap<Integer, SuggestionModel> suggestionModels = new HashMap<>();
    private int teamId;
    // this method create view for your Dialog


    public static DialogLearning newInstance(List<MovieModel> movieLearning, int teamId) {
        DialogLearning f = new DialogLearning();
        // Supply num input as an argument.
        f.setMovieLearning(movieLearning);
        f.setTeamId(teamId);
        return f;
    }

    public void setMovieLearning(List<MovieModel> movieLearning) {
        this.movieLearning = movieLearning;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //inflate layout with recycler view

        alert = new BoxLoading();
        View rootView = View.inflate(getContext(), R.layout.dialog_suggestion, null);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //setadapter
        RecyclerDialogLearningAdapter adapter = new RecyclerDialogLearningAdapter(movieLearning, getContext(), teamId);
        mRecyclerView.setAdapter(adapter);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle("Suggestion")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!suggestionModels.isEmpty()) {
                            setUiInProgress(getFragmentManager(), alert, true);

                            JsonArray jsonArray = new JsonArray();
                            for(Map.Entry<Integer, SuggestionModel> entry : suggestionModels.entrySet()) {
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("idTeam", teamId);
                                jsonObject.addProperty("idMovie", entry.getValue().getIdMovie());
                                jsonObject.addProperty("like", entry.getValue().isLiked());

                                jsonArray.add(jsonObject);
                                // do what you have to do here
                                // In your case, an other loop.
                            }
                            JsonObject object = new JsonObject();
                            object.add("content", jsonArray);
                            Log.d(TAG, "onClick: " + object);
                            String url = BuildConfig.URL;
                            OkHttpClient.Builder builder = new OkHttpClient.Builder();
                            final Context context = getContext();
                            Retrofit.Builder retrofit = new Retrofit.Builder()
                                    .client(builder.build())
                                    .baseUrl(url)
                                    .addConverterFactory(GsonConverterFactory.create());
                            EndpointInterface endpointInterface = retrofit.build().create(EndpointInterface.class);
                            Call<Boolean> call = endpointInterface.learningResult(LoginTools.getToken(getContext()), object);
                            call.enqueue(new Callback<Boolean>() {
                                @Override
                                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                    Log.d(TAG, "onResponse: " + response);
                                    if (response.isSuccessful()) {
                                        setUiInProgress(getFragmentManager(), alert, false);
                                    } else {
                                        Log.d(TAG, "onResponse: " + response.raw());
                                        setUiInProgress(getFragmentManager(), alert, false);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Boolean> call, Throwable t) {
                                    Log.d(TAG, "onFailure: " + t.getMessage());
                                    setUiInProgress(getFragmentManager(), alert, false);

                                }
                            });
                        }
                    }
                })
                .setView(rootView);


        //get your recycler view and populate it.
        myDialog = alertDialogBuilder.create();
        return myDialog;
    }

    public static Dialog getMyDialog() {
        return myDialog;
    }
}
