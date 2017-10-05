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
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RatingBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cineplanner.kevin.cineplanner.util.BoxLoading;
import com.cineplanner.kevin.cineplanner.BuildConfig;
import com.cineplanner.kevin.cineplanner.comment.CommentActivity;
import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.login.LoginTools;
import com.cineplanner.kevin.cineplanner.movie.DialogMovie;
import com.cineplanner.kevin.cineplanner.planning.EndpointInterface;
import com.cineplanner.kevin.cineplanner.team.CommentModel;
import com.cineplanner.kevin.cineplanner.team.EventModel;
import com.cineplanner.kevin.cineplanner.team.NotationModel;
import com.cineplanner.kevin.cineplanner.util.NetworkUtils;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.cineplanner.kevin.cineplanner.comment.CommentActivity.COMMENTS;
import static com.cineplanner.kevin.cineplanner.comment.CommentActivity.EVENTID;
import static com.cineplanner.kevin.cineplanner.util.NetworkUtils.setUiInProgress;

public class EventDetailActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    private static final String TAG = "EventDetailActivity";
    public static String EVENT = "event";
    public static String TEAM = "team";
    public static String NAMETEAM = "nameTeam";
    public static AppCompatEditText movie;
    public static MovieModel movieSelected;
    Calendar cal;
    Calendar cal2;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private AppCompatEditText name;
    private AppCompatTextView startDate;
    private AppCompatTextView startHour;
    private AppCompatTextView endDate;
    private AppCompatTextView endHour;
    private AppCompatTextView team;
    private AppCompatTextView comments;
    private AppCompatButton update;
    private AppCompatButton delete;
    private AppCompatButton submitRate;
    private RatingBar myRating;
    private RatingBar teamRating;
    private AppCompatImageView imageView;
    private AppCompatImageButton search;
    private int day;
    private int month;
    private int year;
    private BoxLoading alert;
    private String nameTeam;
    private EventModel eventModel;
    private long id;
    private DatePickerDialog datePickerDialog;

    public static void setMovieSelected(MovieModel movieSelected) {
        EventDetailActivity.movieSelected = movieSelected;
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
        comments = (AppCompatTextView) findViewById(R.id.comments);
        update = (AppCompatButton) findViewById(R.id.update);
        delete = (AppCompatButton) findViewById(R.id.delete);
        submitRate = (AppCompatButton) findViewById(R.id.send_rate);
        search = (AppCompatImageButton) findViewById(R.id.search_movie);
        imageView = (AppCompatImageView) findViewById(R.id.img_event);
        myRating = (RatingBar) findViewById(R.id.my_rating);
        teamRating = (RatingBar) findViewById(R.id.rating_team);

        alert = new BoxLoading();
        Bundle bundle = getIntent().getExtras();


        eventModel = (EventModel) bundle.getSerializable(EVENT);
        id = bundle.getLong(TEAM);
        nameTeam = bundle.getString(NAMETEAM);
        team.setText("Team : " + nameTeam);
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
            Glide.with(getApplicationContext()).load(eventModel.getMovie().getPoster_path()).into(imageView);

        }
        float rate = 0;
        for (NotationModel n :
                eventModel.getNotations()) {
            rate += n.getNotation();
        }
        rate = rate / eventModel.getNotations().size();

        teamRating.setRating(rate);

        for (NotationModel n :
                eventModel.getNotations()) {
            if (n.getAuthorId() == LoginTools.getIdUser(getApplicationContext())) {
                submitRate.setEnabled(false);
                myRating.setIsIndicator(true);
                myRating.setRating(n.getNotation());
            }
        }

        comments.setText("Commentaires (" + eventModel.getComments().size() + ")");
        comments.setOnClickListener(this);
        if (LoginTools.getIdUser(getApplicationContext()) == eventModel.getCreatorId()) {
            startDate.setOnClickListener(this);
            startHour.setOnClickListener(this);
            endDate.setOnClickListener(this);
            endHour.setOnClickListener(this);
            update.setOnClickListener(this);
            delete.setOnClickListener(this);
            search.setOnClickListener(this);
            submitRate.setOnClickListener(this);
        } else {
            name.setEnabled(false);
            movie.setEnabled(false);
            search.setVisibility(View.GONE);
            update.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }
        if (!NetworkUtils.isOnline(getApplicationContext())) {
            name.setEnabled(false);
            movie.setEnabled(false);
            search.setVisibility(View.GONE);
            update.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            submitRate.setEnabled(false);
            myRating.setIsIndicator(true);
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
                            jsonObject.addProperty("id", eventModel.getId());
                            if (movieSelected != null) {
                                jsonObject.addProperty("idMovie", movieSelected.getId());
                            } else {
                                jsonObject.addProperty("idMovie", eventModel.getMovie().getId());
                            }
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
                                        Toast.makeText(EventDetailActivity.this, "Evénement mis à jour", Toast.LENGTH_SHORT).show();
                                        setUiInProgress(getSupportFragmentManager(), alert, false);

                                        Intent intent = new Intent();
                                        intent.putExtra("event", response.body());
                                        setResult(3, intent);
                                        finish();

                                    } else {
                                        Toast.makeText(EventDetailActivity.this, response.raw().toString(), Toast.LENGTH_SHORT).show();
                                        setUiInProgress(getSupportFragmentManager(), alert, false);

                                    }
                                }

                                @Override
                                public void onFailure(Call<EventModel> call, Throwable t) {
                                    Log.d(TAG, "onFailure: " + t.getMessage());
                                    Toast.makeText(EventDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    setUiInProgress(getSupportFragmentManager(), alert, false);

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
                                DialogMovie dFragment = DialogMovie.newInstance(response.body());
                                dFragment.show(getSupportFragmentManager(), "tag");
                                setUiInProgress(getSupportFragmentManager(), alert, false);

                            } else {
                                setUiInProgress(getSupportFragmentManager(), alert, false);

                                Toast.makeText(EventDetailActivity.this, response.raw().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<MovieModel>> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t.getMessage());
                            setUiInProgress(getSupportFragmentManager(), alert, false);

                            Toast.makeText(EventDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                } else {
                    Toast.makeText(this, "Recherche vide", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.send_rate:
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("idEvent", eventModel.getId());
                jsonObject.addProperty("notation", myRating.getRating());
                String url = BuildConfig.URL;
                Retrofit.Builder retrofit = new Retrofit.Builder()
                        .client(NetworkUtils.client(getApplicationContext(), "notation"))
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create());
                EndpointInterface endpointInterface = retrofit.build().create(EndpointInterface.class);
                Call<NotationModel> call = endpointInterface.notation(LoginTools.getToken(getApplicationContext()), jsonObject);
                call.enqueue(new Callback<NotationModel>() {
                    @Override
                    public void onResponse(Call<NotationModel> call, Response<NotationModel> response) {
                        Log.d(TAG, "onResponse: " + response);
                        if (response.isSuccessful()) {
                            Toast.makeText(EventDetailActivity.this, "Note enregistré", Toast.LENGTH_SHORT).show();
                            submitRate.setEnabled(false);
                            myRating.setIsIndicator(true);
                            //recalcule
                            float rate = myRating.getRating();
                            Log.d(TAG, "onResponse:rate " + rate);
                            Log.d(TAG, "onResponse:rate " + eventModel.getNotations());
                            for (NotationModel n :
                                    eventModel.getNotations()) {
                                rate += n.getNotation();
                            }
                            Log.d(TAG, "onResponse:rate " + rate);
                            Log.d(TAG, "onResponse:rate " + eventModel.getNotations().size());
                            if (eventModel.getNotations().size() == 0){
                                teamRating.setRating(rate);
                            }else {
                                rate = rate / eventModel.getNotations().size();
                                teamRating.setRating(rate);
                            }
                            eventModel.getNotations().add(response.body());

                        } else {
                            Toast.makeText(EventDetailActivity.this, response.raw().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<NotationModel> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                        Toast.makeText(EventDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
                break;
            case R.id.comments:
                Intent intentComment = new Intent(getApplicationContext(), CommentActivity.class);
                intentComment.putExtra(COMMENTS, eventModel.getComments());
                Log.d(TAG, "onClick: " + eventModel.getId());
                intentComment.putExtra(EVENTID, eventModel.getId());
                startActivityForResult(intentComment, 5);

                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5) {
            if (data != null) {
                if (data.hasExtra("comments")) {
                    ArrayList<CommentModel> comment = (ArrayList<CommentModel>) data.getSerializableExtra("comments");
                    eventModel.setComments(comment);
                    comments.setText("Commentaires (" + eventModel.getComments().size() + ")");

                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        movieSelected = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: befor");
        Intent intent = new Intent();
        intent.putExtra("finish", eventModel);
        setResult(3, intent);
        finish();
        super.onBackPressed();
    }
}
