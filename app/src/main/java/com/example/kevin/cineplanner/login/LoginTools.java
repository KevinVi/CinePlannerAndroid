package com.example.kevin.cineplanner.login;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Kevin on 20/09/2017 for ZKY.
 */

public class LoginTools {
    private static final String TAG = "LoginTools";
    private static final String PREF_LOGIN = "PREF_LOGIN";

    private static final String PREF_LOGIN_LOGIN = "PREF_LOGIN_LOGIN";
    private static final String PREF_LOGIN_PASSWORD = "PREF_LOGIN_PASSWORD";
    private static final String PREF_LOGIN_TOKEN = "PREF_LOGIN_TOKEN";

    /**
     * Save user password and logn in sharedPreferences.
     *
     * @param context   need for SharedPreferences
     * @param idLogin   id user
     * @param passLogin pass user
     */
    public static void saveLoginInfo(Context context, String idLogin, String passLogin) {
        final SharedPreferences loginPreferences = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE);
        final SharedPreferences.Editor prefsEditor = loginPreferences.edit();
        prefsEditor.putString(PREF_LOGIN_LOGIN, idLogin);
        prefsEditor.putString(PREF_LOGIN_PASSWORD, passLogin);
        prefsEditor.apply();
    }

    /**
     * Save user token sharedPreferences.
     *
     * @param context need for SharedPreferences
     * @param token   token user
     */
    public static void setToken(Context context, String token) {
        final SharedPreferences loginPreferences = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE);
        final SharedPreferences.Editor prefsEditor = loginPreferences.edit();
        prefsEditor.putString(PREF_LOGIN_TOKEN, token);
        prefsEditor.apply();
    }


    /**
     * Get login id for sharedPreferences.
     *
     * @param context need for SharedPreferences
     * @return id
     */
    public static String getLogin(Context context) {
        final SharedPreferences loginPreferences = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE);
        return loginPreferences.getString(PREF_LOGIN_LOGIN, "");
    }

    /**
     * Get login pass for sharedPreferences.
     *
     * @param context need for SharedPreferences
     * @return pass
     */
    public static String getPass(Context context) {
        final SharedPreferences loginPreferences = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE);
        return loginPreferences.getString(PREF_LOGIN_PASSWORD, "");
    }


    /**
     * Get token for sharedPreferences.
     *
     * @param context  need for SharedPreferences
     * @return token
     */
    public static String getToken(Context context) {
        final SharedPreferences loginPreferences = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE);
        return loginPreferences.getString(PREF_LOGIN_TOKEN, "");
    }

    /**
     * Delete user Login info sharedPreferences.
     *
     * @param context need for SharedPreferences
     */
    public static void deleteLoginInfo(Context context) {
        final SharedPreferences loginPreferences = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE);
        final SharedPreferences.Editor prefsEditor = loginPreferences.edit().clear();
        prefsEditor.apply();
    }


}
