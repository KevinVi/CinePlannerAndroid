package com.cineplanner.kevin.cineplanner.movie;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.event.EventActivity;
import com.cineplanner.kevin.cineplanner.event.MovieModel;

/**
 * Created by Kevin on 24/09/2017 for ZKY.
 */

public class DialogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final AppCompatTextView titleMovie;


    public DialogViewHolder(View itemView) {
        super(itemView);
        titleMovie = itemView.findViewById(R.id.title_movie);
        itemView.setOnClickListener(this);
    }

    public AppCompatTextView getTitleMovie() {
        return titleMovie;
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), getTitleMovie().getText().toString(), Toast.LENGTH_SHORT).show();
        EventActivity.setMovieSelected((MovieModel) getTitleMovie().getTag());
        DialogMovie.getMyDialog().dismiss();
        EventActivity.movie.setText(getTitleMovie().getText().toString());
    }
}
