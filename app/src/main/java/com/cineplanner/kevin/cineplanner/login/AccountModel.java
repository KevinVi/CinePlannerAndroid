package com.cineplanner.kevin.cineplanner.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Kevin on 01/10/2017 for CinePlanner.
 */

public class AccountModel {
    @SerializedName("accountPublic")
    @Expose
    private Account account;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
