package com.cineplanner.kevin.cineplanner.login;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.cineplanner.kevin.cineplanner.BuildConfig;
import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.planning.PlanningActivity;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private AppCompatEditText loginId;
    private AppCompatEditText loginPass;
    private TextInputLayout loginIdInput;
    private TextInputLayout loginPassInput;
    private AppCompatButton loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginId = (AppCompatEditText) findViewById(R.id.login_id_input);
        loginPass = (AppCompatEditText) findViewById(R.id.password_id_input);
        loginIdInput = (TextInputLayout) findViewById(R.id.id_layout);
        loginPassInput = (TextInputLayout) findViewById(R.id.id_layout_password);
        loginBtn = (AppCompatButton) findViewById(R.id.login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        loginBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        final String username = loginId.getText().toString();
        final String password = loginPass.getText().toString();
        if (!username.isEmpty() && !password.isEmpty()) {

            String url = BuildConfig.URL + "account/";
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            Retrofit.Builder retrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create());
            LoginInterface postInterface = retrofit.build().create(LoginInterface.class);
            Call<LoginModel> call = postInterface.getLogin(username, password);
            call.enqueue(new Callback<LoginModel>() {
                @Override
                public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                    Log.d(TAG, "onResponse: " + response);
                    Log.d(TAG, "onResponse: " + response.body().getToken());
                    if (response.isSuccessful()) {
                        LoginTools.saveLoginInfo(getApplicationContext(),username,password);
                        LoginTools.setToken(getApplicationContext(),response.body().getToken());
                        Intent intent = new Intent(getApplicationContext(), PlanningActivity.class);
                        startActivity(intent);
                    } else {
                        loginBtn.setOnClickListener(LoginActivity.this);
                        Toast.makeText(LoginActivity.this, response.raw().toString(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginModel> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    loginBtn.setOnClickListener(LoginActivity.this);
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}
