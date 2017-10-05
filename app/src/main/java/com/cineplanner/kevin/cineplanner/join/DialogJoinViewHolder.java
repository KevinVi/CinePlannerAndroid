package com.cineplanner.kevin.cineplanner.join;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.cineplanner.kevin.cineplanner.BuildConfig;
import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.login.LoginTools;
import com.cineplanner.kevin.cineplanner.planning.EndpointInterface;
import com.cineplanner.kevin.cineplanner.team.TeamModel;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.cineplanner.kevin.cineplanner.join.DialogJoin.getMyDialog;
import static com.cineplanner.kevin.cineplanner.planning.PlanningActivity.joinTeam;
import static com.cineplanner.kevin.cineplanner.planning.PlanningActivity.mDrawerList;
import static com.cineplanner.kevin.cineplanner.planning.PlanningActivity.myTeams;
import static com.cineplanner.kevin.cineplanner.planning.PlanningActivity.pendingTeams;

/**
 * Created by Kevin on 03/10/2017 for CinePlanner.
 */

public class DialogJoinViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "DialogViewHolder";
    private final AppCompatTextView teamName;
    private final AppCompatTextView teamCreator;


    public DialogJoinViewHolder(View itemView) {
        super(itemView);
        teamName = itemView.findViewById(R.id.team_name);
        teamCreator = itemView.findViewById(R.id.team_creator);
        itemView.setOnClickListener(this);
    }

    public AppCompatTextView getTeamName() {
        return teamName;
    }

    public AppCompatTextView getTeamCreator() {
        return teamCreator;
    }

    @Override
    public void onClick(View view) {

        TeamModel model = (TeamModel) teamName.getTag();
        myTeams.add((TeamModel) teamName.getTag());
        List<String> teamNames = new ArrayList<>();
        for (TeamModel t :
                myTeams) {
            teamNames.add(t.getName());
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_list_item_multiple_choice, teamNames);

        mDrawerList.setAdapter(arrayAdapter);
        mDrawerList.deferNotifyDataSetChanged();


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", model.getId());

        Context context = view.getContext();


        joinTeam.setText(context.getString(R.string.join_team) + "(" + (pendingTeams.size() - 1) + ")");
        Log.d(TAG, "onClick: " + jsonObject);
        String url = BuildConfig.URL + "team/";
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Retrofit.Builder retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create());
        EndpointInterface endpointInterface = retrofit.build().create(EndpointInterface.class);
        Call<Boolean> call = endpointInterface.join(LoginTools.getToken(context), jsonObject);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());

            }
        });
        getMyDialog().dismiss();

    }
}
