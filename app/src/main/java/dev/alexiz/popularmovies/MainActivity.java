package dev.alexiz.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

import dev.alexiz.popularmovies.adapters.MovieAdapter;
import dev.alexiz.popularmovies.data.PopularMoviesContract.FavoriteEntry;
import dev.alexiz.popularmovies.models.Movie;
import dev.alexiz.popularmovies.utils.JsonUtils;
import dev.alexiz.popularmovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener,
    MovieAdapter.MovieAdapterOnClickHandler {

    private static final String MOVIE_INTENT_KEY = "movie";

    private String sortMethod;
    private boolean favoritesOnly;

    private RecyclerView moviesRecyclerView;

    private TextView errorMessageTextView;

    private ProgressBar loadingIndicator;

    private MovieAdapter adapter;

    private FloatingActionButton settingsFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSharedPreferences();

        GridLayoutManager layoutManager;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        } else {
            layoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
        }

        this.settingsFab = findViewById(R.id.settings_fab);
        this.settingsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsActivity();
            }
        });

        this.moviesRecyclerView = findViewById(R.id.rv_movies);
        this.moviesRecyclerView.setLayoutManager(layoutManager);
        this.moviesRecyclerView.setHasFixedSize(true);

        this.errorMessageTextView = findViewById(R.id.tv_main_error_message);

        this.loadingIndicator = findViewById(R.id.pb_main_loading_indicator);

        this.adapter = new MovieAdapter(this);

        this.moviesRecyclerView.setAdapter(this.adapter);

        if (this.favoritesOnly) {
            loadFavoriteMovieData();
        } else {
            if (NetworkUtils.isConnected(this)) {
                loadMovieData();
            } else {
                showErrorMessage();
            }
        }
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.sortMethod = sharedPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_key_popularity));
        this.favoritesOnly = sharedPreferences.getBoolean(getString(R.string.pref_favorites_only_key), getResources().getBoolean(R.bool.pref_favorites_only_default));

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void loadMovieData() {
        showMovies();

        new FetchMovieDataTask().execute();
    }

    private void loadFavoriteMovieData() {
        showMovies();

        new FetchFavoriteMovieDataTask().execute();
    }

    private void showMovies() {
        this.errorMessageTextView.setVisibility(View.INVISIBLE);
        this.moviesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        if (this.favoritesOnly) {
            this.errorMessageTextView.setText(R.string.error_message_favorite_movies);
        } else {
            this.errorMessageTextView.setText(R.string.error_message_loading_movies);
        }
        this.moviesRecyclerView.setVisibility(View.INVISIBLE);
        this.errorMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intentToDetailActivity = new Intent(this, DetailActivity.class);
        intentToDetailActivity.putExtra(MOVIE_INTENT_KEY, movie);
        startActivity(intentToDetailActivity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    private void openSettingsActivity() {
        Intent startSettingsActivityIntent = new Intent(this, SettingsActivity.class);
        startActivity(startSettingsActivityIntent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_key))) {
            this.sortMethod = sharedPreferences.getString(key, getString(R.string.pref_sort_key_popularity));
        }
        if (key.equals(getString(R.string.pref_favorites_only_key))) {
            this.favoritesOnly = sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_favorites_only_default));
        }
        if (this.favoritesOnly) {
            loadFavoriteMovieData();
        } else {
            if (NetworkUtils.isConnected(this)) {
                loadMovieData();
            } else {
                showErrorMessage();
            }
        }
    }

    private class FetchMovieDataTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null && !movies.isEmpty()) {
                showMovies();
                adapter.setMovieData(movies);
            } else {
                showErrorMessage();
            }
        }

        @Override
        protected ArrayList<Movie> doInBackground(Void... params) {
            URL requestUrl = NetworkUtils.buildQueryUrl(sortMethod);

            try {
                String response = NetworkUtils.getResponseFromHttp(requestUrl);

                return JsonUtils.getMovies(response);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    public class FetchFavoriteMovieDataTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null && !movies.isEmpty()) {
                showMovies();
                adapter.setMovieData(movies);
            } else {
                showErrorMessage();
            }
        }

        @Override
        protected ArrayList<Movie> doInBackground(Void... params) {
            ArrayList<Movie> movies = new ArrayList<>();

            try {
                Cursor cursor = getApplicationContext().getContentResolver().query(FavoriteEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

                if (cursor != null) {
                    int idColumn = cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_ID);
                    int titleColumn = cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_TITLE);
                    int posterPathColumn = cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_POSTER_PATH);
                    int backdropPathColumn = cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_BACKDROP_PATH);
                    int synopsisColumn = cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_SYNOPSIS);
                    int releaseDateColumn = cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE);
                    int ratingColumn = cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_RATING);
                    int popularityColumn = cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_POPULARITY);

                    while (cursor.moveToNext()) {
                        movies.add(new Movie(cursor.getInt(idColumn), cursor.getString(titleColumn),
                                cursor.getString(posterPathColumn), cursor.getString(backdropPathColumn),
                                cursor.getString(synopsisColumn), cursor.getString(releaseDateColumn),
                                cursor.getDouble(ratingColumn), cursor.getFloat(popularityColumn)));
                    }
                    cursor.close();
                }
                return movies;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

    }

}