package com.cineplanner.kevin.cineplanner.login;

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
import android.widget.Button;
import android.widget.Toast;

import com.cineplanner.kevin.cineplanner.util.BoxLoading;
import com.cineplanner.kevin.cineplanner.BuildConfig;
import com.cineplanner.kevin.cineplanner.R;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.cineplanner.kevin.cineplanner.login.LoginActivity.getToken;
import static com.cineplanner.kevin.cineplanner.util.NetworkUtils.setUiInProgress;

/**
 * Created by Kevin on 01/10/2017 for CinePlanner.
 */

public class DialogCreate extends DialogFragment {
    private static final String TAG = "DialogCreate";
    private BoxLoading alert;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        alert = new BoxLoading();
        View rootView = View.inflate(getContext(), R.layout.dialog_create, null);
        final AppCompatEditText email = rootView.findViewById(R.id.login_id_input);
        final AppCompatEditText pass = rootView.findViewById(R.id.password_id_input);
        final AppCompatEditText name = rootView.findViewById(R.id.name_input);
        final AppCompatEditText lastname = rootView.findViewById(R.id.lastname_input);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                // Set Dialog Icon
                // Set Dialog Title
                .setTitle(R.string.add_team)
                // Set Dialog Message

                // Positive button
                .setPositiveButton("OK", null);

        alertDialogBuilder.setView(rootView);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something

                        final String username = email.getText().toString();
                        final String password = pass.getText().toString();
                        final String nameUser = name.getText().toString();
                        final String lastnameUser = lastname.getText().toString();
                        Log.d(TAG, "onClick: " + username);
                        if (!username.isEmpty() && !password.isEmpty() && !nameUser.isEmpty() && !lastnameUser.isEmpty()) {
                            setUiInProgress(getFragmentManager(), alert, true);

                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("login", username);
                            jsonObject.addProperty("password", password);
                            jsonObject.addProperty("firstName", nameUser);
                            jsonObject.addProperty("lastName", lastnameUser);


                            Log.d(TAG, "onClick: " + jsonObject);
                            String url = BuildConfig.URL + "account/";
                            OkHttpClient.Builder builder = new OkHttpClient.Builder();
                            final Context context = getContext();
                            Retrofit.Builder retrofit = new Retrofit.Builder()
                                    .client(builder.build())
                                    .baseUrl(url)
                                    .addConverterFactory(GsonConverterFactory.create());
                            LoginInterface endpointInterface = retrofit.build().create(LoginInterface.class);
                            Call<AccountModel> call = endpointInterface.createAccount(LoginTools.getToken(getContext()), jsonObject);
                            call.enqueue(new Callback<AccountModel>() {
                                @Override
                                public void onResponse(Call<AccountModel> call, Response<AccountModel> response) {
                                    Log.d(TAG, "onResponse: " + response);
                                    if (response.isSuccessful()) {
                                        getToken(getContext(), username, password, getFragmentManager(), alert, true);
                                    } else {
                                        Log.d(TAG, "onResponse: " + response.raw());
                                        setUiInProgress(getFragmentManager(), alert, false);
                                    }
                                }

                                @Override
                                public void onFailure(Call<AccountModel> call, Throwable t) {
                                    Log.d(TAG, "onFailure: " + t.getMessage());
                                    setUiInProgress(getFragmentManager(), alert, false);

                                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();

                        }
                        //Dismiss once everything is OK.
                        //   dialog.dismiss();
                    }
                });
            }
        });
        return alertDialog;
    }
}
