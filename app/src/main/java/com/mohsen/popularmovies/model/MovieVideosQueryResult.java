package com.mohsen.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 *
 * Created by Mohsen on 23.02.2018.
 */

public class MovieVideosQueryResult implements Parcelable {
    @SerializedName("results")
    private final List<MovieVideo> result;

    public MovieVideosQueryResult(List<MovieVideo> result) { this.result = result; }

    public List<MovieVideo> getResult() { return result; }


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
