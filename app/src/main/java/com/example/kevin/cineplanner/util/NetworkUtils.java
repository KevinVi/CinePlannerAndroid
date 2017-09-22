package com.example.kevin.cineplanner.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.example.kevin.cineplanner.BuildConfig;
import com.example.kevin.cineplanner.R;
import com.example.kevin.cineplanner.login.LoginActivity;
import com.example.kevin.cineplanner.login.LoginInterface;
import com.example.kevin.cineplanner.login.LoginModel;
import com.example.kevin.cineplanner.login.LoginTools;
import com.example.kevin.cineplanner.planning.PlanningActivity;
import com.ncornette.cache.OkCacheControl;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Kevin on 22/09/2017 for ZKY.
 */

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception ex) {
            return false;
        }
    }


    /**
     * Create client for okhttp.
     *
     * @param context   for app config and context
     * @param cacheName for set the name of endpoint
     * @return the client
     */
    public static OkHttpClient client(final Context context, final String cacheName) {
        File file = new File(context.getCacheDir(), cacheName);
        Cache cache = new Cache(file, getCacheFileSize());
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (isOnline(context)) {
                    return chain.proceed(
                            request.newBuilder()
                                    .addHeader("Cache-Control", "public, max-stale=" + 120)
                                    .build()
                    );
                } else {
                    int maxStale = Integer.MAX_VALUE;
                    return chain.proceed(
                            request.newBuilder()
                                    .addHeader("Cache-Control", "public, max-stale=" + maxStale)
                                    .build()
                    );
                }
            }
        };
        // Lib to change header fo cache
        return OkCacheControl.on(new OkHttpClient.Builder())
                .apply() // return to the OkHttpClient.Builder instance
                .cache(cache)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        request.cacheControl().noStore();
                        return chain.proceed(request);
                    }
                })
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, final Response responses) throws IOException {
                        String url;
                        url = BuildConfig.URL + "account/";
                        //String url = BuildConfig.BASE_LINK_API + "igs" + "/";
                        OkHttpClient.Builder builder = new OkHttpClient.Builder();
                        //TODO need set 15 second for real prod
                        builder.connectTimeout(90, TimeUnit.SECONDS).readTimeout(90, TimeUnit.SECONDS).writeTimeout(90, TimeUnit.SECONDS);
                        builder.addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = chain.request().newBuilder()
                                        .build();
                                return chain.proceed(request);
                            }
                        });

                        Retrofit.Builder retrofit = new Retrofit.Builder()
                                .client(builder.build())
                                .baseUrl(url)
                                .addConverterFactory(GsonConverterFactory.create());
                        LoginInterface postInterface = retrofit.build().create(LoginInterface.class);
                        Call<LoginModel> call = postInterface.getLogin(LoginTools.getLogin(context), LoginTools.getPass(context));
                        retrofit2.Response<LoginModel> response = call.execute();
                        if (response.isSuccessful()) {
                            LoginTools.setToken(context, response.body().getToken());
                            return responses.request().newBuilder()
                                    .header("X-Appscho-Token", response.body().getToken())
                                    .build();
                        } else {
                            if (response.code() == 401) {
                                Intent intent = new Intent(context, LoginActivity.class);
                                context.startActivity(intent);
                            }
                            return null;
                        }
                    }
                })
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
    }


    public Long getMaxStale() {
        return (long) (60 * 1000 * 5);
    }

    public static Integer getCacheFileSize() {
        return 10 * 1024 * 1024;
    }


}
