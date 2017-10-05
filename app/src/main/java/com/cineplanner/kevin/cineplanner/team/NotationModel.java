package com.cineplanner.kevin.cineplanner.team;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Kevin on 01/10/2017 for CinePlanner.
 */

public class NotationModel implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("authorId")
    @Expose
    private int authorId;
    @SerializedName("notation")
    @Expose
    private int notation;
    @SerializedName("eventId")
    @Expose
    private int eventId;

    public int getId() {
        return id;
    }

    public int getAuthorId() {
        return authorId;
    }

    public int getNotation() {
        return notation;
    }

    public int getEventId() {
        return eventId;
    }
}
