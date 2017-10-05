package com.cineplanner.kevin.cineplanner.movie;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.event.MovieModel;

import java.util.List;

/**
 * Created by Kevin on 24/09/2017 for CinePlanner.
 */

public class DialogMovie extends DialogFragment {
    private static Dialog myDialog;
    private RecyclerView mRecyclerView;
    private RecyclerDialogAdapter adapter;
    private List<MovieModel> movies;
    // this method create view for your Dialog

    public static DialogMovie newInstance(List<MovieModel> movies) {
        DialogMovie f = new DialogMovie();
        // Supply num input as an argument.
        f.setMovies(movies);
        return f;
    }

    public static Dialog getMyDialog() {
        return myDialog;
    }

    public void setMovies(List<MovieModel> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //inflate layout with recycler view

        View rootView = View.inflate(getContext(), R.layout.dialog_movie, null);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //setadapter
        RecyclerDialogAdapter adapter = new RecyclerDialogAdapter(movies, getContext());
        mRecyclerView.setAdapter(adapter);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle("Films")
                .setView(rootView);


        //get your recycler view and populate it.
        myDialog = alertDialogBuilder.create();
        return myDialog;
    }
}