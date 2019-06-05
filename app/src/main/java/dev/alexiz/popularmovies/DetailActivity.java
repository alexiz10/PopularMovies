package dev.alexiz.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import dev.alexiz.popularmovies.adapters.ReviewAdapter;
import dev.alexiz.popularmovies.adapters.TrailerAdapter;
import dev.alexiz.popularmovies.data.PopularMoviesContract.FavoriteEntry;
import dev.alexiz.popularmovies.models.Movie;
import dev.alexiz.popularmovies.models.Review;
import dev.alexiz.popularmovies.models.Trailer;
import dev.alexiz.popularmovies.utils.JsonUtils;
import dev.alexiz.popularmovies.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler,
    ReviewAdapter.ReviewAdapterOnClickHandler {

    private static final String MOVIE_INTENT_KEY = "movie";

    private Movie movie;

    private String movieId;

    private RecyclerView trailersRecyclerView;
    private RecyclerView reviewsRecyclerView;

    private TextView trailersErrorMessageTextView;
    private TextView reviewsErrorMessageTextView;

    private ProgressBar trailersLoadingIndicator;
    private ProgressBar reviewsLoadingIndicator;

    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private Button favoriteButton;

    private boolean movieInFavorites = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        ImageView backdropImageView = findViewById(R.id.iv_detail_backdrop_image);
        ImageView posterImageView = findViewById(R.id.iv_detail_image);
        TextView titleTextView = findViewById(R.id.tv_detail_title);
        TextView releaseDateTextView = findViewById(R.id.tv_detail_release_date);
        TextView ratingTextView = findViewById(R.id.tv_detail_rating);
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
                ratingTextView.setText(String.valueOf(this.movie.getRating()) + "/10");
                synopsisTextView.setText(this.movie.getSynopsis());
                movieId = Integer.toString(this.movie.getId());
            }
        }

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        this.trailersRecyclerView = findViewById(R.id.rv_trailers);
        this.trailersRecyclerView.setLayoutManager(trailerLayoutManager);
        this.trailersRecyclerView.setHasFixedSize(true);
        this.trailersRecyclerView.setNestedScrollingEnabled(false);

        this.reviewsRecyclerView = findViewById(R.id.rv_reviews);
        this.reviewsRecyclerView.setLayoutManager(reviewLayoutManager);
        this.reviewsRecyclerView.setHasFixedSize(true);
        this.reviewsRecyclerView.setNestedScrollingEnabled(false);

        this.trailersErrorMessageTextView = findViewById(R.id.tv_trailer_error_message);
        this.reviewsErrorMessageTextView = findViewById(R.id.tv_review_error_message);

        this.trailersLoadingIndicator = findViewById(R.id.pb_trailer_loading_indicator);
        this.reviewsLoadingIndicator = findViewById(R.id.pb_review_loading_indicator);

        this.trailerAdapter = new TrailerAdapter(this);
        this.reviewAdapter = new ReviewAdapter(this);

        this.trailersRecyclerView.setAdapter(this.trailerAdapter);
        this.reviewsRecyclerView.setAdapter(this.reviewAdapter);

        if (NetworkUtils.isConnected(this)) {
            loadTrailerData();
            loadReviewData();
        } else {
            showTrailerErrorMessage();
            showReviewErrorMessage();
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

    private void loadTrailerData() {
        showTrailers();

        new FetchMovieTrailersTask().execute();
    }

    private void loadReviewData() {
        showReviews();

        new FetchMovieReviewsTask().execute();
    }

    private void showTrailers() {
        this.trailersRecyclerView.setVisibility(View.VISIBLE);
        this.trailersErrorMessageTextView.setVisibility(View.INVISIBLE);
    }

    private void showReviews() {
        this.reviewsRecyclerView.setVisibility(View.VISIBLE);
        this.reviewsErrorMessageTextView.setVisibility(View.INVISIBLE);
    }

    private void showTrailerErrorMessage() {
        this.trailersRecyclerView.setVisibility(View.INVISIBLE);
        this.trailersErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void showReviewErrorMessage() {
        this.reviewsRecyclerView.setVisibility(View.INVISIBLE);
        this.reviewsErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Trailer trailer) {
        Intent intentToPlayVideo = new Intent(Intent.ACTION_VIEW, NetworkUtils.buildTrailerVideoUri(trailer.getKey()));
        startActivity(intentToPlayVideo);
    }

    @Override
    public void onClick(Review review) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.review_dialog_layout, null);

        TextView authorTextView = view.findViewById(R.id.tv_review_dialog_author);
        TextView contentTextView = view.findViewById(R.id.tv_review_dialog_content);

        authorTextView.setText(review.getAuthor());
        contentTextView.setText(review.getContent());

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setFavoriteButtonText() {
        if (this.movieInFavorites) {
            this.favoriteButton.setText(R.string.details_remove_from_favorites);
        } else {
            this.favoriteButton.setText(R.string.details_add_to_favorites);
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

    private class FetchMovieTrailersTask extends AsyncTask<Void, Void, ArrayList<Trailer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            trailersLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> trailers) {
            trailersLoadingIndicator.setVisibility(View.INVISIBLE);
            if (trailers != null && !trailers.isEmpty()) {
                showTrailers();
                trailerAdapter.setTrailerData(trailers);
            } else {
                showTrailerErrorMessage();
            }
        }

        @Override
        protected ArrayList<Trailer> doInBackground(Void... params) {
            try {
                URL trailersUrl = NetworkUtils.buildTrailersUrl(movieId);

                String trailersResponse = NetworkUtils.getResponseFromHttp(trailersUrl);

                return JsonUtils.getTrailers(trailersResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    private class FetchMovieReviewsTask extends AsyncTask<Void, Void, ArrayList<Review>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            reviewsLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviews) {
            reviewsLoadingIndicator.setVisibility(View.INVISIBLE);
            if (reviews != null && !reviews.isEmpty()) {
                showReviews();
                reviewAdapter.setReviewData(reviews);
            } else {
                showReviewErrorMessage();
            }
        }

        @Override
        protected ArrayList<Review> doInBackground(Void... params) {
            try {
                URL reviewsUrl = NetworkUtils.buildReviewsURL(movieId);

                String reviewsResponse = NetworkUtils.getResponseFromHttp(reviewsUrl);

                return JsonUtils.getReviews(reviewsResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

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