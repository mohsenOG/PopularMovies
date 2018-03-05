package com.mohsen.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mohsen on 23.02.2018.
 *
 */

public class MovieReview implements Parcelable {
    @SerializedName("id")
    private final String id;
    @SerializedName("author")
    private final String author;
    @SerializedName("content")
    private final String content;

    public MovieReview(String id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    String getAuthor() { return author; }

    public String getContent() { return content; }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.author);
        dest.writeString(this.content);
    }

    private MovieReview(Parcel in) {
        this.id = in.readString();
        this.author = in.readString();
        this.content = in.readString();
    }

    static final Parcelable.Creator<MovieReview> CREATOR = new Parcelable.Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel source) {
            return new MovieReview(source);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };
}
