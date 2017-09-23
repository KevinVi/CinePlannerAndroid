package com.example.kevin.cineplanner.planning;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.example.kevin.cineplanner.BuildConfig;
import com.example.kevin.cineplanner.MyDialogFragment;
import com.example.kevin.cineplanner.R;
import com.example.kevin.cineplanner.event.EventActivity;
import com.example.kevin.cineplanner.event.EventDetailActivity;
import com.example.kevin.cineplanner.invite.InviteActivity;
import com.example.kevin.cineplanner.login.LoginActivity;
import com.example.kevin.cineplanner.login.LoginInterface;
import com.example.kevin.cineplanner.login.LoginModel;
import com.example.kevin.cineplanner.login.LoginTools;
import com.example.kevin.cineplanner.team.EventModel;
import com.example.kevin.cineplanner.team.TeamModel;
import com.example.kevin.cineplanner.util.NetworkUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.kevin.cineplanner.event.EventActivity.DAY;
import static com.example.kevin.cineplanner.event.EventActivity.MONTH;
import static com.example.kevin.cineplanner.event.EventActivity.TEAM;
import static com.example.kevin.cineplanner.event.EventActivity.YEAR;
import static com.example.kevin.cineplanner.event.EventDetailActivity.EVENT;
import static com.example.kevin.cineplanner.event.EventDetailActivity.NAMETEAM;

public class PlanningActivity extends AbstractPlanning implements WeekView.EventClickListener {

    private static final String TAG = "PlanningActivity";
    public static ListView mDrawerList;

    private AppCompatButton addTeam;
    private AppCompatButton logout;
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
    private List<Long> teamIds = new ArrayList<>();
    List<String> teamNames = new ArrayList<>();

