package com.mohsen.popularmovies.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Mohsen on 27.02.2018.
 *
 */

public class DatabaseWrapper {
    private SQLiteDatabase mDb = null;
    private MovieDbHelper dbHelper = null;

    public DatabaseWrapper(Context context, boolean isWritable) {
        dbHelper = new MovieDbHelper(context);
        if (isWritable)
            mDb = dbHelper.getWritableDatabase();
        else
            mDb = dbHelper.getReadableDatabase();
    }

    public Cursor query(String table, String[] columns, String selection,
                 String[] selectionArgs, String groupBy, String having,
                 String orderBy) {
        return mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public long insert(String table, String nullColumnHack, ContentValues values) {
        return mDb.insert(table, nullColumnHack, values);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        return mDb.delete(table, whereClause, whereArgs);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mDb.close();
        dbHelper.close();
    }
}
