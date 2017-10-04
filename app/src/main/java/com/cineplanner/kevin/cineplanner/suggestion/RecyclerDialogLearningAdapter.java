package com.cineplanner.kevin.cineplanner.suggestion;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.event.MovieModel;
import com.cineplanner.kevin.cineplanner.join.DialogJoinViewHolder;

import java.util.ArrayList;
import java.util.List;

import static com.cineplanner.kevin.cineplanner.suggestion.DialogLearning.suggestionModels;

/**
 * Created by Kevin on 03/10/2017 for CinePlanner.
 */

public class RecyclerDialogLearningAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MovieModel> movies;
    private Context context;
    private int teamId;

    public RecyclerDialogLearningAdapter(List<MovieModel> movies, Context context, int teamId) {
        this.movies = movies;
        this.context = context;
        this.teamId = teamId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_suggestion_list, parent, false);
        return new DialogLearningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final DialogLearningViewHolder dialogLearningViewHolder = (DialogLearningViewHolder) holder;
        holder.setIsRecyclable(false);
        dialogLearningViewHolder.getTitleSug().setText(movies.get(position).getTitle());
        dialogLearningViewHolder.getDescSug().setText(movies.get(position).getOverview());

        if (suggestionModels.containsKey(movies.get(dialogLearningViewHolder.getAdapterPosition()).getId())) {
            dialogLearningViewHolder.getSugOk().setChecked(suggestionModels.get(movies.get(dialogLearningViewHolder.getAdapterPosition()).getId()).isLiked());
        } else {
            suggestionModels.put(movies.get(dialogLearningViewHolder.getAdapterPosition()).getId(), new SuggestionModel(teamId, movies.get(dialogLearningViewHolder.getAdapterPosition()).getId(), false));
        }
        dialogLearningViewHolder.getSugOk().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                suggestionModels.put(movies.get(dialogLearningViewHolder.getAdapterPosition()).getId(), new SuggestionModel(teamId, movies.get(dialogLearningViewHolder.getAdapterPosition()).getId(), b));

            }
        });
        Glide.with(context).load(movies.get(position).getPoster_path()).into(dialogLearningViewHolder.getImgSug());


    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}
