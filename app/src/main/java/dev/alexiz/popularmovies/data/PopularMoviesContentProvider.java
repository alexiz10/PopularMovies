package dev.alexiz.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

import dev.alexiz.popularmovies.data.PopularMoviesContract.FavoriteEntry;

public class PopularMoviesContentProvider extends ContentProvider {

    public static final int FAVORITES = 100;
    public static final int FAVORITE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PopularMoviesContract.AUTHORITY, PopularMoviesContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(PopularMoviesContract.AUTHORITY, PopularMoviesContract.PATH_FAVORITES + "/#", FAVORITE_WITH_ID);

        return uriMatcher;
    }

    private PopularMoviesDbHelper popularMoviesDbHelper;

    @Override
    public boolean onCreate() {
        this.popularMoviesDbHelper = new PopularMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                            @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = this.popularMoviesDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor returnedCursor;

        switch (match) {
            case FAVORITES:
                returnedCursor = db.query(FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnedCursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return returnedCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = this.popularMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnedUri;

        switch (match) {
            case FAVORITES:
                long id = db.insert(FavoriteEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnedUri = ContentUris.withAppendedId(FavoriteEntry.CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = this.popularMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int deletedRows;

        switch (match) {
            case FAVORITES:
                deletedRows = db.delete(FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                deletedRows = db.delete(FavoriteEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (deletedRows != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = this.popularMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int updatedRows;

        switch (match) {
            case FAVORITES:
                updatedRows = db.update(FavoriteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                updatedRows = db.update(FavoriteEntry.TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (updatedRows != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        return updatedRows;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVORITES:
                return "vnd.android.cursor.dir/" + PopularMoviesContract.AUTHORITY + PopularMoviesContract.PATH_FAVORITES;
            case FAVORITE_WITH_ID:
                return "vnd.android.cursor.item/" + PopularMoviesContract.AUTHORITY + PopularMoviesContract.PATH_FAVORITES;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

}