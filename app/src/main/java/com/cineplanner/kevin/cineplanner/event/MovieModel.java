package com.cineplanner.kevin.cineplanner.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Kevin on 24/09/2017 for CinePlanner.
 */

public class MovieModel implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("vote_count")
    @Expose
    float vote_count;

    @SerializedName("vote_average")
    @Expose
    float vote_average;

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("popularity")
    @Expose
    float popularity;

    @SerializedName("poster_path")
    @Expose
    String poster_path;

    @SerializedName("original_language")
    @Expose
    String original_language;

    @SerializedName("original_title")
    @Expose
    String original_title;

    @SerializedName("backdrop_path")
    @Expose
    String backdrop_path;

    @SerializedName("overview")
    @Expose
    String overview;

    @SerializedName("release_date")
    @Expose
    String release_date;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MovieModel that = (MovieModel) o;

        if (id != that.id) return false;
        if (Float.compare(that.vote_count, vote_count) != 0) return false;
        if (Float.compare(that.vote_average, vote_average) != 0) return false;
        if (Float.compare(that.popularity, popularity) != 0) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (poster_path != null ? !poster_path.equals(that.poster_path) : that.poster_path != null)
            return false;
        if (original_language != null ? !original_language.equals(that.original_language) : that.original_language != null)
            return false;
        if (original_title != null ? !original_title.equals(that.original_title) : that.original_title != null)
            return false;
        if (backdrop_path != null ? !backdrop_path.equals(that.backdrop_path) : that.backdrop_path != null)
            return false;
        if (overview != null ? !overview.equals(that.overview) : that.overview != null)
            return false;
        return release_date != null ? release_date.equals(that.release_date) : that.release_date == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (vote_count != +0.0f ? Float.floatToIntBits(vote_count) : 0);
        result = 31 * result + (vote_average != +0.0f ? Float.floatToIntBits(vote_average) : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (popularity != +0.0f ? Float.floatToIntBits(popularity) : 0);
        result = 31 * result + (poster_path != null ? poster_path.hashCode() : 0);
        result = 31 * result + (original_language != null ? original_language.hashCode() : 0);
        result = 31 * result + (original_title != null ? original_title.hashCode() : 0);
        result = 31 * result + (backdrop_path != null ? backdrop_path.hashCode() : 0);
        result = 31 * result + (overview != null ? overview.hashCode() : 0);
        result = 31 * result + (release_date != null ? release_date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MovieModel{" +
                "id=" + id +
                ", vote_count=" + vote_count +
                ", vote_average=" + vote_average +
                ", title='" + title + '\'' +
                ", popularity=" + popularity +
                ", poster_path='" + poster_path + '\'' +
                ", original_language='" + original_language + '\'' +
                ", original_title='" + original_title + '\'' +
                ", backdrop_path='" + backdrop_path + '\'' +
                ", overview='" + overview + '\'' +
                ", release_date='" + release_date + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getVote_count() {
        return vote_count;
    }

    public void setVote_count(float vote_count) {
        this.vote_count = vote_count;
    }

    public float getVote_average() {
        return vote_average;
    }

    public void setVote_average(float vote_average) {
        this.vote_average = vote_average;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }
}
