package dev.alexiz.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import dev.alexiz.popularmovies.R;
import dev.alexiz.popularmovies.models.Movie;
import dev.alexiz.popularmovies.utils.NetworkUtils;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ArrayList<Movie> movies = new ArrayList<>();

    private final MovieAdapterOnClickHandler clickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    @NonNull
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_grid_item, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        String posterPath = this.movies.get(position).getPosterPath();
        Context context = holder.posterImageView.getContext();
        Glide.with(context).load(NetworkUtils.buildPosterUri(posterPath))
                .into(holder.posterImageView);
    }

    @Override
    public int getItemCount() {
        if (this.movies.isEmpty()) {
            return 0;
        }
        return this.movies.size();
    }

    public void setMovieData(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView posterImageView;

        MovieAdapterViewHolder(View view) {
            super(view);
            posterImageView = view.findViewById(R.id.iv_movie);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movie = movies.get(adapterPosition);
            clickHandler.onClick(movie);
        }

    }

}