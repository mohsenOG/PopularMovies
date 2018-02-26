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
    @SerializedName("poster_path")
    private final String posterRelativePath;

    public MovieInfo(String id, String posterRelativePath) {
        this.id = id;
        this.posterRelativePath = posterRelativePath;
    }

    public String getId() { return id; }

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
        dest.writeString(this.posterRelativePath);
    }

    protected MovieInfo(Parcel in) {
        this.id = in.readString();
        this.posterRelativePath = in.readString();
    }

    // Make the class parcable! Auto-generated
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
