package com.example.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.popularmovies.MainActivity;
import com.example.popularmovies.Movie;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Scanner;

/**
 * Contains utilities for building URLs and fetching
 * HTTP responses
 */
public class NetworkUtils {

    // base query
    private final static String MOVIE_BASE_URL =
            "https://api.themoviedb.org/3/movie";

    // movie query
    private final static String QUERY_POPULAR_BASE_URL =
            "https://api.themoviedb.org/3/movie/popular";
    private final static String QUERY_RATED_BASE_URL =
            "https://api.themoviedb.org/3/movie/top_rated";

    private final static String PARAM_API_KEY = "api_key";
    private final static String API_KEY = ""; // Enter API key here

    private static String PARAM_LANGUAGE = "language";
    private static String mLanguage = "en-US";
    private static String PAGE = "page";

    // image
    private final static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private final static String IMAGE_SIZE_PARAM = "w185";

    // trailer
    private final static String TRAILER_INDICATOR = "videos";

    // review
    private final static String REVIEW_INDICATOR = "reviews";

    // Youtube
    private final static String YOUTUBE_BASE_URL = "http://www.youtube.com/watch";
    private final static String YOUTUBE_KEY = "v";

    /**
     * Constructs the query URL
     * @param sortBy The value to determine the sort
     * @param pageNum The page number to query
     * @return The built query URL
     */
    public static URL buildMovieQueryUrl(Movie.SortByValue sortBy, int pageNum) {
        String baseUrl = "";

        switch (sortBy)
        {
            case MOST_POPULAR:
                baseUrl = QUERY_POPULAR_BASE_URL;
                break;
            case HIGHEST_RATED:
                baseUrl = QUERY_RATED_BASE_URL;
                break;
        }

        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, mLanguage)
                .appendQueryParameter(PAGE, String.valueOf(pageNum))
                .build();

        URL url = null;
        try {

            url = new URL(URLDecoder.decode(builtUri.toString(), "UTF-8"));

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("MalformedURL", e.getMessage());
        } catch (UnsupportedEncodingException ue){

            ue.printStackTrace();
            Log.e("UnsupportedEncoding", ue.getMessage());
        }

        return url;
    }

    /**
     * Constructs the image url
     * @param imageQuery The specified image query string
     * @return The built image url
     */
    public static URL buildImageUrl(String imageQuery) {
        Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(IMAGE_SIZE_PARAM)
                .appendPath(imageQuery)
                .build();

        URL url = null;
        try {

            url = new URL(URLDecoder.decode(builtUri.toString(), "UTF-8"));

        } catch (MalformedURLException e) {

            e.printStackTrace();
            Log.e("MalformedURL", e.getMessage());
        } catch (UnsupportedEncodingException ue){

            ue.printStackTrace();
            Log.e("UnsupportedEncoding", ue.getMessage());
        }

        return url;
    }

    /**
     * Constructs the trailers url
     * @param trailerID The specified trailer identifier
     * @return The built trailer url
     */
    public static URL buildTrailerUrl(int trailerID) {
        //movie/{id}/videos
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath("" + trailerID)
                .appendPath(TRAILER_INDICATOR)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        URL url = null;
        try {

            url = new URL(URLDecoder.decode(builtUri.toString(), "UTF-8"));

        } catch (MalformedURLException e) {

            e.printStackTrace();
            Log.e("MalformedURL", e.getMessage());
        } catch (UnsupportedEncodingException ue){

            ue.printStackTrace();
            Log.e("UnsupportedEncoding", ue.getMessage());
        }

        return url;
    }

    /**
     * Constructs the reviews url
     * @param reviewID The specified review identifier
     * @return The built review url
     */
    public static URL buildReviewUrl(int reviewID) {
        //movie/{id}/reviews
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath("" + reviewID)
                .appendPath(REVIEW_INDICATOR)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        URL url = null;
        try {

            url = new URL(URLDecoder.decode(builtUri.toString(), "UTF-8"));

        } catch (MalformedURLException e) {

            e.printStackTrace();
            Log.e("MalformedURL", e.getMessage());
        } catch (UnsupportedEncodingException ue){

            ue.printStackTrace();
            Log.e("UnsupportedEncoding", ue.getMessage());
        }

        return url;
    }

    /**
     * Constructs the Youtube url
     * @param videoKey The specified video key
     * @return The built Youtube url
     */
    public static URL buildYoutubeUrl(String videoKey) {
        Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_KEY, videoKey)
                .build();

        URL url = null;
        try {

            url = new URL(URLDecoder.decode(builtUri.toString(), "UTF-8"));

        } catch (MalformedURLException e) {

            e.printStackTrace();
            Log.e("MalformedURL", e.getMessage());
        } catch (UnsupportedEncodingException ue){

            ue.printStackTrace();
            Log.e("UnsupportedEncoding", ue.getMessage());
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}