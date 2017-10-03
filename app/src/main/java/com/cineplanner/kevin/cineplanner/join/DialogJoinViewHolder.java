package com.cineplanner.kevin.cineplanner.join;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.event.EventActivity;
import com.cineplanner.kevin.cineplanner.event.EventDetailActivity;
import com.cineplanner.kevin.cineplanner.event.MovieModel;
import com.cineplanner.kevin.cineplanner.movie.DialogMovie;
import com.cineplanner.kevin.cineplanner.team.TeamModel;

import java.util.ArrayList;
import java.util.List;

import static com.cineplanner.kevin.cineplanner.planning.PlanningActivity.mDrawerList;
import static com.cineplanner.kevin.cineplanner.planning.PlanningActivity.myTeams;

/**
 * Created by Kevin on 03/10/2017 for ZKY.
 */

public class DialogJoinViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final AppCompatTextView teamName;
    private final AppCompatTextView teamCreator;
    private static final String TAG = "DialogViewHolder";


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

    }
}
