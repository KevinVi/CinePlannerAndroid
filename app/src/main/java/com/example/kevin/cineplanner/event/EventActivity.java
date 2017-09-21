package com.example.kevin.cineplanner.event;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.kevin.cineplanner.BuildConfig;
import com.example.kevin.cineplanner.R;
import com.example.kevin.cineplanner.login.LoginActivity;
import com.example.kevin.cineplanner.login.LoginInterface;
import com.example.kevin.cineplanner.login.LoginModel;
import com.example.kevin.cineplanner.login.LoginTools;
import com.example.kevin.cineplanner.planning.EndpointInterface;
import com.example.kevin.cineplanner.planning.PlanningActivity;
import com.example.kevin.cineplanner.team.EventModel;
import com.google.gson.JsonObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventActivity extends AppCompatActivity implements View.OnClickListener {
    private AppCompatEditText name;
    private AppCompatTextView start;
    private AppCompatTextView end;
    private AppCompatButton startBtn;
    private AppCompatButton endBtn;
    private AppCompatButton createBtn;
    private static final String TAG = "EventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        name = (AppCompatEditText) findViewById(R.id.event_id_input);
        start = (AppCompatTextView) findViewById(R.id.start_text);
        end = (AppCompatTextView) findViewById(R.id.end_text);
        startBtn = (AppCompatButton) findViewById(R.id.start_button);
        endBtn = (AppCompatButton) findViewById(R.id.end_button);
        createBtn = (AppCompatButton) findViewById(R.id.create);
        startBtn.setOnClickListener(this);
        endBtn.setOnClickListener(this);
        createBtn.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_button:
                final View dialogView = View.inflate(this, R.layout.date_time_picker, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.AppTheme).create();

                dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
                        TimePicker timePicker = dialogView.findViewById(R.id.time_picker);

                        Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute());

                        start.setText("" + calendar.getTimeInMillis());
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setView(dialogView);
                alertDialog.show();
                break;
            case R.id.end_button:
                final View dialogView2 = View.inflate(this, R.layout.date_time_picker, null);
                final AlertDialog alertDialog2 = new AlertDialog.Builder(this, R.style.AppTheme).create();

                dialogView2.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DatePicker datePicker = dialogView2.findViewById(R.id.date_picker);
                        TimePicker timePicker = dialogView2.findViewById(R.id.time_picker);

                        Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute());

                        end.setText("" + calendar.getTimeInMillis());
                        alertDialog2.dismiss();
                    }
                });
                alertDialog2.setView(dialogView2);
                alertDialog2.show();
                break;
            case R.id.create:
                String nameEvent = name.getText().toString();
                long startTime = 0;
                if (!start.getText().toString().isEmpty())
                    startTime = Long.valueOf(start.getText().toString());

                long endTime = 0;
                if (!end.getText().toString().isEmpty())
                    endTime = Long.valueOf(end.getText().toString());
                if (endTime != 0 && startTime != 0 && !nameEvent.isEmpty()) {

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("name", nameEvent);
                    jsonObject.addProperty("start", startTime);
                    jsonObject.addProperty("end", endTime);
                    jsonObject.addProperty("idTeam", 2);

                    String url = BuildConfig.URL;
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    Retrofit.Builder retrofit = new Retrofit.Builder()
                            .client(builder.build())
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
}
