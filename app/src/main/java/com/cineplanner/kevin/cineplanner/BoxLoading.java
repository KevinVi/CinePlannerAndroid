package com.cineplanner.kevin.cineplanner;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Kevin on 25/09/2017 for CinePlanner.
 */

public class BoxLoading extends DialogFragment {
    private ViewGroup viewGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getContext().setTheme(R.style.AppThemeDialog);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewGroup = container;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder OptionDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View convertView = inflater.inflate(R.layout.box_loading, viewGroup, false);
        AppCompatTextView textView = convertView.findViewById(R.id.title_box);
        textView.setText(getString(R.string.loading));
        textView.setTextColor(Color.BLACK);
        OptionDialog.setView(convertView);
        OptionDialog.setCancelable(false);
        return OptionDialog.create();
    }
}
