package com.cineplanner.kevin.cineplanner.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cineplanner.kevin.cineplanner.BoxLoading;
import com.cineplanner.kevin.cineplanner.BuildConfig;
import com.cineplanner.kevin.cineplanner.movie.DialogMovie;
import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.login.LoginTools;
import com.cineplanner.kevin.cineplanner.planning.EndpointInterface;
import com.cineplanner.kevin.cineplanner.team.EventModel;
import com.cineplanner.kevin.cineplanner.util.NetworkUtils;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.cineplanner.kevin.cineplanner.util.NetworkUtils.setUiInProgress;

public class EventActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public static String DAY = "day";
    public static String MONTH = "month";
    public static String YEAR = "year";
    public static String MOVIE = "movie";
    public static String TEAM = "team";
    private AppCompatEditText name;
    public static AppCompatEditText movie;
    private AppCompatTextView startDate;
    private AppCompatTextView startHour;
    private AppCompatTextView endDate;
    private AppCompatTextView endHour;
    private AppCompatButton createBtn;
    private AppCompatImageButton search;
    private int day;
    private int month;
    private int year;
    private long id;
    private BoxLoading alert;
    private MovieModel movieModel;
    private static final String TAG = "EventActivity";
    private DatePickerDialog datePickerDialog;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public static MovieModel movieSelected;

    public static void setMovieSelected(MovieModel movieSelected) {
        EventActivity.movieSelected = movieSelected;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        name = (AppCompatEditText) findViewById(R.id.event_id_input);
        movie = (AppCompatEditText) findViewById(R.id.movie_id_input);
        startDate = (AppCompatTextView) findViewById(R.id.date_start);
        startHour = (AppCompatTextView) findViewById(R.id.hour_start);
        endDate = (AppCompatTextView) findViewById(R.id.date_end);
        endHour = (AppCompatTextView) findViewById(R.id.hour_end);
        createBtn = (AppCompatButton) findViewById(R.id.create);
        search = (AppCompatImageButton) findViewById(R.id.search_movie);

        alert = new BoxLoading();

        Bundle bundle = getIntent().getExtras();
        day = bundle.getInt(DAY);
        month = bundle.getInt(MONTH);
        year = bundle.getInt(YEAR);
        id = bundle.getLong(TEAM);
        if (bundle.containsKey(MOVIE)) {
            movieModel = (MovieModel) bundle.get(MOVIE);
            movieSelected = movieModel;
            movie.setText(movieModel.getTitle());
        }
        Log.d(TAG, "onCreate: " + id);
        Log.d(TAG, "onCreate:d " + day);
        Log.d(TAG, "onCreate:m " + month);
        Log.d(TAG, "onCreate:y " + year);
        datePickerDialog = new DatePickerDialog(
                this, this, year, month, day);

        startDate.setText(
                day + "/" + (month + 1) + "/"
                        + year);
        endDate.setText(
                day + "/" + (month + 1) + "/"
                        + year);
        startHour.setText("17:00");
        endHour.setText("20:00");
        startDate.setOnClickListener(this);
        startHour.setOnClickListener(this);
        endDate.setOnClickListener(this);
        endHour.setOnClickListener(this);
        createBtn.setOnClickListener(this);
        search.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.date_start:
                datePickerDialog.show();
                break;

            case R.id.date_end:
                datePickerDialog.show();
                break;
            case R.id.hour_start:
                int hour = 17;
                int minute = 0;
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        String time;
                        if (selectedMinute < 10) {
                            time = ":0" + selectedMinute;
                        } else {
                            time = ":" + selectedMinute;
                        }
                        if (selectedHour < 10) {
                            time = "0" + selectedHour + time;
                        } else {
                            time = selectedHour + time;
                        }
                        startHour.setText(time);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                break;
            case R.id.hour_end:
                int hour2 = 20;
                int minute2 = 0;
                TimePickerDialog mTimePicker2;
                mTimePicker2 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String time;
                        if (selectedMinute < 10) {
                            time = ":0" + selectedMinute;
                        } else {
                            time = ":" + selectedMinute;
                        }
                        if (selectedHour < 10) {
                            time = "0" + selectedHour + time;
                        } else {
                            time = selectedHour + time;
                        }
                        endHour.setText(time);
                        if (Integer.valueOf(startHour.getText().toString().replace(":", "")) >= Integer.valueOf(endHour.getText().toString().replace(":", ""))) {
                            endHour.setTextColor(Color.RED);
                        } else {
                            endHour.setTextColor(Color.BLACK);
                        }
                    }
                }, hour2, minute2, true);//Yes 24 hour time
                mTimePicker2.setTitle("Select Time");
                mTimePicker2.show();
                break;
            case R.id.create:

                if ((Integer.valueOf(startHour.getText().toString().replace(":", "")) >= Integer.valueOf(endHour.getText().toString().replace(":", "")))) {
                    endHour.setTextColor(Color.RED);
                    Toast.makeText(this, "L'événement doit finir après l'heure de commencement", Toast.LENGTH_SHORT).show();
                } else {
                    String nameEvent = name.getText().toString();
                    if (nameEvent.isEmpty()) {
                        Toast.makeText(this, "L'événement doit avoir un nom", Toast.LENGTH_SHORT).show();
                    } else {
                        String start = startDate.getText().toString() + " " + startHour.getText().toString();
                        Date startTime = new Date();
                        Log.d(TAG, "onClick: " + start);
                        try {
                            startTime = simpleDateFormat.parse(start);
                        } catch (ParseException ex) {
                            Log.d(TAG, "onClick: " + ex);
                        }
                        String end = endDate.getText().toString() + " " + endHour.getText().toString();
                        Log.d(TAG, "onClick: " + start);
                        Date endTime = new Date();
                        try {
                            endTime = simpleDateFormat.parse(end);
                        } catch (ParseException ex) {
                            Log.d(TAG, "onClick: " + ex);
                        }
                        if (endTime.getTime() != 0 && startTime.getTime() != 0 && !nameEvent.isEmpty()) {

                            setUiInProgress(getSupportFragmentManager(), alert, true);

                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("name", nameEvent);
                            jsonObject.addProperty("start", startTime.getTime());
                            jsonObject.addProperty("end", endTime.getTime());
                            jsonObject.addProperty("idTeam", id);
                            jsonObject.addProperty("idMovie", movieSelected.getId());
                            Log.d(TAG, "onClick: " + jsonObject);

                            String url = BuildConfig.URL;
                            Retrofit.Builder retrofit = new Retrofit.Builder()
                                    .client(NetworkUtils.client(getApplicationContext(), "event"))
                                    .baseUrl(url)
                                    .addConverterFactory(GsonConverterFactory.create());
                            EndpointInterface endpointInterface = retrofit.build().create(EndpointInterface.class);
                            Call<EventModel> call = endpointInterface.createEvent(LoginTools.getToken(getApplicationContext()), jsonObject);
                            call.enqueue(new Callback<EventModel>() {
                                @Override
                                public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                                    Log.d(TAG, "onResponse: " + response);
                                    if (response.isSuccessful()) {
                                        Toast.makeText(EventActivity.this, "Evénement ajouté", Toast.LENGTH_SHORT).show();

                                        setUiInProgress(getSupportFragmentManager(), alert, false);

                                        Intent intent = new Intent();
                                        intent.putExtra("event", response.body());
                                        setResult(2, intent);
                                        finish();

                                    } else {
                                        setUiInProgress(getSupportFragmentManager(), alert, false);

                                        Toast.makeText(EventActivity.this, response.raw().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<EventModel> call, Throwable t) {
                                    Log.d(TAG, "onFailure: " + t.getMessage());
                                    setUiInProgress(getSupportFragmentManager(), alert, false);

                                    Toast.makeText(EventActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        } else {
                            Toast.makeText(this, "L'événement n'a pas de date conforme", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.search_movie:
                if (!movie.getText().toString().isEmpty()) {
                    setUiInProgress(getSupportFragmentManager(), alert, true);

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("query", movie.getText().toString());
                    String url = BuildConfig.URL;
                    Retrofit.Builder retrofit = new Retrofit.Builder()
                            .client(NetworkUtils.client(getApplicationContext(), "event"))
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create());
                    EndpointInterface endpointInterface = retrofit.build().create(EndpointInterface.class);
                    Call<List<MovieModel>> call = endpointInterface.searchEvent(LoginTools.getToken(getApplicationContext()), jsonObject);
                    call.enqueue(new Callback<List<MovieModel>>() {
                        @Override
                        public void onResponse(Call<List<MovieModel>> call, Response<List<MovieModel>> response) {
                            Log.d(TAG, "onResponse: " + response);
                            if (response.isSuccessful()) {
                                setUiInProgress(getSupportFragmentManager(), alert, false);
                                DialogMovie dFragment = DialogMovie.newInstance(response.body());
                                dFragment.show(getSupportFragmentManager(), "tag");

                            } else {
                                setUiInProgress(getSupportFragmentManager(), alert, false);

                                Toast.makeText(EventActivity.this, response.raw().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<MovieModel>> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t.getMessage());
                            setUiInProgress(getSupportFragmentManager(), alert, false);

                            Toast.makeText(EventActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                } else {
                    Toast.makeText(this, "Recherche vide", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        startDate.setText(
                i2 + "/" + (i1 + 1) + "/"
                        + i);
        endDate.setText(
                i2 + "/" + (i1 + 1) + "/"
                        + i);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        movieSelected = null;
        super.onDestroy();
    }
}
