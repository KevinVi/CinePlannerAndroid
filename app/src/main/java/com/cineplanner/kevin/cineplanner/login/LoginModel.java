package com.cineplanner.kevin.cineplanner.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Kevin on 17/09/2017 for ZKY.
 */

public class LoginModel {
    @SerializedName("token")
    @Expose
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "LoginModel{" +
                "token='" + token + '\'' +
                '}';
    }
}
