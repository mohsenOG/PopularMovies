package com.mohsen.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.mohsen.popularmovies.database.MovieDetailsContract.MovieInfoEntry.*;
/**
 *
 * Created by Mohsen on 27.02.2018.
 */

public class MovieContentProvider extends ContentProvider {

    private static final int MOVIE_INFO = 100;
    private static final int MOVIE_INFO_WITH_ID = 101;

    private final UriMatcher sUriMatcher = buildUriMatcher();
    private DatabaseWrapper mDb;

    @Override
    public boolean onCreate() {
        mDb = new DatabaseWrapper(getContext(), true);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = sUriMatcher.match(uri);
        Cursor ret;
        String id;
        switch (match) {
            case MOVIE_INFO:
                ret = mDb.query(MovieDetailsContract.MovieInfoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_INFO_WITH_ID:
                id = uri.getPathSegments().get(1);
                ret = mDb.query(MovieDetailsContract.MovieInfoEntry.TABLE_NAME, projection, COLUMN_NAME_MOVIE_ID + "=?", new String[]{id}, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ret;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);
        Uri ret;
        long id;
        switch (match) {
            case MOVIE_INFO:
                id = mDb.insert(MovieDetailsContract.MovieInfoEntry.TABLE_NAME, null, values);
                if (id > 0)
                    ret = ContentUris.withAppendedId(CONTENT_URI_MOVIES, id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int deletedRowsNo;
        String id = uri.getPathSegments().get(1);
        switch (match) {
            case MOVIE_INFO_WITH_ID:
                deletedRowsNo = mDb.delete(MovieDetailsContract.MovieInfoEntry.TABLE_NAME, COLUMN_NAME_MOVIE_ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (deletedRowsNo != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return deletedRowsNo;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Directory & Single item
        uriMatcher.addURI(MovieDetailsContract.AUTHORITY, MovieDetailsContract.PATH_MOVIES, MOVIE_INFO);
        uriMatcher.addURI(MovieDetailsContract.AUTHORITY, MovieDetailsContract.PATH_MOVIES + "/#", MOVIE_INFO_WITH_ID);

        return uriMatcher;
    }

}
