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
import java.io.FilterInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import com.jakewharton.disklrucache.DiskLruCache;

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
        Log.d(TAG, "client: " + cache);
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (isOnline(context)) {
                    return chain.proceed(
                            request.newBuilder()
                                    .addHeader("Cache-Control", "public, max-stale=" + TimeUnit.MILLISECONDS.toSeconds(getMaxStale()))
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


    public static Long getMaxStale() {
        return (long) (60 * 1000 * 5);
    }

    public static Integer getCacheFileSize() {
        return 10 * 1024 * 1024;
    }

    /**
     * Return cache from listed cache files.
     *
     * @param context      for get from cache directory
     * @param url          for get from a endpoint too
     * @param endpointName for get the correct dir
     * @return Return String of cache.
     */
    public static String getFromCache(Context context, HttpUrl url, String endpointName) {
        try {
            DiskLruCache cache = DiskLruCache.open(new File(context.getCacheDir(), endpointName), 201105, 2,
                    getCacheFileSize()
            );
            cache.flush();
            String key = Cache.key(url);
            final DiskLruCache.Snapshot snapshot;
            snapshot = cache.get(key);
            if (snapshot == null) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "getFromCache: null" + cache.size());
                    Log.i(TAG, "getFromCache: " + url);
                    Log.i(TAG, "getFromCache: " + endpointName);
                } else {
                    Log.i(TAG, "getFromCache: not null");
                }
                return null;
            }
            FilterInputStream bodyIn = new FilterInputStream(snapshot.getInputStream(1)) {
                @Override
                public void close() throws IOException {
                    snapshot.close();
                    super.close();
                }
            };
            Scanner sc = new Scanner(bodyIn);
            String str = "";
            String strNext;
            while (sc.hasNext() && (strNext = sc.nextLine()) != null) {
                str = str + strNext;
            }
            Log.d(TAG, "getFromCache: " + str);
            return str;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


}
