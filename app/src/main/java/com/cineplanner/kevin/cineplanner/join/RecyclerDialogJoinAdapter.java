package com.cineplanner.kevin.cineplanner.join;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.movie.DialogViewHolder;
import com.cineplanner.kevin.cineplanner.team.TeamModel;

import java.util.List;

/**
 * Created by Kevin on 03/10/2017 for ZKY.
 */

public class RecyclerDialogJoinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TeamModel> teams;

    public RecyclerDialogJoinAdapter(List<TeamModel> teams) {
        this.teams = teams;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_join, parent, false);
        return new DialogJoinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DialogJoinViewHolder dialogJoinViewHolder = (DialogJoinViewHolder) holder;
        dialogJoinViewHolder.getTeamCreator().setText(teams.get(position).getCreator());
        dialogJoinViewHolder.getTeamName().setText(teams.get(position).getName());
        dialogJoinViewHolder.getTeamName().setTag(teams.get(position));
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }
}
