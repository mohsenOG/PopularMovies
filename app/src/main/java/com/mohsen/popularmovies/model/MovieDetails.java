package com.mohsen.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mohsen on 23.02.2018.
 *
 */

public class MovieDetails implements Parcelable {

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
    @SerializedName("videos")
    private final MovieVideosQueryResult videos;
    @SerializedName("reviews")
    private final MovieReviewsQueryResult reviews;

    //public String getId() { return id;}

    public String getTitle() { return title; }

    public String getOriginalTitle() { return originalTitle; }

    public String getPosterRelativePath() { return posterRelativePath; }

    public String getOverview() { return overview; }

    public String getVoteAverage() { return voteAverage; }

    public String getReleaseDate() { return releaseDate; }

    public MovieVideosQueryResult getVideos() { return videos; }

    public MovieReviewsQueryResult getReviews() { return reviews; }


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
        dest.writeParcelable(this.videos, flags);
        dest.writeParcelable(this.reviews, flags);
    }

    private MovieDetails(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.originalTitle = in.readString();
        this.posterRelativePath = in.readString();
        this.overview = in.readString();
        this.voteAverage = in.readString();
        this.releaseDate = in.readString();
        this.videos = in.readParcelable(MovieVideosQueryResult.class.getClassLoader());
        this.reviews = in.readParcelable(MovieReviewsQueryResult.class.getClassLoader());
    }

    public static final Parcelable.Creator<MovieDetails> CREATOR = new Parcelable.Creator<MovieDetails>() {
        @Override
        public MovieDetails createFromParcel(Parcel source) {
            return new MovieDetails(source);
        }

        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };
}