package dev.alexiz.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class PopularMoviesContract {

    public static final String AUTHORITY = "dev.alexiz.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";

    private PopularMoviesContract(){}

    public static class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_POSTER_PATH = "movie_poster_path";
        public static final String COLUMN_MOVIE_BACKDROP_PATH = "movie_backdrop_path";
        public static final String COLUMN_MOVIE_SYNOPSIS = "movie_synopsis";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_MOVIE_RATING = "movie_rating";
        public static final String COLUMN_MOVIE_POPULARITY = "movie_popularity";

    }

}