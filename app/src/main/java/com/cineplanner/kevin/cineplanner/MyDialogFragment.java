package com.cineplanner.kevin.cineplanner;

/**
 * Created by Kevin on 20/09/2017 for ZKY.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.cineplanner.kevin.cineplanner.login.LoginTools;
import com.cineplanner.kevin.cineplanner.planning.EndpointInterface;
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

public class MyDialogFragment extends DialogFragment {
    private static final String TAG = "MyDialogFragment";


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View rootView = View.inflate(getContext(), R.layout.dialog_fragment, null);
        final AppCompatEditText editText = rootView.findViewById(R.id.edit_team);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                // Set Dialog Icon
                // Set Dialog Title
                .setTitle(R.string.add_team)
                // Set Dialog Message

                // Positive button
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String teamName = editText.getText().toString();
                        if (!teamName.isEmpty()) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("name", teamName);


                            Log.d(TAG, "onClick: " + jsonObject);
                            String url = BuildConfig.URL+"team/";
                            OkHttpClient.Builder builder = new OkHttpClient.Builder();
                            final Context context = getContext();
                            Retrofit.Builder retrofit = new Retrofit.Builder()
                                    .client(builder.build())
                                    .baseUrl(url)
                                    .addConverterFactory(GsonConverterFactory.create());
                            EndpointInterface endpointInterface = retrofit.build().create(EndpointInterface.class);
                            Call<TeamModel> call = endpointInterface.createTeam(LoginTools.getToken(getContext()), jsonObject);
                            call.enqueue(new Callback<TeamModel>() {
                                @Override
                                public void onResponse(Call<TeamModel> call, Response<TeamModel> response) {
                                    Log.d(TAG, "onResponse: " + response);
                                    if (response.isSuccessful()) {
                                        List<String> array = new ArrayList<>();
                                        array.add(teamName);

                                        Log.d(TAG, "onResponse: " + context);
                                        ArrayAdapter arrayAdapter = new ArrayAdapter<>(context,
                                                android.R.layout.simple_list_item_multiple_choice, array);

                                        mDrawerList.setAdapter(arrayAdapter);
                                        mDrawerList.deferNotifyDataSetChanged();

                                    } else {
                                        Log.d(TAG, "onResponse: " + response.raw());
                                    }
                                }

                                @Override
                                public void onFailure(Call<TeamModel> call, Throwable t) {
                                    Log.d(TAG, "onFailure: " + t.getMessage());
                                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

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