package com.example.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder>{
    private final ReviewAdapterOnClickHandler mClickHandler;
    private ArrayList<Review> mReviewData;

    public interface ReviewAdapterOnClickHandler {
        void onClick(Review selectedReview);
    }

    public ReviewAdapter(ArrayList<Review> reviews, ReviewAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        mReviewData = reviews;
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder reviewAdapterViewHolder, int position) {
        Review selectedReview = mReviewData.get(position);

        reviewAdapterViewHolder.mAuthorTextView.setText(selectedReview.getAuthor());
        reviewAdapterViewHolder.mReviewTextView.setText(selectedReview.getContent());
    }

    @Override
    public int getItemCount() {
        if (mReviewData == null)
            return 0;

        return mReviewData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mReviewTextView;
        private final TextView mAuthorTextView;

        public ReviewAdapterViewHolder(View view) {
            super(view);
            mReviewTextView = view.findViewById(R.id.review_content_text);
            mAuthorTextView = view.findViewById(R.id.review_author_text);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Review selectedReview = mReviewData.get(adapterPosition);
            mClickHandler.onClick(selectedReview);
        }
    }
}
