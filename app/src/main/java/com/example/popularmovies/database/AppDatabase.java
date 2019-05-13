package com.example.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.popularmovies.ArrayConverter;
import com.example.popularmovies.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
@TypeConverters(ArrayConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "movielist";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context){
        if (sInstance == null){
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract MovieDao movieDao();
}
