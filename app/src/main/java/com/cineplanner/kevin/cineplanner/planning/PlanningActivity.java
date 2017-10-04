package com.cineplanner.kevin.cineplanner.planning;

import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.anupcowkur.reservoir.Reservoir;
import com.cineplanner.kevin.cineplanner.BoxLoading;
import com.cineplanner.kevin.cineplanner.BuildConfig;
import com.cineplanner.kevin.cineplanner.CachingControlInterceptor;
import com.cineplanner.kevin.cineplanner.InviteDialogFragment;
import com.cineplanner.kevin.cineplanner.MyDialogFragment;
import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.event.EventActivity;
import com.cineplanner.kevin.cineplanner.event.EventDetailActivity;
import com.cineplanner.kevin.cineplanner.event.MovieModel;
import com.cineplanner.kevin.cineplanner.invite.InviteActivity;
import com.cineplanner.kevin.cineplanner.join.DialogJoin;
import com.cineplanner.kevin.cineplanner.login.LoginActivity;
import com.cineplanner.kevin.cineplanner.login.LoginTools;
import com.cineplanner.kevin.cineplanner.movie.DialogMovie;
import com.cineplanner.kevin.cineplanner.suggeestionDisplay.DialogLearningDisplay;
import com.cineplanner.kevin.cineplanner.suggestion.DialogLearning;
import com.cineplanner.kevin.cineplanner.suggestion.SuggestionModel;
import com.cineplanner.kevin.cineplanner.team.EventModel;
import com.cineplanner.kevin.cineplanner.team.TeamModel;
import com.cineplanner.kevin.cineplanner.util.NetworkUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.cineplanner.kevin.cineplanner.event.EventActivity.DAY;
import static com.cineplanner.kevin.cineplanner.event.EventActivity.MONTH;
import static com.cineplanner.kevin.cineplanner.event.EventActivity.TEAM;
import static com.cineplanner.kevin.cineplanner.event.EventActivity.YEAR;
import static com.cineplanner.kevin.cineplanner.event.EventDetailActivity.EVENT;
import static com.cineplanner.kevin.cineplanner.event.EventDetailActivity.NAMETEAM;
import static com.cineplanner.kevin.cineplanner.util.NetworkUtils.getCacheFileSize;
import static com.cineplanner.kevin.cineplanner.util.NetworkUtils.isOnline;
import static com.cineplanner.kevin.cineplanner.util.NetworkUtils.setUiInProgress;

public class PlanningActivity extends AbstractPlanning implements WeekView.EventClickListener {

    private static final String TAG = "PlanningActivity";
    public static ListView mDrawerList;

    private AppCompatButton addTeam;
    private AppCompatButton joinTeam;
    private AppCompatButton logout;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private FloatingActionMenu menuRed;
    private FloatingActionButton event;
    private FloatingActionButton invite;
    private FloatingActionButton refresh;
    private FloatingActionButton suggestion;
    private List<FloatingActionMenu> menus = new ArrayList<>();
    private List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
    private List<EventModel> currentEvents = new ArrayList<>();
    public static List<TeamModel> myTeams = new ArrayList<>();
    private List<TeamModel> pendingTeams = new ArrayList<>();
    private BoxLoading alert;


