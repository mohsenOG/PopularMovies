package com.mohsen.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 *
 * Created by Mohsen on 23.02.2018.
 */

public class MovieVideosQueryResult implements Parcelable {

    public static final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";
    public static final String YOUTUBE_APP_URL = "vnd.youtube:";

    @SerializedName("results")
    private final List<MovieVideo> result;

    public MovieVideosQueryResult(List<MovieVideo> result) { this.result = result; }

    public List<MovieVideo> getResult() { return result; }

    public static String getYoutubeLink(@Nullable String youtubeId) {
        if (youtubeId == null || youtubeId.isEmpty()) return null;
        return new StringBuilder().append(YOUTUBE_URL).append(youtubeId).toString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.result);
    }

    protected MovieVideosQueryResult(Parcel in) {
        this.result = in.createTypedArrayList(MovieVideo.CREATOR);
    }

    public static final Parcelable.Creator<MovieVideosQueryResult> CREATOR = new Parcelable.Creator<MovieVideosQueryResult>() {
        @Override
        public MovieVideosQueryResult createFromParcel(Parcel source) {
            return new MovieVideosQueryResult(source);
        }

        @Override
        public MovieVideosQueryResult[] newArray(int size) {
            return new MovieVideosQueryResult[size];
        }
    };
}
