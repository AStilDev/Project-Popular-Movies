package com.example.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.popularmovies.database.AppDatabase;
import com.example.popularmovies.database.AppExecutors;
import com.example.popularmovies.utilities.JSONUtils;
import com.example.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * The main activity
 */
public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final int NUM_COLUMNS = 2;
    private static final int DETAIL_ACTIVITY_REQUEST = 44;
    private static final String SORT_BY = "sort_by";
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private ArrayList<Movie> mMovies;
    GridLayoutManager mLayoutManager;
    private int mPageNum;
    private boolean mLoading;
    private Movie.SortByValue mSortBy;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mSortBy = Movie.SortByValue.values()[savedInstanceState.getInt(SORT_BY)]; // int -> enum
        } else {
            mSortBy = Movie.SortByValue.MOST_POPULAR;
        }

        mMovies = new ArrayList<>();
        mPageNum = 1;

        mRecyclerView = findViewById(R.id.movies_recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, NUM_COLUMNS));
        mLayoutManager = (GridLayoutManager)mRecyclerView.getLayoutManager();

        loadMovieData(); // set initial page

        mMovieAdapter = new MovieAdapter(mMovies, this);
        mRecyclerView.setAdapter(mMovieAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // endless* scrolling
                int position = mLayoutManager.findLastVisibleItemPosition();
                if(mLayoutManager!= null && mMovies != null && position <= 100 &&
                        ((position + 1) == mMovies.size())) {
                    if (mLoading){
                        mLoading = false;
                        mPageNum++;
                        loadMovieData();
                        mMovieAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        mDb = AppDatabase.getInstance((getApplicationContext()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DETAIL_ACTIVITY_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (mSortBy == Movie.SortByValue.FAVORITE){
                    retrieveMovieDataFromDb(); // update favorite list
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.most_popular_menu_item:
                mSortBy = Movie.SortByValue.MOST_POPULAR;
                break;
            case R.id.vote_average_menu_item:
                mSortBy = Movie.SortByValue.HIGHEST_RATED;
                break;
            case R.id.favorite_menu_item:
                mSortBy = Movie.SortByValue.FAVORITE;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        mPageNum = 1;
        mMovies.clear();
        loadMovieData();
        mMovieAdapter.notifyDataSetChanged();
        mLayoutManager.scrollToPositionWithOffset(0, 0);

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(SORT_BY, mSortBy.ordinal()); // enum -> int
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSortBy = Movie.SortByValue.values()[savedInstanceState.getInt(SORT_BY)]; // int -> enum
    }

    @Override
    public void onClick(Movie selectedMovie) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("SelectedMovie", selectedMovie );

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("Bundle", bundle);
        startActivity(intent);
    }

    /**
     * Loads movie data via a task
     */
    private void loadMovieData() {
        try {
            mLoading = true;

            // append collection
            if (mSortBy == Movie.SortByValue.FAVORITE){
                retrieveMovieDataFromDb();
            }else {
                mMovies.addAll(
                        new FetchMovieDataTask(mPageNum).execute(mSortBy).get());
            }
        } catch (Exception ex) {
            Log.e("Exception", ex.getMessage());
        }
    }

    private void retrieveMovieDataFromDb(){
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> moviesList) {
                mMovies.clear();
                mMovies.addAll(new ArrayList<>(moviesList));
                mMovieAdapter.notifyDataSetChanged();
            }
        });
    }

    public static class FetchMovieDataTask extends AsyncTask<Movie.SortByValue, Void, ArrayList<Movie>> {

        int pageNum;

        public FetchMovieDataTask(int pageNum){
            this.pageNum = pageNum;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Movie> doInBackground(Movie.SortByValue... params) {

            if (params.length == 0) {
                return null;
            }

            Movie.SortByValue sortBy = params[0];
            URL movieRequestURL = NetworkUtils.buildMovieQueryUrl(sortBy, pageNum);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestURL);

                return JSONUtils.getMovieDataFromJson(jsonMovieResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {

        }
    }
}
