package com.cineplanner.kevin.cineplanner.team;

import android.annotation.SuppressLint;
import android.graphics.Color;

import com.alamkanak.weekview.WeekViewEvent;
import com.cineplanner.kevin.cineplanner.R;
import com.cineplanner.kevin.cineplanner.event.MovieModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.w3c.dom.Comment;

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

    @SerializedName("creatorId")
    @Expose
    private int creatorId;
    @SerializedName("start")
    @Expose
    private long start;
    @SerializedName("end")
    @Expose
    private long end;
    @SerializedName("comments")
    @Expose
    private ArrayList<CommentModel> comments;
    @SerializedName("notations")
    @Expose
    private ArrayList<NotationModel> notations;
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


    public MovieModel getMovie() {
        return movie;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public ArrayList<CommentModel> getComments() {
        return comments;
    }

    public ArrayList<NotationModel> getNotations() {
        return notations;
    }

    public void setComments(ArrayList<CommentModel> comments) {
        this.comments = comments;
    }

    public void setNotations(ArrayList<NotationModel> notations) {
        this.notations = notations;
    }

    public void setMovie(MovieModel movie) {
        this.movie = movie;
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
        if (creatorId != that.creatorId) return false;
        if (movie != null ? !movie.equals(that.movie) : that.movie != null) return false;
        if (comments != null ? !comments.equals(that.comments) : that.comments != null)
            return false;
        return notations != null ? notations.equals(that.notations) : that.notations == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (int) (start ^ (start >>> 32));
        result = 31 * result + (int) (end ^ (end >>> 32));
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        result = 31 * result + (notations != null ? notations.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EventModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creator='" + creator + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", comments=" + comments +
                ", notations=" + notations +
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
