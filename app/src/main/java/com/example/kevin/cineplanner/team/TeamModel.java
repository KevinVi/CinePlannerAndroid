package com.example.kevin.cineplanner.team;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Kevin on 20/09/2017 for ZKY.
 */

public class TeamModel {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("creator")
    @Expose
    private String creator;
    @SerializedName("pendingUsers")
    @Expose
    private ArrayList<String> pendingUsers;
    @SerializedName("events")
    @Expose
    private ArrayList<EventModel> events;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    public ArrayList<String> getPendingUsers() {
        return pendingUsers;
    }

    public ArrayList<EventModel> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        return "TeamModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creator='" + creator + '\'' +
                ", pendingUsers=" + pendingUsers +
                ", events=" + events +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TeamModel teamModel = (TeamModel) o;

        if (id != teamModel.id) return false;
        if (name != null ? !name.equals(teamModel.name) : teamModel.name != null) return false;
        if (creator != null ? !creator.equals(teamModel.creator) : teamModel.creator != null)
            return false;
        if (pendingUsers != null ? !pendingUsers.equals(teamModel.pendingUsers) : teamModel.pendingUsers != null)
            return false;
        return events != null ? events.equals(teamModel.events) : teamModel.events == null;

    }

}
