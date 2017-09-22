package com.example.kevin.cineplanner.planning;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;
import com.example.kevin.cineplanner.BuildConfig;
import com.example.kevin.cineplanner.MyDialogFragment;
import com.example.kevin.cineplanner.R;
import com.example.kevin.cineplanner.event.EventActivity;
import com.example.kevin.cineplanner.login.LoginActivity;
import com.example.kevin.cineplanner.login.LoginInterface;
import com.example.kevin.cineplanner.login.LoginModel;
import com.example.kevin.cineplanner.login.LoginTools;
import com.example.kevin.cineplanner.team.EventModel;
import com.example.kevin.cineplanner.team.TeamModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlanningActivity extends AbstractPlanning {

    private static final String TAG = "PlanningActivity";
    public static ListView mDrawerList;

    private AppCompatButton addTeam;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private FloatingActionMenu menuRed;
    private FloatingActionButton event;
    private FloatingActionButton invite;
    private List<FloatingActionMenu> menus = new ArrayList<>();
    private List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
    private int newYear;
    private int newMonth;

    private Handler mUiHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        addTeam = (AppCompatButton) findViewById(R.id.add_team);
        Log.d(TAG, "onCreate: " + mDrawerLayout);
        mTitle = mDrawerTitle = getTitle();
        // Set the adapter for the list view
        setMenu();

        menuRed = (FloatingActionMenu) findViewById(R.id.fab);
        event = (FloatingActionButton) findViewById(R.id.action_event);
        invite = (FloatingActionButton) findViewById(R.id.action_invite);

//        final FloatingActionButton programFab1 = new FloatingActionButton(this);
//        programFab1.setButtonSize(FloatingActionButton.SIZE_MINI);
//        programFab1.setLabelText("hello");
//        menuRed.addMenuButton(programFab1);

        menuRed.setClosedOnTouchOutside(true);

        menuRed.hideMenuButton(false);

        menus.add(menuRed);

        int delay = 400;
        for (final FloatingActionMenu menu : menus) {
            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    menu.showMenuButton(true);
                }
            }, delay);
            delay += 150;
        }
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PlanningActivity.this, "invite", Toast.LENGTH_SHORT).show();
            }
        });
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PlanningActivity.this, "event", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        addTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialogFragment dFragment = new MyDialogFragment();
                // Show DialogFragment
                dFragment.show(getSupportFragmentManager(), "MyDialogFragment");
            }
        });

    }





    private void setMenu() {
        String url = BuildConfig.URL;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(90, TimeUnit.SECONDS).readTimeout(90, TimeUnit.SECONDS).writeTimeout(90, TimeUnit.SECONDS);
        Retrofit.Builder retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create());
        EndpointInterface endpointInterface = retrofit.build().create(EndpointInterface.class);
        Call<List<TeamModel>> call = endpointInterface.getTeams(LoginTools.getToken(getApplicationContext()));
        call.enqueue(new Callback<List<TeamModel>>() {
            @Override
            public void onResponse(Call<List<TeamModel>> call, Response<List<TeamModel>> response) {
                Log.d(TAG, "onResponse: " + response);
                Log.d(TAG, "onResponse: " + response.body().toString());
                if (response.isSuccessful()) {

                    List<String> teamNames = new ArrayList<>();
                    for (TeamModel t :
                            response.body()) {
                        teamNames.add(t.getName());
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_list_item_multiple_choice, teamNames);

                    mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    mDrawerList.setAdapter(arrayAdapter);

                    if (!response.body().isEmpty()) {
                        for (EventModel ev :
                                response.body().get(0).getEvents()) {
                            Log.d(TAG, "onResponse: " + ev);
                            Log.d(TAG, "onResponse:events " + events);
                            events.add(ev.toWeekViewEvent());
                        }

                         getWeekView().notifyDatasetChanged();
                    }
                } else {
                    Log.d(TAG, "onResponse: " + response.raw());
                }
            }

            @Override
            public void onFailure(Call<List<TeamModel>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data.hasExtra("event")) {
                EventModel event = (EventModel) data.getSerializableExtra("event");
                Log.d(TAG, "onActivityResult: " + event.toString());
                this.events.add(event.toWeekViewEvent());
                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, 3);
                startTime.set(Calendar.MINUTE, 0);
                startTime.set(Calendar.MONTH, newMonth - 1);
                startTime.set(Calendar.YEAR, newYear);
                Calendar endTime = (Calendar) startTime.clone();
                endTime.add(Calendar.HOUR, 1);
                endTime.set(Calendar.MONTH, newMonth - 1);
                WeekViewEvent event2 = new WeekViewEvent(100, getEventTitle(startTime), startTime, endTime);
                event2.setColor(getResources().getColor(R.color.event_color_01));
                this.events.add(event2);
                Log.d(TAG, "onActivityResult:events " + events);
                getWeekView().notifyDatasetChanged();

            }
        }
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> matchedEvents = new ArrayList<WeekViewEvent>();
        for (WeekViewEvent event : events) {
            if (eventMatches(event, newYear, newMonth)) {
                matchedEvents.add(event);
            }
        }
        return matchedEvents;

    }
    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month - 1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }
}