    private Handler mUiHandler = new Handler();
    private HashMap<Long, EventModel> eventMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Show a toast message about the touched event.
        getmWeekView().setOnEventClickListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        addTeam = (AppCompatButton) findViewById(R.id.add_team);
        logout = (AppCompatButton) findViewById(R.id.logout);
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
                if (mDrawerList.getCheckedItemPosition() >= 0) {
                    Intent intent = new Intent(getApplicationContext(), InviteActivity.class);
                    startActivity(intent);
                    Toast.makeText(PlanningActivity.this, "invite", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: " + mDrawerList.getCheckedItemPosition());
                } else {
                    Toast.makeText(PlanningActivity.this, "Sélectionnez une team", Toast.LENGTH_SHORT).show();
                }
            }
        });
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerList.getCheckedItemPosition() >= 0) {
                    Toast.makeText(PlanningActivity.this, "event" + getWeekView().getFirstVisibleDay().get(Calendar.DAY_OF_YEAR), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                    intent.putExtra(MONTH, getWeekView().getFirstVisibleDay().get(Calendar.MONTH));
                    intent.putExtra(DAY, getWeekView().getFirstVisibleDay().get(Calendar.DAY_OF_MONTH));
                    intent.putExtra(YEAR, getWeekView().getFirstVisibleDay().get(Calendar.YEAR));
                    intent.putExtra(TEAM, teamIds.get(mDrawerList.getCheckedItemPosition()));
                    startActivityForResult(intent, 1);
                } else {
                    Toast.makeText(PlanningActivity.this, "Sélectionnez une taem", Toast.LENGTH_SHORT).show();
                }
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
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginTools.deleteLoginInfo(getApplicationContext());
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });


        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                Log.d(TAG, "onClick: " + mDrawerList.getCheckedItemPosition());
                LoginTools.setSelectedTeam(getApplicationContext(), mDrawerList.getCheckedItemPosition());
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });




    }


    private void setMenu() {
        if (NetworkUtils.isOnline(getApplicationContext())) {
            String url = BuildConfig.URL + "team/";
            Retrofit.Builder retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(NetworkUtils.client(getApplicationContext(), "teams"));
            EndpointInterface endpointInterface = retrofit.build().create(EndpointInterface.class);
            Call<List<TeamModel>> call = endpointInterface.getTeams(LoginTools.getToken(getApplicationContext()));
            call.enqueue(new Callback<List<TeamModel>>() {
                @Override
                public void onResponse(Call<List<TeamModel>> call, Response<List<TeamModel>> response) {
                    Log.d(TAG, "onResponse: " + response);
                    if (response.isSuccessful()) {

                        Log.d(TAG, "onResponse: " + response.body().toString());
                        teamNames = new ArrayList<>();
                        for (TeamModel t :
                                response.body()) {
                            teamNames.add(t.getName());
                            teamIds.add(t.getId());
                        }
                        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                                android.R.layout.simple_list_item_multiple_choice, teamNames);

                        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                        mDrawerList.setAdapter(arrayAdapter);
                        if (LoginTools.getSelectedTeam(getApplicationContext()) >= 0) {
                            Log.d(TAG, "onResponse: " + LoginTools.getSelectedTeam(getApplicationContext()));
                            mDrawerList.setItemChecked(LoginTools.getSelectedTeam(getApplicationContext()), true);
                        }else{
                            mDrawerLayout.openDrawer(Gravity.START);
                        }

                        if (!response.body().isEmpty()) {
                            for (EventModel ev :
                                    response.body().get(0).getEvents()) {
                                Log.d(TAG, "onResponse: " + ev);
                                Log.d(TAG, "onResponse:events " + events);
                                events.add(ev.toWeekViewEvent());
                                eventMap.put(ev.getId(), ev);
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
        } else {
            final HttpUrl url = HttpUrl.parse(BuildConfig.URL + "team/");
            String cache = NetworkUtils.getFromCache(getApplicationContext(), url, "teams");
            List<TeamModel> teams = new ArrayList<>();
            try {
                teams = cache != null ? (List<TeamModel>) new Gson().fromJson(cache, new TypeToken<List<TeamModel>>() {
                }.getType()) : new ArrayList<TeamModel>();

            } catch (JsonSyntaxException ex) {
                ex.printStackTrace();
            }


            Log.d(TAG, "setMenu: " + teams);
            List<String> teamNames = new ArrayList<>();
            for (TeamModel t :
                    teams) {
                teamNames.add(t.getName());
                teamIds.add(t.getId());
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_list_item_multiple_choice, teamNames);

            mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            mDrawerList.setAdapter(arrayAdapter);
            if (LoginTools.getSelectedTeam(getApplicationContext()) >= 0) {
                Log.d(TAG, "onResponse: 000" + LoginTools.getSelectedTeam(getApplicationContext()));
                mDrawerList.setItemChecked(LoginTools.getSelectedTeam(getApplicationContext()), true);
            }else{
                mDrawerLayout.openDrawer(Gravity.START);
            }

            if (!teams.isEmpty()) {
                for (EventModel ev :
                        teams.get(0).getEvents()) {
                    Log.d(TAG, "onResponse: " + ev);
                    Log.d(TAG, "onResponse:events " + events);
                    events.add(ev.toWeekViewEvent());
                }

                getWeekView().notifyDatasetChanged();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                if (data.hasExtra("event")) {
                    EventModel event = (EventModel) data.getSerializableExtra("event");
                    Log.d(TAG, "onActivityResult: " + event.toString());
                    this.events.add(event.toWeekViewEvent());

                    eventMap.put(event.getId(), event);

                    Log.d(TAG, "onActivityResult:events " + events);
                    getWeekView().notifyDatasetChanged();

                }
            }
        } else if (requestCode == 2) {
            if (data != null) {
                if (data.hasExtra("event")) {
                    EventModel event = (EventModel) data.getSerializableExtra("event");
                    Log.d(TAG, "onActivityResult: " + event.toString());

                    EventModel eventRemove = eventMap.get(event.getId());
                    this.events.remove(eventRemove.toWeekViewEvent());

                    this.events.add(event.toWeekViewEvent());

                    eventMap.put(event.getId(), event);

                    Log.d(TAG, "onActivityResult:events " + events);
                    getWeekView().notifyDatasetChanged();

                }
            }
        } else if (requestCode == 3) {
            if (data != null) {
                if (data.hasExtra("event")) {
                    EventModel event = (EventModel) data.getSerializableExtra("event");
                    Log.d(TAG, "onActivityResult: " + event.toString());

                    EventModel eventRemove = eventMap.get(event.getId());
                    this.events.remove(eventRemove.toWeekViewEvent());
                    eventMap.values().removeAll(Collections.singleton(event));
                    Log.d(TAG, "onActivityResult:events " + events);
                    getWeekView().notifyDatasetChanged();

                }
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

    @Override
    public void onEmptyViewLongPress(Calendar time) {

    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

        Log.d(TAG, "onEventClick: " + event);
        EventModel ev = eventMap.get(event.getId());
        Intent intent = new Intent(getApplicationContext(), EventDetailActivity.class);
        intent.putExtra(EVENT, ev);
        intent.putExtra(TEAM, teamIds.get(mDrawerList.getCheckedItemPosition()));
        intent.putExtra(NAMETEAM, teamNames.get(mDrawerList.getCheckedItemPosition()));
        startActivityForResult(intent, 2);
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }
}
