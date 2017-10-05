package com.cineplanner.kevin.cineplanner.suggeestionDisplay;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.cineplanner.kevin.cineplanner.util.BoxLoading;
import com.cineplanner.kevin.cineplanner.BuildConfig;
import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.event.EventActivity;
import com.cineplanner.kevin.cineplanner.event.MovieModel;
import com.cineplanner.kevin.cineplanner.login.LoginTools;
import com.cineplanner.kevin.cineplanner.planning.EndpointInterface;
import com.cineplanner.kevin.cineplanner.planning.PlanningActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.cineplanner.kevin.cineplanner.event.EventActivity.DAY;
import static com.cineplanner.kevin.cineplanner.event.EventActivity.MONTH;
import static com.cineplanner.kevin.cineplanner.event.EventActivity.MOVIE;
import static com.cineplanner.kevin.cineplanner.event.EventActivity.YEAR;
import static com.cineplanner.kevin.cineplanner.util.NetworkUtils.setUiInProgress;

/**
 * Created by Kevin on 04/10/2017 for CinePlanner.
 */

public class DialogLearningDisplay extends DialogFragment {
    private static final String TAG = "DialogLearningDisplay";
    private static Dialog myDialog;
    private MovieModel movie;
    private BoxLoading alert;
    private int teamId;
    private PlanningActivity activity;
    // this method create view for your Dialog


    public static DialogLearningDisplay newInstance(MovieModel movie, int teamId, PlanningActivity activity) {
        DialogLearningDisplay f = new DialogLearningDisplay();
        Log.d(TAG, "newInstance: ");
        // Supply num input as an argument.
        f.setMovie(movie);
        f.setTeamId(teamId);
        f.setActivity(activity);
        return f;
    }

    public void setMovie(MovieModel movie) {
        this.movie = movie;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public void setActivity(PlanningActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.d(TAG, "onCreateDialog: learning");
        alert = new BoxLoading();
        View rootView = View.inflate(getContext(), R.layout.dialog_suggestion_display, null);
        AppCompatTextView title = rootView.findViewById(R.id.title_sug);
        AppCompatTextView description = rootView.findViewById(R.id.description_sug);
        AppCompatTextView release = rootView.findViewById(R.id.date);
        AppCompatImageView img = rootView.findViewById(R.id.image_sug);

        title.setText(movie.getTitle());
        if (movie.getOverview().isEmpty()) {
            description.setText("Aucune description disponible");
        } else {
            description.setText(movie.getOverview());
        }
        Glide.with(getContext()).load(movie.getPoster_path()).into(img);
        release.setText(movie.getRelease_date());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle("Suggestion")
                .setPositiveButton("Ajouter !", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(activity, EventActivity.class);
                        String[] tabDate = movie.getRelease_date().split("-");
                        Log.d(TAG, "onClick: " + tabDate.length);
                        Log.d(TAG, "onClick: " + tabDate[0]);
                        for (String s :
                                tabDate) {
                            Log.d(TAG, "onClick: " + s);
                        }
                        intent.putExtra(MONTH, Integer.valueOf(tabDate[1]));
                        intent.putExtra(DAY, Integer.valueOf(tabDate[2]));
                        intent.putExtra(YEAR, Integer.valueOf(tabDate[0]));
                        intent.putExtra(MOVIE, movie);
                        intent.putExtra(EventActivity.TEAM, Long.valueOf(teamId));
                        activity.startActivityForResult(intent, 2);
                    }
                })
                .setNegativeButton("Pas interessé", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setUiInProgress(getFragmentManager(), alert, true);

                        JsonArray jsonArray = new JsonArray();
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("idTeam", teamId);
                        jsonObject.addProperty("idMovie", movie.getId());
                        jsonObject.addProperty("liked", false);

                        jsonArray.add(jsonObject);
                        // do what you have to do here
                        // In your case, an other loop.

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
                })
                .setView(rootView);


        //get your recycler view and populate it.
        myDialog = alertDialogBuilder.create();
        return myDialog;

    }
}
