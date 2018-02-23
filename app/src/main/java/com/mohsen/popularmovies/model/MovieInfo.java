package com.mohsen.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mohsen on 21.02.2018.
 *
 */

public class MovieInfo implements Parcelable {

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w185";

    @SerializedName("id")
    private final String id;
    @SerializedName("title")
    private final String title;
    @SerializedName("original_title")
    private final String originalTitle;
    @SerializedName("poster_path")
    private final String posterRelativePath;
    @SerializedName("overview")
    private final String overview;
    @SerializedName("vote_average")
    private final String voteAverage;
    @SerializedName("release_date")
    private final String releaseDate;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.originalTitle);
        dest.writeString(this.posterRelativePath);
        dest.writeString(this.overview);
        dest.writeString(this.voteAverage);
        dest.writeString(this.releaseDate);
    }

    protected MovieInfo(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.originalTitle = in.readString();
        this.posterRelativePath = in.readString();
        this.overview = in.readString();
        this.voteAverage = in.readString();
        this.releaseDate = in.readString();
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR = new Parcelable.Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel source) {
            return new MovieInfo(source);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };
}
