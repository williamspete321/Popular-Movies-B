package com.example.android.popularmovies1b;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_BASE = "https://api.themoviedb.org/3/movie/";
    private static final String MOVIE_BASE_POPULAR_URL = MOVIE_BASE + "popular";
    private static final String MOVIE_BASE_TOP_RATED_URL = MOVIE_BASE + "top_rated";
    private static final String MOVIE_BASE_VIDEOS_URL = MOVIE_BASE + "%d/videos";

    // Insert your API key between quotation marks
    private static final String api_key = "";

    private static final String language = "en-US";
    private static final int pageNumber = 1;

    final static String API_PARAM = "api_key";
    final static String LANG_PARAM = "language";
    final static String PAGE_PARAM = "page";

    //creates URL for list of movies by popular or highest rated
    public static URL buildUrlForMovieList(String sortMethod) {
        Uri builtUri = null;
        if(sortMethod.equals("popular")) {
            builtUri = buildMovieListUriByPopular();
        } else {
            builtUri = buildMovieListUriByRated();
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Bulti URI: " + url);

        return url;
    }

    //creates URL for list of videos related to one movie; uses specific movie id
    public static URL buildUrlForSpecificMovieVideoList(int movieId) {
        Uri builtUri = buildTrailerListUriById(movieId);

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Bulti URI: " + url);

        return url;
    }

    private static Uri buildMovieListUriByPopular() {
        Uri builtUri = Uri.parse(MOVIE_BASE_POPULAR_URL).buildUpon()
                .appendQueryParameter(API_PARAM,api_key)
                .appendQueryParameter(LANG_PARAM,language)
                .appendQueryParameter(PAGE_PARAM,Integer.toString(pageNumber))
                .build();
        return builtUri;
    }

    private static Uri buildMovieListUriByRated() {
        Uri builtUri = Uri.parse(MOVIE_BASE_TOP_RATED_URL).buildUpon()
                .appendQueryParameter(API_PARAM,api_key)
                .appendQueryParameter(LANG_PARAM,language)
                .appendQueryParameter(PAGE_PARAM,Integer.toString(pageNumber))
                .build();
        return builtUri;
    }

    private static Uri buildTrailerListUriById(int movieId) {
        Uri builtUri = Uri.parse(String.format(MOVIE_BASE_VIDEOS_URL, movieId)).buildUpon()
                .appendQueryParameter(API_PARAM,api_key)
                .appendQueryParameter(LANG_PARAM,language)
                .build();
        return builtUri;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(10000);
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
