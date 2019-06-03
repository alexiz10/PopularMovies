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
import dev.alexiz.popularmovies.models.Trailer;
import dev.alexiz.popularmovies.utils.NetworkUtils;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private ArrayList<Trailer> trailers = new ArrayList<>();

    private final TrailerAdapterOnClickHandler clickHandler;

    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer trailer);
    }

    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    @NonNull
    public TrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_thumbnail_item, parent, false);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapterViewHolder holder, int position) {
        String key = trailers.get(position).getKey();
        Context context = holder.trailerThumbnailImageView.getContext();
        Glide.with(context).load(NetworkUtils.buildTrailerThumbnailUri(key))
                .into(holder.trailerThumbnailImageView);
    }

    @Override
    public int getItemCount() {
        if (this.trailers.isEmpty()) {
            return 0;
        }
        return this.trailers.size();
    }

    public void setTrailerData(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView trailerThumbnailImageView;

        TrailerAdapterViewHolder(View view) {
            super(view);
            trailerThumbnailImageView = view.findViewById(R.id.iv_trailer_thumbnail);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Trailer trailer = trailers.get(adapterPosition);
            clickHandler.onClick(trailer);
        }

    }

}