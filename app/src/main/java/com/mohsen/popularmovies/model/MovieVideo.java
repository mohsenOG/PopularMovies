package com.mohsen.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 *
 * Created by Mohsen on 23.02.2018.
 */

public class MovieVideo implements Parcelable {

    @SerializedName("id")
    private final String youtubeId;
    @SerializedName("name")
    private final String name;

    public MovieVideo(String youtubeId, String name) {
        this.youtubeId = youtubeId;
        this.name = name;
    }

    public String getYoutubeId() { return youtubeId; }

    public String getName() { return name; }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.youtubeId);
        dest.writeString(this.name);
    }

    protected MovieVideo(Parcel in) {
        this.youtubeId = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<MovieVideo> CREATOR = new Parcelable.Creator<MovieVideo>() {
        @Override
        public MovieVideo createFromParcel(Parcel source) {
            return new MovieVideo(source);
        }

        @Override
        public MovieVideo[] newArray(int size) {
            return new MovieVideo[size];
        }
    };
}
