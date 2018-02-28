package com.mohsen.popularmovies.database;

import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

/**
 * Created by Mohsen on 27.02.2018.
 *
 */

public final class MovieDetailsContract {
    private MovieDetailsContract() {}

    public static final String AUTHORITY = "com.mohsen.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movieInfo";
    public static final String PATH_VIDEOS = "movieVideos";

    public static class MovieInfoEntry implements BaseColumns {
        public static final Uri CONTENT_URI_MOVIES = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movieInfo";
        public static final String COLUMN_NAME_MOVIE_ID = "movieId";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_NAME_POSTER_PATH = "posterPath";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_NAME_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_NAME_REVIEWS = "reviews";
    }

    public static class MovieVideoEntry implements BaseColumns {
        public static final Uri CONTENT_URI_VIDEOS = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();

        public static final String TABLE_NAME = "movieVideos";
        public static final String COLUMN_NAME_MOVIE_ID = "movieId";
        public static final String COLUMN_NAME_YOUTUBE_ID = "youtubeId";
        public static final String COLUMN_NAME_NAME = "name";
    }
}
