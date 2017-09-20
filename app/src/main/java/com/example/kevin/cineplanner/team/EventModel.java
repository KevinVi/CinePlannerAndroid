package com.example.kevin.cineplanner.team;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Kevin on 20/09/2017 for ZKY.
 */

public class EventModel {


    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("creator")
    @Expose
    private String creator;
    @SerializedName("start")
    @Expose
    private long start;
    @SerializedName("end")
    @Expose
    private long end;
    @SerializedName("preComments")
    @Expose
    private ArrayList<String> preComments;
    @SerializedName("postComments")
    @Expose
    private ArrayList<String> postComments;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventModel that = (EventModel) o;

        if (id != that.id) return false;
        if (start != that.start) return false;
        if (end != that.end) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (creator != null ? !creator.equals(that.creator) : that.creator != null) return false;
        if (preComments != null ? !preComments.equals(that.preComments) : that.preComments != null)
            return false;
        return postComments != null ? postComments.equals(that.postComments) : that.postComments == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (int) (start ^ (start >>> 32));
        result = 31 * result + (int) (end ^ (end >>> 32));
        result = 31 * result + (preComments != null ? preComments.hashCode() : 0);
        result = 31 * result + (postComments != null ? postComments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EventModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creator='" + creator + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", preComments=" + preComments +
                ", postComments=" + postComments +
                '}';
    }
}
