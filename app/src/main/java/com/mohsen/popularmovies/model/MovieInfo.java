package com.mohsen.popularmovies.model;

import android.graphics.Movie;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mohsen on 21.02.2018.
 *
 */

public class MovieInfo {

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w185";

    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("poster_path")
    private String posterRelativePath;
    @SerializedName("overview")
    private String overview;
    @SerializedName("vote_average")
    private String voteAverage;
    @SerializedName("release_date")
    private String releaseDate;

    public MovieInfo(String id, String title, String originalTitle, String posterRelativePath, String overview, String voteAverage, String releaseDate) {
        this.id = id;
        this.title = title;
        this.originalTitle = originalTitle;
        this.posterRelativePath = posterRelativePath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public String getId() { return id; }

    public String getTitle() { return title; }

    public String getOriginalTitle() { return originalTitle; }

    public String getOverview() { return overview; }

    public String getVoteAverage() { return voteAverage; }

    public String getReleaseDate() { return releaseDate; }

    public String getPosterRelativePath() { return posterRelativePath; }

    /**
     *
     * @param relativePath path written by API
     * @return Absolute path of the movie poster otherwise null.
     */
    public static String posterPathConverter(String relativePath) {
        return POSTER_BASE_URL + POSTER_SIZE + relativePath;
    }

}
