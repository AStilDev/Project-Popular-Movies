package com.example.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.popularmovies.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM Movie ORDER BY id")
    LiveData<List<Movie>> loadAllMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("SELECT * FROM Movie WHERE id = :id")
    LiveData<Movie> loadMovieById(int id);
}
