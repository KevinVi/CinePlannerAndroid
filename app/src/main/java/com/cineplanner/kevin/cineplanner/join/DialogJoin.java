package com.cineplanner.kevin.cineplanner.join;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.movie.RecyclerDialogAdapter;
import com.cineplanner.kevin.cineplanner.team.TeamModel;

import java.util.List;

/**
 * Created by Kevin on 03/10/2017 for CinePlanner.
 */

public class DialogJoin extends DialogFragment {


    private static Dialog myDialog;
    private RecyclerView mRecyclerView;
    private RecyclerDialogAdapter adapter;
    private List<TeamModel> penddingTeams;
    // this method create view for your Dialog

    public static DialogJoin newInstance(List<TeamModel> penddingTeams) {
        DialogJoin f = new DialogJoin();
        // Supply num input as an argument.
        f.setPenddingTeams(penddingTeams);
        return f;
    }

    public static Dialog getMyDialog() {
        return myDialog;
    }

    public void setPenddingTeams(List<TeamModel> movies) {
        this.penddingTeams = movies;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //inflate layout with recycler view

        View rootView = View.inflate(getContext(), R.layout.dialog_movie, null);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //setadapter
        RecyclerDialogJoinAdapter adapter = new RecyclerDialogJoinAdapter(penddingTeams);
        mRecyclerView.setAdapter(adapter);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle("Team")
                .setMessage("Click sur la team que tu veux rejoindre")
                .setView(rootView);


        //get your recycler view and populate it.
        myDialog = alertDialogBuilder.create();
        return myDialog;
    }
}
