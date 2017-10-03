package com.cineplanner.kevin.cineplanner.suggestion;

/**
 * Created by Kevin on 03/10/2017 for ZKY.
 */

public class SuggestionModel {
    int idTeam ;
    int idMovie;
    boolean liked;

    public SuggestionModel(int idTeam, int idMovie, boolean liked) {
        this.idTeam = idTeam;
        this.idMovie = idMovie;
        this.liked = liked;
    }

    public int getIdTeam() {
        return idTeam;
    }

    public void setIdTeam(int idTeam) {
        this.idTeam = idTeam;
    }

    public int getIdMovie() {
        return idMovie;
    }

    public void setIdMovie(int idMovie) {
        this.idMovie = idMovie;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