    private Handler mUiHandler = new Handler();
    private HashMap<Long, EventModel> eventMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Reservoir.init(this, 2048); //in bytes
        } catch (IOException e) {
            //failure
        }
        // Show a toast message about the touched event.
        getmWeekView().setOnEventClickListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        addTeam = (AppCompatButton) findViewById(R.id.add_team);
        joinTeam = (AppCompatButton) findViewById(R.id.join_team);
        logout = (AppCompatButton) findViewById(R.id.logout);
        Log.d(TAG, "onCreate: " + mDrawerLayout);
        mTitle = mDrawerTitle = getTitle();
        // Set the adapter for the list view
        alert = new BoxLoading();
        setMenu();
        menuRed = (FloatingActionMenu) findViewById(R.id.fab);
        event = (FloatingActionButton) findViewById(R.id.action_event);
        invite = (FloatingActionButton) findViewById(R.id.action_invite);
        refresh = (FloatingActionButton) findViewById(R.id.action_refresh);
        suggestion = (FloatingActionButton) findViewById(R.id.action_suggestion);

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
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline(getApplicationContext())) {
                    setMenu();
                }
            }
        });
        suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerList.getCheckedItemPosition() >= 0) {
                    setUiInProgress(getSupportFragmentManager(), alert, true);

                    if (!LoginTools.getLearn(getApplicationContext())) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("id", myTeams.get(mDrawerList.getCheckedItemPosition()).getId());
                        Log.d(TAG, "onClick: " + jsonObject);
                        String url = BuildConfig.URL;
                        OkHttpClient.Builder builder = new OkHttpClient.Builder();
                        Retrofit.Builder retrofit = new Retrofit.Builder()
                                .client(builder.build())
                                .baseUrl(url)
                                .addConverterFactory(GsonConverterFactory.create());
                        EndpointInterface endpointInterface = retrofit.build().create(EndpointInterface.class);
                        Call<List<MovieModel>> call = endpointInterface.learning(LoginTools.getToken(getApplicationContext()), jsonObject);
                        call.enqueue(new Callback<List<MovieModel>>() {
                            @Override
                            public void onResponse(Call<List<MovieModel>> call, Response<List<MovieModel>> response) {
                                Log.d(TAG, "onResponse: " + response);
                                if (response.isSuccessful()) {
                                    Log.d(TAG, "onResponse:res  " + response.body().toString());
                                    Log.d(TAG, "onResponse:res size " + response.body().size());
                                    DialogLearning dFragment = DialogLearning.newInstance(response.body(), (int) myTeams.get(mDrawerList.getCheckedItemPosition()).getId());

                                    dFragment.show(getSupportFragmentManager(), "DialogLearning");
                                    Log.d(TAG, "onClick: " + mDrawerList.getCheckedItemPosition());
                                    setUiInProgress(getSupportFragmentManager(), alert, false);
                                } else {
                                    Log.d(TAG, "onResponse: " + response.raw());
                                    LoginTools.setLean(getApplicationContext(), true);
                                    setUiInProgress(getSupportFragmentManager(), alert, false);
                                }
                            }

                            @Override
                            public void onFailure(Call<List<MovieModel>> call, Throwable t) {
                                Log.d(TAG, "onFailure: " + t.getMessage());
                                setUiInProgress(getSupportFragmentManager(), alert, false);

                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("id", myTeams.get(mDrawerList.getCheckedItemPosition()).getId());
                        Log.d(TAG, "onClick: " + jsonObject);
                        String url = BuildConfig.URL;
                        OkHttpClient.Builder builder = new OkHttpClient.Builder();
                        Retrofit.Builder retrofit = new Retrofit.Builder()
                                .client(builder.build())
                                .baseUrl(url)
                                .addConverterFactory(GsonConverterFactory.create());
                        EndpointInterface endpointInterface = retrofit.build().create(EndpointInterface.class);
                        Call<MovieModel> call = endpointInterface.learningSuggestion(LoginTools.getToken(getApplicationContext()), jsonObject);
                        call.enqueue(new Callback<MovieModel>() {
                            @Override
                            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                                Log.d(TAG, "onResponse: " + response);
                                if (response.isSuccessful()) {
                                    Log.d(TAG, "onResponse:res  " + response.body().toString());
                                    setUiInProgress(getSupportFragmentManager(), alert, false);
                                    DialogLearningDisplay dFragment = DialogLearningDisplay.newInstance(response.body(), (int) myTeams.get(mDrawerList.getCheckedItemPosition()).getId(), PlanningActivity.this);

                                    dFragment.show(getSupportFragmentManager(), "DialogLearningDisplay");
                                    Log.d(TAG, "onClick: " + mDrawerList.getCheckedItemPosition());
                                } else {
                                    Toast.makeText(getApplicationContext(), "Aucune Suggestion", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onResponse: " + response.raw());
                                    setUiInProgress(getSupportFragmentManager(), alert, false);
                                }
                            }

                            @Override
                            public void onFailure(Call<MovieModel> call, Throwable t) {
                                Log.d(TAG, "onFailure: " + t.getMessage());
                                setUiInProgress(getSupportFragmentManager(), alert, false);

                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                } else {
                    Toast.makeText(PlanningActivity.this, "Sélectionnez une team", Toast.LENGTH_SHORT).show();
                }
            }
        });


        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerList.getCheckedItemPosition() >= 0) {
                    InviteDialogFragment dFragment = InviteDialogFragment.newInstance(myTeams.get(mDrawerList.getCheckedItemPosition()));
                    // Show DialogFragment
                    dFragment.show(getSupportFragmentManager(), "MyDialogFragment");
                    Log.d(TAG, "onClick: " + mDrawerList.getCheckedItemPosition());
                } else {
                    Toast.makeText(PlanningActivity.this, "Sélectionnez une team", Toast.LENGTH_SHORT).show();
                }
                Type resultType = new TypeToken<List<TeamModel>>() {
                }.getType();
                try {
                    List<TeamModel> teams = Reservoir.get("teams", resultType);
                    Log.d(TAG, "onClick: " + teams);
                } catch (IOException e) {
                    //failure
                }
            }
        });
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mDrawerList.getCheckedItemPosition() >= 0) {
                    if (NetworkUtils.isOnline(getApplicationContext())) {
                        Toast.makeText(PlanningActivity.this, "event" + getWeekView().getFirstVisibleDay().get(Calendar.DAY_OF_YEAR), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                        intent.putExtra(MONTH, getWeekView().getFirstVisibleDay().get(Calendar.MONTH));
                        intent.putExtra(DAY, getWeekView().getFirstVisibleDay().get(Calendar.DAY_OF_MONTH));
                        intent.putExtra(YEAR, getWeekView().getFirstVisibleDay().get(Calendar.YEAR));
                        intent.putExtra(EventActivity.TEAM, myTeams.get(mDrawerList.getCheckedItemPosition()).getId());
                        startActivityForResult(intent, 2);
                    } else {
                        Toast.makeText(PlanningActivity.this, "Internet necessaire pour créé un événement", Toast.LENGTH_SHORT).show();
                    }
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

        joinTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pendingTeams.isEmpty()) {
                    DialogJoin dFragment = DialogJoin.newInstance(pendingTeams);
                    dFragment.show(getSupportFragmentManager(), "dialogjoin");
                } else {
                    Toast.makeText(PlanningActivity.this, "Aucune team à rejoindre", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Log.d(TAG, "open: " + mDrawerList.getCheckedItemPosition());

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                Log.d(TAG, "onClick: " + mDrawerList.getCheckedItemPosition());
                if (mDrawerList.getCheckedItemPosition() >= 0) {

                    LoginTools.setSelectedTeam(getApplicationContext(), mDrawerList.getCheckedItemPosition());

                    events = new ArrayList<>();
                    Log.d(TAG, "onDrawerClosed: " + LoginTools.getSelectedTeam(getApplicationContext()));
                    Log.d(TAG, "onDrawerClosed: " + myTeams.get(LoginTools.getSelectedTeam(getApplicationContext())).getId());
                    Log.d(TAG, "onDrawerClosed: " + myTeams.get(LoginTools.getSelectedTeam(getApplicationContext())).getEvents());
                    for (EventModel ev :
                            myTeams.get(LoginTools.getSelectedTeam(getApplicationContext())).getEvents()) {
                        currentEvents.add(ev);
                        Log.d(TAG, "onResponse: " + ev);
                        Log.d(TAG, "onResponse:events " + events);
                        events.add(ev.toWeekViewEvent());
                        eventMap.put(ev.getId(), ev);
                    }
//                    List<WeekViewEvent> matchedEvents = new ArrayList<WeekViewEvent>();
//                    for (WeekViewEvent event : events) {
//                        matchedEvents.add(event);
//                    }

                    getWeekView().notifyDatasetChanged();
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


    }

    private void getPendingTeams() {
        String url = BuildConfig.URL + "team/";

        Retrofit.Builder retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(NetworkUtils.client(getApplicationContext(), "team"));
        EndpointInterface endpointInterface = retrofit.build().create(EndpointInterface.class);
        Call<List<TeamModel>> call = endpointInterface.getPending(LoginTools.getToken(getApplicationContext()));
        call.enqueue(new Callback<List<TeamModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<TeamModel>> call, @NonNull Response<List<TeamModel>> response) {
                Log.d(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.body());
                    pendingTeams = response.body();
                    joinTeam.setText(getString(R.string.join_team) + "(" + pendingTeams.size() + ")");
                    setUiInProgress(getSupportFragmentManager(), alert, false);
                } else {
                    Log.d(TAG, "onResponse: " + response.raw());
                    Toast.makeText(PlanningActivity.this, "Impossible de récupéré les groupes", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TeamModel>> call, @NonNull Throwable thwThrowable) {
                Toast.makeText(PlanningActivity.this, "Impossible de récupéré les groupes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setMenu() {
        setUiInProgress(getSupportFragmentManager(), alert, true);
        String url = BuildConfig.URL + "team/";

        Retrofit.Builder retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(NetworkUtils.client(getApplicationContext(), "team"));
        EndpointInterface endpointInterface = retrofit.build().create(EndpointInterface.class);
        Call<List<TeamModel>> call = endpointInterface.getTeams(LoginTools.getToken(getApplicationContext()));
        call.enqueue(new Callback<List<TeamModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<TeamModel>> call, @NonNull Response<List<TeamModel>> response) {
                Log.d(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {

                    if (response.body() != null) {

                        if (!response.body().equals(myTeams))
                            Log.d(TAG, "onResponse: " + response.body().toString());
                        List<String> teamNames = new ArrayList<>();
                        for (TeamModel t :
                                response.body()) {
                            teamNames.add(t.getName());
                        }
                        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                                android.R.layout.simple_list_item_multiple_choice, teamNames);

                        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                        mDrawerList.setAdapter(arrayAdapter);

                        try {
                            Reservoir.put("teams", response.body());
                        } catch (IOException e) {
                            //failure;
                        }
                        if (!response.body().isEmpty()) {
                            myTeams = response.body();
                        }
                        if (LoginTools.getSelectedTeam(getApplicationContext()) >= 0) {
                            Log.d(TAG, "onResponse: " + LoginTools.getSelectedTeam(getApplicationContext()));
                            mDrawerList.setItemChecked(LoginTools.getSelectedTeam(getApplicationContext()), true);
                            if (!response.body().isEmpty()) {

                                events = new ArrayList<>();
                                for (EventModel ev :
                                        response.body().get(LoginTools.getSelectedTeam(getApplicationContext())).getEvents()) {
                                    currentEvents.add(ev);
                                    Log.d(TAG, "onResponse: " + ev);
                                    Log.d(TAG, "onResponse:events " + events);
                                    events.add(ev.toWeekViewEvent());
                                    eventMap.put(ev.getId(), ev);
                                }

                                getWeekView().notifyDatasetChanged();
                            }
                        } else {
                            mDrawerLayout.openDrawer(Gravity.START);
                        }
                        getPendingTeams();


                    }
                } else {
                    Log.d(TAG, "onResponse: " + response.raw());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TeamModel>> call, @NonNull Throwable thwThrowable) {
                Type resultType = new TypeToken<List<TeamModel>>() {
                }.getType();
                try {
                    List<TeamModel> teams = Reservoir.get("teams", resultType);

                    if (teams != null) {
                        List<String> teamNames = new ArrayList<>();

                        for (TeamModel t :
                                teams) {
                            teamNames.add(t.getName());
                        }
                        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                                android.R.layout.simple_list_item_multiple_choice, teamNames);

                        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                        mDrawerList.setAdapter(arrayAdapter);

                        if (LoginTools.getSelectedTeam(getApplicationContext()) >= 0) {
                            Log.d(TAG, "onResponse: " + LoginTools.getSelectedTeam(getApplicationContext()));
                            mDrawerList.setItemChecked(LoginTools.getSelectedTeam(getApplicationContext()), true);
                        } else {
                            mDrawerLayout.openDrawer(Gravity.START);
                        }

                        events = new ArrayList<>();
                        if (!teams.isEmpty()) {
                            for (EventModel ev :
                                    teams.get(0).getEvents()) {
                                Log.d(TAG, "onResponse: " + ev);
                                Log.d(TAG, "onResponse:events " + events);
                                events.add(ev.toWeekViewEvent());
                                eventMap.put(ev.getId(), ev);
                            }

                            getWeekView().notifyDatasetChanged();
                        }
                        setUiInProgress(getSupportFragmentManager(), alert, false);
                    }
                } catch (IOException e) {
                    //failure
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + resultCode + data);
        if (requestCode == 2) {
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
        } else if (requestCode == 3) {
            if (data != null) {
                if (data.hasExtra("idMovie")) {
                    long id = data.getLongExtra("idMovie", -1);
                    Log.d(TAG, "onActivityResult: " + id);

                    if (id != -1) {
                        EventModel eventRemove = eventMap.get(id);
                        this.events.remove(eventRemove.toWeekViewEvent());
                        eventMap.remove(id);
                    }

                    Log.d(TAG, "onActivityResult:events " + events);
                    getWeekView().notifyDatasetChanged();

                } else if (data.hasExtra("event")) {
                    EventModel event = (EventModel) data.getSerializableExtra("event");
                    Log.d(TAG, "onActivityResult: " + event.toString());
                    EventModel oldEvent = eventMap.get(event.getId());
                    this.events.remove(oldEvent.toWeekViewEvent());
                    this.events.add(event.toWeekViewEvent());
                    eventMap.put(event.getId(), event);

                    Log.d(TAG, "onActivityResult:events " + events);
                    getWeekView().notifyDatasetChanged();

                } else if (data.hasExtra("finish")) {
                    EventModel event = (EventModel) data.getSerializableExtra("finish");
                    eventMap.put(event.getId(), event);
                }
            }
        } else if (requestCode == 4) {


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

        if (mDrawerList.getCheckedItemPosition() >= 0) {
            Log.d(TAG, "onEventClick: " + event);
            EventModel ev = eventMap.get(event.getId());
            Intent intent = new Intent(getApplicationContext(), EventDetailActivity.class);
            intent.putExtra(EVENT, ev);
            intent.putExtra(TEAM, myTeams.get(mDrawerList.getCheckedItemPosition()).getId());
            intent.putExtra(NAMETEAM, myTeams.get(mDrawerList.getCheckedItemPosition()).getName());
            startActivityForResult(intent, 3);
        }
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }


}
