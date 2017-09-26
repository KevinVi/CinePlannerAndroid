package com.cineplanner.kevin.cineplanner.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cineplanner.kevin.cineplanner.BuildConfig;
import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.login.LoginTools;
import com.cineplanner.kevin.cineplanner.movie.DialogMovie;
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

public class EventDetailActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public static String EVENT = "event";
    public static String TEAM = "team";
    public static String NAMETEAM = "nameTeam";
    private AppCompatEditText name;
    private AppCompatTextView startDate;
    private AppCompatTextView startHour;
    private AppCompatTextView endDate;
    private AppCompatTextView endHour;
    private AppCompatTextView team;
    private AppCompatButton update;
    private AppCompatButton delete;
    private AppCompatImageButton search;
    public static AppCompatEditText movie;
    private int day;
    private int month;
    private int year;
    private String nameTeam;
    private EventModel eventModel;
    private long id;
    private DatePickerDialog datePickerDialog;
    Calendar cal;
    Calendar cal2;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
    private static final String TAG = "EventDetailActivity";
    public static MovieModel movieSelected;

    public static void setMovieSelected(MovieModel movieSelected) {
        EventActivity.movieSelected = movieSelected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        name = (AppCompatEditText) findViewById(R.id.name_input);
        movie = (AppCompatEditText) findViewById(R.id.movie_id_input);
        startDate = (AppCompatTextView) findViewById(R.id.date_start);
        startHour = (AppCompatTextView) findViewById(R.id.hour_start);
        endDate = (AppCompatTextView) findViewById(R.id.date_end);
        endHour = (AppCompatTextView) findViewById(R.id.hour_end);
        team = (AppCompatTextView) findViewById(R.id.group_name);
        update = (AppCompatButton) findViewById(R.id.update);
        delete = (AppCompatButton) findViewById(R.id.delete);
        search = (AppCompatImageButton) findViewById(R.id.search_movie);

        Bundle bundle = getIntent().getExtras();


        eventModel = (EventModel) bundle.getSerializable(EVENT);
        id = bundle.getLong(TEAM);
        nameTeam = bundle.getString(NAMETEAM);
        team.setText(nameTeam);
        name.setText(eventModel.getName());
        cal = Calendar.getInstance();
        cal.setTimeInMillis(eventModel.getStart());

        cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(eventModel.getEnd());
        datePickerDialog = new DatePickerDialog(
                this, this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        startDate.setText(
                cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/"
                        + cal.get(Calendar.YEAR));
        endDate.setText(
                cal2.get(Calendar.DAY_OF_MONTH) + "/" + (cal2.get(Calendar.MONTH) + 1) + "/"
                        + cal2.get(Calendar.YEAR));

        String time;
        if (cal.get(Calendar.MINUTE) < 10) {
            time = ":0" + cal.get(Calendar.MINUTE);
        } else {
            time = ":" + cal.get(Calendar.MINUTE);
        }
        if (cal.get(Calendar.HOUR_OF_DAY) < 10) {
            time = "0" + cal.get(Calendar.HOUR_OF_DAY) + time;
        } else {
            time = cal.get(Calendar.HOUR_OF_DAY) + time;
        }
        startHour.setText(time);

        if (cal2.get(Calendar.MINUTE) < 10) {
            time = ":0" + cal2.get(Calendar.MINUTE);
        } else {
            time = ":" + cal2.get(Calendar.MINUTE);
        }
        if (cal2.get(Calendar.HOUR_OF_DAY) < 10) {
            time = "0" + cal2.get(Calendar.HOUR_OF_DAY) + time;
        } else {
            time = cal2.get(Calendar.HOUR_OF_DAY) + time;
        }
        endHour.setText(time);

        if (eventModel.getMovie() != null) {
            movie.setText(eventModel.getMovie().getTitle());
        }

        startDate.setOnClickListener(this);
        startHour.setOnClickListener(this);
        endDate.setOnClickListener(this);
        endHour.setOnClickListener(this);
        update.setOnClickListener(this);
        delete.setOnClickListener(this);
        search.setOnClickListener(this);

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.date_start:
                datePickerDialog.show();
                break;

            case R.id.date_end:
                datePickerDialog.show();
                break;
            case R.id.hour_start:
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
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
                int hour2 = cal2.get(Calendar.HOUR_OF_DAY);
                int minute2 = cal2.get(Calendar.MINUTE);
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
            case R.id.update:
                if ((Integer.valueOf(startHour.getText().toString().replace(":", "")) >= Integer.valueOf(endHour.getText().toString().replace(":", "")))) {
                    endHour.setTextColor(Color.RED);
                    Toast.makeText(this, "L'événement doit finir après l'heure de commencement", Toast.LENGTH_SHORT).show();
                } else {
                    String nameEvent = name.getText().toString();
                    if (nameEvent.isEmpty()) {
                        Toast.makeText(this, "L'événement doit avoir un nom", Toast.LENGTH_SHORT).show();
                    } else {
                        String start = startDate + " " + startHour;
                        Date startTime = new Date();
                        Log.d(TAG, "onClick: " + start);
                        try {
                            startTime = simpleDateFormat.parse(start);
                        } catch (ParseException ex) {
                            System.out.println("Exception " + ex);
                        }
                        String end = endDate + " " + endHour;
                        Log.d(TAG, "onClick: " + start);
                        Date endTime = new Date();
                        try {
                            endTime = simpleDateFormat.parse(end);
                        } catch (ParseException ex) {
                            System.out.println("Exception " + ex);
                        }
                        if (endTime.getTime() != 0 && startTime.getTime() != 0 && !nameEvent.isEmpty()) {


                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("name", nameEvent);
                            jsonObject.addProperty("start", startTime.getTime());
                            jsonObject.addProperty("end", endTime.getTime());
                            jsonObject.addProperty("id", eventModel.getId());
                            Log.d(TAG, "onClick: " + jsonObject);

                            String url = BuildConfig.URL;
                            Retrofit.Builder retrofit = new Retrofit.Builder()
                                    .client(NetworkUtils.client(getApplicationContext(), "event"))
                                    .baseUrl(url)
                                    .addConverterFactory(GsonConverterFactory.create());
                            EndpointInterface endpointInterface = retrofit.build().create(EndpointInterface.class);
                            Call<EventModel> call = endpointInterface.updateEvent(LoginTools.getToken(getApplicationContext()), jsonObject);
                            call.enqueue(new Callback<EventModel>() {
                                @Override
                                public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                                    Log.d(TAG, "onResponse: " + response);
                                    if (response.isSuccessful()) {
                                        Toast.makeText(EventDetailActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent();
                                        intent.putExtra("event", response.body());
                                        setResult(2, intent);
                                        finish();

                                    } else {
                                        Toast.makeText(EventDetailActivity.this, response.raw().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<EventModel> call, Throwable t) {
                                    Log.d(TAG, "onFailure: " + t.getMessage());
                                    Toast.makeText(EventDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        } else {
                            Toast.makeText(this, "L'événement n'a pas de date conforme", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.delete:
                Intent intent = new Intent();
                intent.putExtra("idMovie", eventModel.getId());
                setResult(3, intent);
                finish();
                break;
            case R.id.search_movie:
                if (!movie.getText().toString().isEmpty()) {
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
                                DialogMovie dFragment = DialogMovie.newInstance(response.body());
                                dFragment.show(getSupportFragmentManager(), "tag");

                            } else {
                                Toast.makeText(EventDetailActivity.this, response.raw().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<MovieModel>> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t.getMessage());
                            Toast.makeText(EventDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

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
}
