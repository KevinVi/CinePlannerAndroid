package com.cineplanner.kevin.cineplanner.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.cineplanner.kevin.cineplanner.BoxLoading;
import com.cineplanner.kevin.cineplanner.BuildConfig;
import com.cineplanner.kevin.cineplanner.login.LoginActivity;
import com.cineplanner.kevin.cineplanner.login.LoginInterface;
import com.cineplanner.kevin.cineplanner.login.LoginModel;
import com.cineplanner.kevin.cineplanner.login.LoginTools;
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

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Kevin on 22/09/2017 for CinePlanner.
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
                    Log.d(TAG, "intercept: ");
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
        HttpLoggingInterceptor loginInterceptor = new HttpLoggingInterceptor();
        loginInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(interceptor)
                .addInterceptor(loginInterceptor)
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

    public static void setUiInProgress(FragmentManager manager, BoxLoading alert, final boolean inProgress) {
        try {
            Log.d(TAG, "setUiInProgress: " + alert);
            if (inProgress) {
                alert.show(manager, "tag");
                alert.setCancelable(false);
            } else {
                Log.d(TAG, "setUiInProgress: " + alert);
                alert.dismiss();

            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }
}
