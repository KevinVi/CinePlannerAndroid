package com.cineplanner.kevin.cineplanner.login;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Kevin on 20/09/2017 for ZKY.
 */

public class LoginTools {
    private static final String TAG = "LoginTools";
    private static final String PREF_LOGIN = "PREF_LOGIN";

    private static final String PREF_USER = "PREF_USER";

    private static final String PREF_LOGIN_LOGIN = "PREF_LOGIN_LOGIN";
    private static final String PREF_LOGIN_PASSWORD = "PREF_LOGIN_PASSWORD";
    private static final String PREF_LOGIN_TOKEN = "PREF_LOGIN_TOKEN";
    private static final String PREF_SELECTED_TEAM = "PREF_SELECTED_TEAM";
    private static final String PREF_USER_FIRSTNAME = "PREF_USER_FIRSTNAME";
    private static final String PREF_USER_LASTNAME = "PREF_USER_LASTNAME";
    private static final String PREF_USER_ID = "PREF_USER_ID";
    private static final String PREF_LEARN = "PREF_LEARN";


    /**
     * Save user content in sharedPreferences.
     *
     * @param context   need for SharedPreferences
     * @param firstname firstname
     * @param lastname  lastname
     * @param id        id
     */
    public static void saveInfo(Context context, String firstname, String lastname, int id) {
        final SharedPreferences loginPreferences = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE);
        final SharedPreferences.Editor prefsEditor = loginPreferences.edit();
        prefsEditor.putString(PREF_USER_FIRSTNAME, firstname);
        prefsEditor.putString(PREF_USER_LASTNAME, lastname);
        prefsEditor.putInt(PREF_USER_ID, id);
        prefsEditor.apply();
    }

    /**
     * Get Id for sharedPreferences.
     *
     * @param context need for SharedPreferences
     * @return picture
     */
    public static int getIdUser(Context context) {
        final SharedPreferences loginPreferences = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE);
        return loginPreferences.getInt(PREF_USER_ID, 0);
    }



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
     * Save user team selected sharedPreferences.
     *
     * @param context need for SharedPreferences
     * @param team    long
     */
    public static void setSelectedTeam(Context context, int team) {
        final SharedPreferences loginPreferences = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE);
        final SharedPreferences.Editor prefsEditor = loginPreferences.edit();
        prefsEditor.putInt(PREF_SELECTED_TEAM, team);
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
     * @param context need for SharedPreferences
     * @return token
     */
    public static String getToken(Context context) {
        final SharedPreferences loginPreferences = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE);
        return loginPreferences.getString(PREF_LOGIN_TOKEN, "");
    }

    /**
     * Get team for sharedPreferences.
     *
     * @param context need for SharedPreferences
     * @return teamId
     */
    public static int getSelectedTeam(Context context) {
        final SharedPreferences loginPreferences = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE);
        return loginPreferences.getInt(PREF_SELECTED_TEAM, -1);
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
        final SharedPreferences loginPreferencesUser = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE);
        final SharedPreferences.Editor prefsEditorUser = loginPreferencesUser.edit().clear();
        prefsEditorUser.apply();
    }


}
