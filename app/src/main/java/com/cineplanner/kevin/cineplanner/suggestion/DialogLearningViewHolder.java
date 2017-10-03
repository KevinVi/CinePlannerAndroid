package com.cineplanner.kevin.cineplanner.suggestion;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import com.cineplanner.kevin.cineplanner.R;

/**
 * Created by Kevin on 03/10/2017 for ZKY.
 */

public class DialogLearningViewHolder extends RecyclerView.ViewHolder {


    private final AppCompatImageView imgSug;
    private final AppCompatTextView titleSug;
    private final AppCompatTextView descSug;
    private final SwitchCompat sugOk;
    private static final String TAG = "DialogViewHolder";

    public DialogLearningViewHolder(View itemView) {
        super(itemView);
        titleSug = itemView.findViewById(R.id.title_sug);
        descSug = itemView.findViewById(R.id.description_sug);
        imgSug = itemView.findViewById(R.id.image_sug);
        sugOk = itemView.findViewById(R.id.switch_sug);
    }

    public AppCompatImageView getImgSug() {
        return imgSug;
    }

    public AppCompatTextView getTitleSug() {
        return titleSug;
    }

    public AppCompatTextView getDescSug() {
        return descSug;
    }

    public SwitchCompat getSugOk() {
        return sugOk;
    }
}
