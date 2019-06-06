package dev.alexiz.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import dev.alexiz.popularmovies.data.PopularMoviesContract.FavoriteEntry;
import dev.alexiz.popularmovies.models.Movie;
import dev.alexiz.popularmovies.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity {

    private static final String MOVIE_INTENT_KEY = "movie";

    private Movie movie;

    private String movieId;

    private ImageButton favoriteButton;

    private boolean movieInFavorites = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView backdropImageView = findViewById(R.id.iv_detail_backdrop_image);
        ImageView posterImageView = findViewById(R.id.iv_detail_image);
        TextView titleTextView = findViewById(R.id.tv_detail_title);
        TextView releaseDateTextView = findViewById(R.id.tv_detail_release_date);
        RatingBar movieRatingBar = findViewById(R.id.rb_detail_rating);
        TextView synopsisTextView = findViewById(R.id.tv_detail_synopsis);

        Intent intent = getIntent();

        if (intent.hasExtra(MOVIE_INTENT_KEY)) {
            this.movie = Objects.requireNonNull(intent.getExtras()).getParcelable(MOVIE_INTENT_KEY);

            if (this.movie != null) {
                Glide.with(this)
                        .load(NetworkUtils.buildBackdropUri(this.movie.getBackdropPath()))
                        .into(backdropImageView);
                Glide.with(this)
                        .load(NetworkUtils.buildPosterUri(this.movie.getPosterPath()))
                        .into(posterImageView);
                titleTextView.setText(this.movie.getTitle());
                releaseDateTextView.setText(formatDate(this.movie.getReleaseDate()));
                movieRatingBar.setRating((float)this.movie.getRating());
                synopsisTextView.setText(this.movie.getSynopsis());
                movieId = Integer.toString(this.movie.getId());
            }
        }

        new CheckMovieInFavoritesTask().execute();

        this.favoriteButton = findViewById(R.id.btn_detail_favorite);
        this.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movieInFavorites) {
                    removeMovieFromFavorites();
                } else {
                    addMovieToFavorites();
                }
            }
        });
    }

    private void setFavoriteButtonText() {
        if (this.movieInFavorites) {
            this.favoriteButton.setImageResource(R.drawable.ic_bookmark_accent_24dp);
        } else {
            this.favoriteButton.setImageResource(R.drawable.ic_bookmark_border_accent_24dp);
        }
    }

    private void addMovieToFavorites() {
        ContentValues values = new ContentValues();
        values.put(FavoriteEntry.COLUMN_MOVIE_ID, this.movie.getId());
        values.put(FavoriteEntry.COLUMN_MOVIE_TITLE, this.movie.getTitle());
        values.put(FavoriteEntry.COLUMN_MOVIE_POSTER_PATH, this.movie.getPosterPath());
        values.put(FavoriteEntry.COLUMN_MOVIE_BACKDROP_PATH, this.movie.getBackdropPath());
        values.put(FavoriteEntry.COLUMN_MOVIE_SYNOPSIS, this.movie.getSynopsis());
        values.put(FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE, this.movie.getReleaseDate());
        values.put(FavoriteEntry.COLUMN_MOVIE_RATING, this.movie.getRating());
        values.put(FavoriteEntry.COLUMN_MOVIE_POPULARITY, this.movie.getPopularity());

        Uri uri = getContentResolver().insert(FavoriteEntry.CONTENT_URI, values);

        if (uri == null) {
            Toast.makeText(getBaseContext(), this.movie.getTitle() + getString(R.string.favorite_movie_could_not_be_added_to_favorites), Toast.LENGTH_SHORT).show();
        } else {
            this.movieInFavorites = true;
            setFavoriteButtonText();
        }
    }

    private void removeMovieFromFavorites() {
        int deleted = getContentResolver().delete(FavoriteEntry.CONTENT_URI,
                FavoriteEntry.COLUMN_MOVIE_ID + " = " + this.movieId,
                null);

        if (deleted > 0) {
            this.movieInFavorites = false;
            setFavoriteButtonText();
        } else {
            Toast.makeText(getBaseContext(), this.movie.getTitle() + getString(R.string.favorite_movie_could_not_be_removed_from_favorites), Toast.LENGTH_SHORT).show();
        }
    }

    private String formatDate(String date) {
        Date currentDate = null;
        try {
            currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("MMM dd, yyyy").format(currentDate);
    }

    private class CheckMovieInFavoritesTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(FavoriteEntry.CONTENT_URI,
                        null,
                        FavoriteEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{movieId},
                        null);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return !(cursor == null || cursor.getCount() < 1);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            movieInFavorites = aBoolean;
            setFavoriteButtonText();
        }

    }

}