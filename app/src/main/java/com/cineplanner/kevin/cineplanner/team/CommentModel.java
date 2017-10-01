package com.cineplanner.kevin.cineplanner.team;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Kevin on 01/10/2017 for ZKY.
 */

public class CommentModel {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("dateCreated")
    @Expose
    private String dateCreated;
    @SerializedName("comment")
    @Expose
    private String comment;

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getComment() {
        return comment;
    }
}
