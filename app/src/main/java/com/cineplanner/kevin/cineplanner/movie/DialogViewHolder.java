package com.cineplanner.kevin.cineplanner.movie;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
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
    private final AppCompatTextView dateMovie;
    private final AppCompatImageView imageMovie;


    public DialogViewHolder(View itemView) {
        super(itemView);
        titleMovie = itemView.findViewById(R.id.title_movie);
        dateMovie = itemView.findViewById(R.id.date_movie);
        imageMovie = itemView.findViewById(R.id.image_movie);
        itemView.setOnClickListener(this);
    }

    public AppCompatTextView getTitleMovie() {
        return titleMovie;
    }

    public AppCompatTextView getDateMovie() {
        return dateMovie;
    }

    public AppCompatImageView getImageMovie() {
        return imageMovie;
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), getTitleMovie().getText().toString(), Toast.LENGTH_SHORT).show();
        EventActivity.setMovieSelected((MovieModel) getTitleMovie().getTag());
        DialogMovie.getMyDialog().dismiss();
        EventActivity.movie.setText(getTitleMovie().getText().toString());
    }
}
