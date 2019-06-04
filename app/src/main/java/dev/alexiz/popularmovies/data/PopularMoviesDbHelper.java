package dev.alexiz.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import dev.alexiz.popularmovies.data.PopularMoviesContract.FavoriteEntry;

public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorite_movies.db";

    private static final int DATABASE_VERSION = 1;

    public PopularMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " +
                FavoriteEntry.TABLE_NAME + "(" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_RATING + " INTEGER NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_POPULARITY + " INTEGER NOT NULL" +
                ");";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }

}