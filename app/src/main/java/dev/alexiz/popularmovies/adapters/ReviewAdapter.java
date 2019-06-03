package dev.alexiz.popularmovies.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import dev.alexiz.popularmovies.R;
import dev.alexiz.popularmovies.models.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private ArrayList<Review> reviews = new ArrayList<>();

    private final ReviewAdapterOnClickHandler clickHandler;

    public interface ReviewAdapterOnClickHandler {
        void onClick(Review review);
    }

    public ReviewAdapter(ReviewAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    @NonNull
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item_layout, parent, false);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder holder, int position) {
        String author = reviews.get(position).getAuthor();
        String content = reviews.get(position).getContent();
        holder.reviewAuthorTextView.setText(author);
        holder.reviewContentTextView.setText(content);
    }

    @Override
    public int getItemCount() {
        if (this.reviews.isEmpty()) {
            return 0;
        }
        return this.reviews.size();
    }

    public void setReviewData(ArrayList<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView reviewAuthorTextView;
        final TextView reviewContentTextView;

        ReviewAdapterViewHolder(View view) {
            super(view);
            this.reviewAuthorTextView = view.findViewById(R.id.tv_review_author);
            this.reviewContentTextView = view.findViewById(R.id.tv_review_content);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Review review = reviews.get(adapterPosition);
            clickHandler.onClick(review);
        }

    }

}