package com.example.popularmovies.utilities;

import android.content.Context;

import com.example.popularmovies.Movie;
import com.example.popularmovies.Review;
import com.example.popularmovies.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Contains utilities for manipulating JSON
 */
public class JSONUtils {

    /**
     * Retrieves data from a JSON string
     * @param movieJsonStr The JSON string to parse
     * @return An arraylist of movie objects
     * @throws JSONException
     */
    public static ArrayList<Movie> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        final String ID = "id";
        final String TITLE = "title";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String OVERVIEW = "overview";
        final String POSTER_PATH = "poster_path";
        final String RESULTS = "results";
        final String MESSAGE_CODE = "cod";

        ArrayList<Movie> movieData = new ArrayList<>();

        JSONObject movieJson = new JSONObject(movieJsonStr);

        if (movieJson.has(MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray movieArray = movieJson.getJSONArray(RESULTS);

        for (int i = 0; i < movieArray.length(); i++) {
            int id;
            String title;
            String releaseDate;
            String posterPath;
            double voteAverage;
            String overview;

            JSONObject movie = movieArray.getJSONObject(i);

            id = movie.getInt(ID);
            title = movie.getString(TITLE);
            releaseDate = movie.getString(RELEASE_DATE);
            posterPath = movie.getString(POSTER_PATH);
            voteAverage = movie.getDouble(VOTE_AVERAGE);
            overview  = movie.getString(OVERVIEW);

            Movie parsedMovieData = new Movie(id, title, voteAverage, overview, posterPath, releaseDate);

            movieData.add(parsedMovieData);
        }

        return movieData;
    }

    ///    //{"id":550,"results":[{"id":"5c9294240e0a267cd516835f","iso_639_1":"en","iso_3166_1":"US","key":"BdJKm16Co6M","name":"Fight Club | #TBT Trailer | 20th Century FOX","site":"YouTube","size":1080,"type":"Trailer"}]}
    /**
     * Retrieves data from a JSON string
     * @param trailerJsonStr The JSON string to parse
     * @return An arraylist of movie objects
     * @throws JSONException
     */
    public static ArrayList<Trailer> getTrailerDataFromJson(String trailerJsonStr)
            throws JSONException {

        final String ID = "id";
        final String KEY = "key";
        final String NAME = "name";
        final String SITE = "site";
        final String TYPE = "type";
        final String RESULTS = "results";
        final String MESSAGE_CODE = "cod";

        ArrayList<Trailer> trailerData = new ArrayList<>();

        JSONObject trailerJson = new JSONObject(trailerJsonStr);

        if (trailerJson.has(MESSAGE_CODE)) {
            int errorCode = trailerJson.getInt(MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray trailerArray = trailerJson.getJSONArray(RESULTS);

        for (int i = 0; i < trailerArray.length(); i++) {
            String id;
            String key;
            String name;
            String site;
            String type;

            JSONObject movie = trailerArray.getJSONObject(i);

            id = movie.getString(ID);
            key = movie.getString(KEY);
            name = movie.getString(NAME);
            site = movie.getString(SITE);
            type  = movie.getString(TYPE);

            if (site.toLowerCase().equals(Trailer.TrailerSiteType.YOUTUBE.name().toLowerCase()) &&
            type.toLowerCase().equals(Trailer.TrailerType.TRAILER.name().toLowerCase()))
            {
                Trailer parsedMovieData = new Trailer(id, key, name, site, type);

                trailerData.add(parsedMovieData);
            }
        }

        return trailerData;
    }

    public static ArrayList<Review> getReviewDataFromJson(String reviewJsonStr)
            throws JSONException {

        final String ID = "id";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String URL = "url";
        final String RESULTS = "results";
        final String MESSAGE_CODE = "cod";

        ArrayList<Review> reviewData = new ArrayList<>();

        JSONObject reviewJson = new JSONObject(reviewJsonStr);

        if (reviewJson.has(MESSAGE_CODE)) {
            int errorCode = reviewJson.getInt(MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray reviewArray = reviewJson.getJSONArray(RESULTS);

        for (int i = 0; i < reviewArray.length(); i++) {
            String id;
            String author;
            String content;
            String url;

            JSONObject movie = reviewArray.getJSONObject(i);

            id = movie.getString(ID);
            author = movie.getString(AUTHOR);
            content = movie.getString(CONTENT);
            url = movie.getString(URL);

            Review parsedReviewData = new Review(id, author, content.trim(), url);

            reviewData.add(parsedReviewData);
        }

        return reviewData;
    }
}
