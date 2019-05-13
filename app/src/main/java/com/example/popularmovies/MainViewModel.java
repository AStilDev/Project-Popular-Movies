package com.example.popularmovies;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.popularmovies.database.AppDatabase;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<Movie>> movies;
    private AppDatabase database;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(this.getApplication());
        movies = database.movieDao().loadAllMovies();
    }

    public LiveData<List<Movie>> getMovies(){
        if (movies == null){
            movies = new MutableLiveData<>();
        }
        return movies;
    }

    public LiveData<Movie> getMovie(int id){
        return database.movieDao().loadMovieById(id);
    }
}