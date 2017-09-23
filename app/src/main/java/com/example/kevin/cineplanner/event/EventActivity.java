package com.example.kevin.cineplanner.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.kevin.cineplanner.BuildConfig;
import com.example.kevin.cineplanner.R;
import com.example.kevin.cineplanner.login.LoginTools;
import com.example.kevin.cineplanner.planning.EndpointInterface;
import com.example.kevin.cineplanner.team.EventModel;
import com.example.kevin.cineplanner.util.NetworkUtils;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public static String DAY = "day";
    public static String MONTH = "month";
    public static String YEAR = "year";
    public static String TEAM = "team";
    private AppCompatEditText name;
    private AppCompatTextView startDate;
    private AppCompatTextView startHour;
    private AppCompatTextView endDate;
    private AppCompatTextView endHour;
    private AppCompatButton createBtn;
    private int day;
    private int month;
    private int year;
    private long id;
    private static final String TAG = "EventActivity";
    private DatePickerDialog datePickerDialog;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        name = (AppCompatEditText) findViewById(R.id.event_id_input);
        startDate = (AppCompatTextView) findViewById(R.id.date_start);
        startHour = (AppCompatTextView) findViewById(R.id.hour_start);
        endDate = (AppCompatTextView) findViewById(R.id.date_end);
        endHour = (AppCompatTextView) findViewById(R.id.hour_end);
        createBtn = (AppCompatButton) findViewById(R.id.create);


        Bundle bundle = getIntent().getExtras();
        day = bundle.getInt(DAY);
        month = bundle.getInt(MONTH);
        year = bundle.getInt(YEAR);
        id = bundle.getInt(TEAM);
        datePickerDialog = new DatePickerDialog(
                this, this, year, month, day);
        startDate.setOnClickListener(this);
        startHour.setOnClickListener(this);
        endDate.setOnClickListener(this);
        endHour.setOnClickListener(this);
        createBtn.setOnClickListener(this);


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
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startHour.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                break;
            case R.id.hour_end:
                Calendar mcurrentTime2 = Calendar.getInstance();
                int hour2 = mcurrentTime2.get(Calendar.HOUR_OF_DAY);
                int minute2 = mcurrentTime2.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker2;
                mTimePicker2 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endHour.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour2, minute2, true);//Yes 24 hour time
                mTimePicker2.setTitle("Select Time");
                mTimePicker2.show();
                break;
            case R.id.create:
                String nameEvent = name.getText().toString();
//                long startTime = 0;
//                if (!start.getText().toString().isEmpty())
//                    startTime = Long.valueOf(start.getText().toString());
//
//                long endTime = 0;
//                if (!end.getText().toString().isEmpty())
//                    endTime = Long.valueOf(end.getText().toString());
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
                    jsonObject.addProperty("idTeam", id);
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
                                Toast.makeText(EventActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent();
                                intent.putExtra("event", response.body());
                                setResult(1, intent);
                                finish();

                            } else {
                                Toast.makeText(EventActivity.this, response.raw().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<EventModel> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t.getMessage());
                            Toast.makeText(EventActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }


                Toast.makeText(this, "Time " + nameEvent + " | " + startTime + " | " + endTime, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        startDate.setText(
                i2 + " / " + (i1 + 1) + " / "
                        + i);
        endDate.setText(
                i2 + " / " + (i1 + 1) + " / "
                        + i);
    }
}
