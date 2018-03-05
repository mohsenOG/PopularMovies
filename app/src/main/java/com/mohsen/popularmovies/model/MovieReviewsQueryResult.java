package com.mohsen.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 *
 * Created by Mohsen on 23.02.2018.
 */

public class MovieReviewsQueryResult implements Parcelable {
    @SerializedName("results")
    private final List<MovieReview> results;

    public MovieReviewsQueryResult(List<MovieReview> results) {
        this.results = results;
    }

    private List<MovieReview> getResults() { return results; }

    public static String ReviewConverter (MovieReviewsQueryResult reviewsList) {
        StringBuilder ret = new StringBuilder();
        List<MovieReview> reviews = reviewsList.getResults();
        for (MovieReview review : reviews) {
            String author =  review.getAuthor();
            String content = review.getContent();
            StringBuilder builder = new StringBuilder();
            builder.append(author != null ? author : "")
                    .append(": ")
                    .append(content != null ? content : "");
            if (!builder.toString().isEmpty())
                builder.append("\n\n");

            ret.append(builder.toString());
        }

        return ret.toString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.results);
    }

    private MovieReviewsQueryResult(Parcel in) {
        this.results = in.createTypedArrayList(MovieReview.CREATOR);
    }

    public static final Parcelable.Creator<MovieReviewsQueryResult> CREATOR = new Parcelable.Creator<MovieReviewsQueryResult>() {
        @Override
        public MovieReviewsQueryResult createFromParcel(Parcel source) {
            return new MovieReviewsQueryResult(source);
        }

        @Override
        public MovieReviewsQueryResult[] newArray(int size) {
            return new MovieReviewsQueryResult[size];
        }
    };
}
