package com.example.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder>{

    private final TrailerAdapterOnClickHandler mClickHandler;
    private ArrayList<Trailer> mTrailerData;

    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer selectedTrailer);
    }

    public TrailerAdapter(ArrayList<Trailer> trailers, TrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        mTrailerData = trailers;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder trailerAdapterViewHolder, int position) {
        Trailer selectedTrailer = mTrailerData.get(position);

        trailerAdapterViewHolder.mTrailerTextView.setText(selectedTrailer.getName());
    }

    @Override
    public int getItemCount() {
        if (mTrailerData == null)
            return 0;

        return mTrailerData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mTrailerTextView;

        public TrailerAdapterViewHolder(View view) {
            super(view);
            mTrailerTextView = view.findViewById(R.id.trailer_text);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Trailer selectedTrailer = mTrailerData.get(adapterPosition);
            mClickHandler.onClick(selectedTrailer);
        }
    }
}

