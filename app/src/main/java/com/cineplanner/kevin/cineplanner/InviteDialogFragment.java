package com.cineplanner.kevin.cineplanner;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.cineplanner.kevin.cineplanner.login.LoginTools;
import com.cineplanner.kevin.cineplanner.planning.EndpointInterface;
import com.cineplanner.kevin.cineplanner.planning.PlanningActivity;
import com.cineplanner.kevin.cineplanner.team.TeamModel;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.cineplanner.kevin.cineplanner.planning.PlanningActivity.mDrawerList;
import static com.cineplanner.kevin.cineplanner.planning.PlanningActivity.myTeams;

/**
 * Created by Kevin on 03/10/2017 for CinePlanner.
 */

public class InviteDialogFragment extends DialogFragment {

    private static final String TAG = "InviteDialogFragment";

    private TeamModel myTeam;
    private static Dialog myDialog;
    private PlanningActivity activity;

    String inMyTeam = "";
    // this method create view for your Dialog


    public static InviteDialogFragment newInstance(TeamModel teamModel, PlanningActivity activity) {
        InviteDialogFragment f = new InviteDialogFragment();

        // Supply num input as an argument.
        f.setMyTeam(teamModel);
        f.setActivity(activity);

        return f;
    }

    public void setMyTeam(TeamModel myTeam) {
        this.myTeam = myTeam;
    }

    public void setActivity(PlanningActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View rootView = View.inflate(getContext(), R.layout.fragment_invite, null);
        final AppCompatEditText editText = rootView.findViewById(R.id.email_team);
        final AppCompatTextView pending = rootView.findViewById(R.id.pending);

        for (String s :
                myTeam.getPendingUsers()) {
            if (inMyTeam.isEmpty()) {
                inMyTeam += s;
            } else {
                inMyTeam += "\n" + s;
            }
        }
        if (inMyTeam.isEmpty()) {
            inMyTeam = "Aucune";
        }
        pending.setText(inMyTeam);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                // Set Dialog Icon
                // Set Dialog Title
                .setTitle(R.string.invite_team)
                // Set Dialog Message

                // Positive button
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String invitedName = editText.getText().toString();
                        if (!invitedName.isEmpty() && !inMyTeam.contains(invitedName)) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("string", invitedName.toLowerCase().trim());
                            jsonObject.addProperty("teamId", myTeam.getId());


                            Log.d(TAG, "onClick: " + jsonObject);
                            String url = BuildConfig.URL + "team/";
                            OkHttpClient.Builder builder = new OkHttpClient.Builder();
                            final Context context = getContext();
                            Retrofit.Builder retrofit = new Retrofit.Builder()
                                    .client(builder.build())
                                    .baseUrl(url)
                                    .addConverterFactory(GsonConverterFactory.create());
                            EndpointInterface endpointInterface = retrofit.build().create(EndpointInterface.class);
                            Call<Boolean> call = endpointInterface.invite(LoginTools.getToken(getContext()), jsonObject);
                            call.enqueue(new Callback<Boolean>() {
                                @Override
                                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                    Log.d(TAG, "onResponse: " + response);
                                    if (response.isSuccessful()) {
                                        Toast.makeText(activity, "Invitation envoyé", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Boolean> call, Throwable t) {
                                    Log.d(TAG, "onFailure: " + t.getMessage());
                                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                })

                // Negative Button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialogBuilder.setView(rootView);
        return alertDialogBuilder.create();
    }
}
