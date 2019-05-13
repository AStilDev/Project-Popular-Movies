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
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{

    private final MovieAdapterOnClickHandler mClickHandler;
    private ArrayList<Movie> mMovieData;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie selectedMovie);
    }

    public MovieAdapter(ArrayList<Movie> movies, MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        mMovieData = movies;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {
        Movie selectedMovie = mMovieData.get(position);

        movieAdapterViewHolder.mMovieTextView.setText(selectedMovie.getTitle());

        URL imageURL = NetworkUtils.buildImageUrl(selectedMovie.getPosterPath());
        Picasso.get().load(imageURL.toString()).into(movieAdapterViewHolder.mMovieImageView);
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null)
            return 0;

        return mMovieData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setMovies(ArrayList<Movie> movies){
        mMovieData = movies;
    }

    public List<Movie> getMovies(){
        return mMovieData;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mMovieTextView;
        private final ImageView mMovieImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mMovieTextView = view.findViewById(R.id.movie_text);
            mMovieImageView = view.findViewById(R.id.movie_image);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie selectedMovie = mMovieData.get(adapterPosition);
            mClickHandler.onClick(selectedMovie);
        }
    }
}
