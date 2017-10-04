package com.cineplanner.kevin.cineplanner.movie;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.event.MovieModel;

import java.util.List;

/**
 * Created by Kevin on 24/09/2017 for CinePlanner.
 */

public class RecyclerDialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MovieModel> movies;
    private Context context;

    public RecyclerDialogAdapter(List<MovieModel> movies, Context context) {
        this.movies = movies;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_row, parent, false);
        return new DialogViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DialogViewHolder dialogViewHolder = (DialogViewHolder) holder;
        dialogViewHolder.getTitleMovie().setText(movies.get(position).getTitle());
        dialogViewHolder.getDateMovie().setText(movies.get(position).getRelease_date());
        Glide.with(context).load(movies.get(position).getPoster_path()).into(dialogViewHolder.getImageMovie());
        dialogViewHolder.getTitleMovie().setTag(movies.get(position));

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}
