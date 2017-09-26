package com.cineplanner.kevin.cineplanner.team;

import android.annotation.SuppressLint;
import android.graphics.Color;

import com.alamkanak.weekview.WeekViewEvent;
import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.event.MovieModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Kevin on 20/09/2017 for ZKY.
 */

public class EventModel implements Serializable {


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
    @SerializedName("movie")
    @Expose
    private MovieModel movie;


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public ArrayList<String> getPreComments() {
        return preComments;
    }

    public ArrayList<String> getPostComments() {
        return postComments;
    }

    public MovieModel getMovie() {
        return movie;
    }

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
        if (movie != null ? !movie.equals(that.movie) : that.movie != null) return false;
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
                ", movie=" + movie +
                '}';
    }

    @SuppressLint("SimpleDateFormat")
    public WeekViewEvent toWeekViewEvent() {


        // Initialize start and end time.
        Calendar now = Calendar.getInstance();
        Calendar startTime = (Calendar) now.clone();
        startTime.setTimeInMillis(getStart());
        Calendar endTime = (Calendar) startTime.clone();
        endTime.setTimeInMillis(getEnd());

        // Create an week view event.
        String title = getName();
        if (getMovie() != null) {
            title += "\n" + getMovie().getTitle();
        }
        WeekViewEvent weekViewEvent = new WeekViewEvent(getId(), title, startTime, endTime);
        weekViewEvent.setColor(R.color.colorPrimary);

        return weekViewEvent;
    }
}
