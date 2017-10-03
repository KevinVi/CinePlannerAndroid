package com.cineplanner.kevin.cineplanner;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.cineplanner.kevin.cineplanner.login.LoginActivity;
import com.cineplanner.kevin.cineplanner.login.LoginInterface;
import com.cineplanner.kevin.cineplanner.login.LoginModel;
import com.cineplanner.kevin.cineplanner.login.LoginTools;
import com.cineplanner.kevin.cineplanner.planning.PlanningActivity;
import com.cineplanner.kevin.cineplanner.util.NetworkUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final int TIME_LOADER = 2000;
    private final int TIME_LOADER_FAIL = 5000;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (LoginTools.getToken(getApplicationContext()).isEmpty()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            }, TIME_LOADER);
        } else {

            if (!NetworkUtils.isOnline(getApplicationContext())) {

                if (!LoginTools.getToken(getApplicationContext()).isEmpty()) {
                    Intent intent = new Intent(getApplicationContext(), PlanningActivity.class);
                    startActivity(intent);
                }
            } else {

                String url = BuildConfig.URL + "account/";
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(90, TimeUnit.SECONDS).readTimeout(90, TimeUnit.SECONDS).writeTimeout(90, TimeUnit.SECONDS);
                Retrofit.Builder retrofit = new Retrofit.Builder()
                        .client(builder.build())
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create());
                LoginInterface postInterface = retrofit.build().create(LoginInterface.class);
                Call<LoginModel> call = postInterface.getLogin(LoginTools.getLogin(getApplicationContext()), LoginTools.getPass(getApplicationContext()));
                call.enqueue(new Callback<LoginModel>() {
                    @Override
                    public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                        if (response.isSuccessful()) {
                            LoginTools.setToken(getApplicationContext(), response.body().getToken());
                            Intent intent = new Intent(getApplicationContext(), PlanningActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginModel> call, Throwable t) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
