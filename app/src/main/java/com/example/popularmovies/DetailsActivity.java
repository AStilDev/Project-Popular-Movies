package com.example.popularmovies;

import android.app.ProgressDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popularmovies.database.AppDatabase;
import com.example.popularmovies.database.AppExecutors;
import com.example.popularmovies.utilities.JSONUtils;
import com.example.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Activity for a movie object's details
 */
public class DetailsActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler,
        ReviewAdapter.ReviewAdapterOnClickHandler {

    private static final String TRAILER_ARRAY = "trailer_array";
    private static final String REVIEW_ARRAY = "review_array";
    TextView mTitleTextView;
    TextView mVoteAverageTextView;
    TextView mOverviewTextView;
    TextView mReleaseDateTextView;
    TextView mTrailerLabelTextView;
    TextView mFavoriteTextView;
    ImageView mPosterImageView;
    ImageView mTrailerArrowImageView;
    ImageView mReviewArrowImageView;
    ImageView mFavoriteStarImageView;
    LinearLayout mTrailerLinearLayout;
    LinearLayout mReviewLinearLayout;
    RecyclerView mTrailerRecyclerView;
    RecyclerView mReviewRecyclerView;
    TrailerAdapter mTrailerAdapter;
    ReviewAdapter mReviewAdapter;
    ArrayList<Trailer> mTrailers;
    ArrayList<Review> mReviews;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mTrailers = new ArrayList<>();
        mReviews = new ArrayList<>();

        mDb = AppDatabase.getInstance(this.getApplication());

        mTitleTextView = findViewById(R.id.title_tv);
        mVoteAverageTextView = findViewById(R.id.vote_average_tv);
        mOverviewTextView = findViewById(R.id.overview_tv);
        mReleaseDateTextView = findViewById(R.id.release_date_tv);
        mPosterImageView = findViewById(R.id.poster_details_iv);
        mTrailerLabelTextView = findViewById(R.id.trailer_label_tv);
        mFavoriteTextView = findViewById(R.id.favorite_tv);
        mTrailerArrowImageView = findViewById(R.id.trailer_arrow_image);
        mReviewArrowImageView = findViewById(R.id.review_arrow_image);
        mFavoriteStarImageView = findViewById(R.id.star_iv);
        mTrailerLinearLayout = findViewById(R.id.trailer_linear_layout);
        mReviewLinearLayout = findViewById(R.id.review_linear_layout);

        // trailer
        mTrailerRecyclerView = findViewById(R.id.trailers_recyclerView);
        LinearLayoutManager layoutManager1
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mTrailerRecyclerView.setLayoutManager(layoutManager1);
        mTrailerRecyclerView.setHasFixedSize(true);
        mTrailerRecyclerView.setVisibility(View.GONE); // hide on startup

        // review
        mReviewRecyclerView = findViewById(R.id.reviews_recyclerView);
        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewRecyclerView.setLayoutManager(layoutManager2);
        mReviewRecyclerView.setHasFixedSize(true);
        mReviewRecyclerView.setVisibility(View.GONE); // hide on startup

        // Trailer
        mTrailerArrowImageView.setImageResource(R.drawable.ic_keyboard_arrow_down_white_18dp); // down on startup
        mTrailerLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show/hide recyclerview
                if (mTrailerRecyclerView.getVisibility() == View.VISIBLE){
                    mTrailerRecyclerView.setVisibility(View.GONE);
                    mTrailerArrowImageView.setImageResource(R.drawable.ic_keyboard_arrow_down_white_18dp);
                } else {
                    mTrailerRecyclerView.setVisibility(View.VISIBLE);
                    mTrailerArrowImageView.setImageResource(R.drawable.ic_keyboard_arrow_up_white_18dp);
                }
            }
        });

        // Review
        mReviewArrowImageView.setImageResource(R.drawable.ic_keyboard_arrow_down_white_18dp); // down on startup
        mReviewLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show/hide recyclerview
                if (mReviewRecyclerView.getVisibility() == View.VISIBLE){
                    mReviewRecyclerView.setVisibility(View.GONE);
                    mReviewArrowImageView.setImageResource(R.drawable.ic_keyboard_arrow_down_white_18dp);
                } else {
                    mReviewRecyclerView.setVisibility(View.VISIBLE);
                    mReviewArrowImageView.setImageResource(R.drawable.ic_keyboard_arrow_up_white_18dp);
                }
            }
        });

        Bundle bundle = getIntent().getBundleExtra("Bundle");
        final Movie selectedMovie = bundle.getParcelable("SelectedMovie");

        if (selectedMovie == null) {
            closeOnError();
        }
        else {
            mTitleTextView.setText(selectedMovie.getTitle());
            mVoteAverageTextView.setText(String.format(Locale.getDefault(),
                    "%.1f", selectedMovie.getVoteAverage()));
            mOverviewTextView.setText(selectedMovie.getOverview());
            mReleaseDateTextView.setText(selectedMovie.getReleaseDate());

            if (savedInstanceState != null) {
                mTrailers = savedInstanceState.getParcelableArrayList(TRAILER_ARRAY);
                mReviews = savedInstanceState.getParcelableArrayList(REVIEW_ARRAY);
            } else {
                loadExtraMovieData(selectedMovie); // set initial data
            }

            // get initial star state
            MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
            final LiveData<Movie> retrievedMovie = viewModel.getMovie(selectedMovie.getId());
            retrievedMovie.observe(this, new Observer<Movie>(){
                @Override
                public void onChanged(@Nullable Movie movie) {
                    // put in observer?
                    retrievedMovie.removeObserver(this);
                    if (movie != null &&movie.getFavorited()){
                        mFavoriteStarImageView.setImageResource(R.drawable.ic_star_full_white_18dp);
                    } else {
                        mFavoriteStarImageView.setImageResource(R.drawable.ic_star_border_white_18dp);
                    }
                }
            });

            mFavoriteStarImageView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    toggleStar(selectedMovie);
                }
            });

            mFavoriteTextView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    toggleStar(selectedMovie);
                }
            });

            mTrailerAdapter = new TrailerAdapter(mTrailers, this);
            mTrailerRecyclerView.setAdapter(mTrailerAdapter);

            mReviewAdapter = new ReviewAdapter(mReviews, this);
            mReviewRecyclerView.setAdapter(mReviewAdapter);

            URL imageURL = NetworkUtils.buildImageUrl(selectedMovie.getPosterPath());
            Picasso.get().load(imageURL.toString()).into(mPosterImageView);

            setTitle(selectedMovie.getTitle());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(TRAILER_ARRAY, mTrailers);
        savedInstanceState.putParcelableArrayList(REVIEW_ARRAY, mReviews);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTrailers = savedInstanceState.getParcelableArrayList(TRAILER_ARRAY);
        mReviews = savedInstanceState.getParcelableArrayList(REVIEW_ARRAY);
    }

    private void loadExtraMovieData(Movie movie){
        try {
            // append collection
            mTrailers.addAll(
                    new FetchTrailerDataTask().execute(movie.getId()).get());

            mReviews.addAll(
                    new FetchReviewDataTask().execute(movie.getId()).get());
        }
        catch (Exception ex){
            Log.e("Exception", ex.getMessage());
        }
    }

    private void toggleStar(final Movie selectedMovie){
        // toggle star image
        if (selectedMovie.getFavorited()){
            mFavoriteStarImageView.setImageResource(R.drawable.ic_star_border_white_18dp);
            selectedMovie.setFavorited(false);

            // delete
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().deleteMovie(selectedMovie);
                }
            });
        } else {
            mFavoriteStarImageView.setImageResource(R.drawable.ic_star_full_white_18dp);
            selectedMovie.setFavorited(true);
            // insert
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().insertMovie(selectedMovie);
                }
            });
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.details_error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(Trailer selectedTrailer) {
        Uri youtubeURI = Uri.parse(NetworkUtils.buildYoutubeUrl(selectedTrailer.getKey()).toString());
        startActivity(new Intent(Intent.ACTION_VIEW, youtubeURI));
    }

    @Override
    public void onClick(Review selectedReview) {
        Uri reviewURI = Uri.parse(selectedReview.getUrl());
        startActivity(new Intent(Intent.ACTION_VIEW, reviewURI));
    }

    // movie id, void, list string of vid urls
    public static class FetchTrailerDataTask extends AsyncTask<Integer, Void, ArrayList<Trailer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Trailer> doInBackground(Integer... params) {

            if (params.length == 0) {
                return null;
            }

            int id = params[0];
            URL movieRequestURL = NetworkUtils.buildTrailerUrl(id);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestURL);

                return JSONUtils.getTrailerDataFromJson(jsonMovieResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> trailers) {

        }
    }

    // movie id, void, list string of vid urls
    public static class FetchReviewDataTask extends AsyncTask<Integer, Void, ArrayList<Review>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Review> doInBackground(Integer... params) {

            if (params.length == 0) {
                return null;
            }

            int id = params[0];
            URL reviewRequestURL = NetworkUtils.buildReviewUrl(id);

            try {
                String jsonReviewResponse = NetworkUtils
                        .getResponseFromHttpUrl(reviewRequestURL);

                return JSONUtils.getReviewDataFromJson(jsonReviewResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviews) {

        }
    }
}
