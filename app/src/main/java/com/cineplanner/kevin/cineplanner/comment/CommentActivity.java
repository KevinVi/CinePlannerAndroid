package com.cineplanner.kevin.cineplanner.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.cineplanner.kevin.cineplanner.util.BoxLoading;
import com.cineplanner.kevin.cineplanner.BuildConfig;
import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.login.LoginTools;
import com.cineplanner.kevin.cineplanner.planning.EndpointInterface;
import com.cineplanner.kevin.cineplanner.team.CommentModel;
import com.cineplanner.kevin.cineplanner.util.NetworkUtils;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.cineplanner.kevin.cineplanner.util.NetworkUtils.setUiInProgress;

/**
 * Created by Kevin on 02/10/2017 for CinePlanner.
 */

public class CommentActivity extends AppCompatActivity {


    private static final String TAG = "CommentActivity";
    public static String COMMENTS = "comments";
    public static String EVENTID = "eventid";
    AppCompatAutoCompleteTextView textIn;
    AppCompatButton add;
    List<String> oldComments = new ArrayList<>();
    ArrayList<CommentModel> commentModels = new ArrayList<>();
    LinearLayoutCompat container;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    ArrayAdapter<String> adapter;
    private BoxLoading alert;
    private long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        alert = new BoxLoading();
        Bundle bundle = getIntent().getExtras();
        commentModels = (ArrayList<CommentModel>) bundle.getSerializable(COMMENTS);
        eventId = bundle.getLong(EVENTID);


        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, oldComments);

        textIn = (AppCompatAutoCompleteTextView) findViewById(R.id.textin);
        textIn.setAdapter(adapter);
        container = (LinearLayoutCompat) findViewById(R.id.container);
        add = (AppCompatButton) findViewById(R.id.add);

        for (CommentModel com :
                commentModels) {
            oldComments.add(com.getComment());
            LayoutInflater layoutInflater =
                    (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.row_comment, null);
            AutoCompleteTextView comment = addView.findViewById(R.id.comment);
            AppCompatTextView creator = addView.findViewById(R.id.creator_comment);
            AppCompatTextView cate = addView.findViewById(R.id.date_comment);

            Log.d(TAG, "onCreate: " + com.toString());
            Log.d(TAG, "onCreate: " + creator);
//            comment.setAdapter(adapter);
            comment.setText(com.getComment());

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(com.getDateCreated()));
            String date = simpleDateFormat.format(calendar.getTime());
            creator.setText(com.getAuthor());
            cate.setText(date);
            container.addView(addView);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!textIn.getText().toString().isEmpty()) {
                    setUiInProgress(getSupportFragmentManager(), alert, true);

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("comment", textIn.getText().toString());
                    jsonObject.addProperty("idEvent", eventId);
                    String url = BuildConfig.URL;
                    Retrofit.Builder retrofit = new Retrofit.Builder()
                            .client(NetworkUtils.client(getApplicationContext(), "comment"))
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create());
                    EndpointInterface endpointInterface = retrofit.build().create(EndpointInterface.class);
                    Call<CommentModel> call = endpointInterface.comment(LoginTools.getToken(getApplicationContext()), jsonObject);
                    call.enqueue(new Callback<CommentModel>() {
                        @Override
                        public void onResponse(@NonNull Call<CommentModel> call, @NonNull Response<CommentModel> response) {
                            Log.d(TAG, "onResponse: " + response);
                            if (response.isSuccessful()) {
                                setUiInProgress(getSupportFragmentManager(), alert, false);


                                commentModels.add(response.body());
                                CommentModel com = response.body();
                                LayoutInflater layoutInflater =
                                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final View addView = layoutInflater.inflate(R.layout.row_comment, null);
                                AutoCompleteTextView comment = addView.findViewById(R.id.comment);
                                AppCompatTextView creator = addView.findViewById(R.id.creator_comment);
                                AppCompatTextView cate = addView.findViewById(R.id.date_comment);
                                comment.setText(com.getComment());
                                creator.setText(com.getAuthor());

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(Long.valueOf(com.getDateCreated()));
                                String date = simpleDateFormat.format(calendar.getTime());
                                cate.setText(date);
                                container.addView(addView);
                            } else {
                                Toast.makeText(CommentActivity.this, response.raw().toString(), Toast.LENGTH_SHORT).show();
                                setUiInProgress(getSupportFragmentManager(), alert, false);
                            }
                        }

                        @Override
                        public void onFailure(Call<CommentModel> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t.getMessage());
                            Toast.makeText(CommentActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            setUiInProgress(getSupportFragmentManager(), alert, false);

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Commentaire vide", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

//    LayoutInflater layoutInflater =
//            (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    final View addView = layoutInflater.inflate(R.layout.row_comment, null);
//
//    AutoCompleteTextView textOut = addView.findViewById(R.id.textAdded);
//                textOut.setAdapter(adapter);
//    final String nameInvite = textIn.getText().toString();
//                textOut.setText(textIn.getText().toString());
//                textIn.setText("");
//                invit.add(nameInvite);
//                container.addView(addView);


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("comments", commentModels);
        setResult(5, intent);
        finish();
        super.onBackPressed();
    }
}
