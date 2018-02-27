package com.mohsen.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.mohsen.popularmovies.database.MovieDetailsContract.*;

/**
 * Created by Mohsen on 27.02.2018.
 *
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_INFO_TABLE = "CREATE TABLE " +
                MovieInfoEntry.TABLE_NAME + " (" +
                MovieInfoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieInfoEntry.COLUMN_NAME_MOVIE_ID + " TEXT," +
                MovieInfoEntry.COLUMN_NAME_TITLE + " TEXT," +
                MovieInfoEntry.COLUMN_NAME_ORIGINAL_TITLE + " TEXT," +
                MovieInfoEntry.COLUMN_NAME_POSTER_PATH + " TEXT," +
                MovieInfoEntry.COLUMN_NAME_OVERVIEW + " TEXT," +
                MovieInfoEntry.COLUMN_NAME_VOTE_AVERAGE + " TEXT," +
                MovieInfoEntry.COLUMN_NAME_RELEASE_DATE + " TEXT," +
                MovieInfoEntry.COLUMN_NAME_REVIEWS + " TEXT" +
                ");";

        final String SQL_CREATE_MOVIE_VIDEO_TABLE = "CREATE TABLE " +
                MovieVideoEntry.TABLE_NAME + " (" +
                MovieVideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieVideoEntry.COLUMN_NAME_MOVIE_ID + " TEXT," +
                MovieVideoEntry.COLUMN_NAME_NAME + " TEXT," +
                MovieVideoEntry.COLUMN_NAME_YOUTUBE_ID + " TEXT" +
                ");";
        db.execSQL(SQL_CREATE_MOVIE_INFO_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_VIDEO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieInfoEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieVideoEntry.TABLE_NAME);
        onCreate(db);
    }
}
