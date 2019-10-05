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

    private static final String MOVIE_BASE_POPULAR_URL
            = "https://api.themoviedb.org/3/movie/popular";
    private static final String MOVIE_BASE_TOP_RATED_URL
            = "https://api.themoviedb.org/3/movie/top_rated";

    // Insert your API key between quotation marks
    private static final String api_key = "";

    private static final String language = "en-US";
    private static final int pageNumber = 1;


    final static String API_PARAM = "api_key";
    final static String LANG_PARAM = "language";
    final static String PAGE_PARAM = "page";

    public static URL buildUrl(String sortMethod) {
        Uri builtUri = null;
        if(sortMethod.equals("popular")) {
            builtUri = buildUriByPopular();
        } else {
            builtUri = buildUriByRated();
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

    private static Uri buildUriByPopular() {
        Uri builtUri = Uri.parse(MOVIE_BASE_POPULAR_URL).buildUpon()
                .appendQueryParameter(API_PARAM,api_key)
                .appendQueryParameter(LANG_PARAM,language)
                .appendQueryParameter(PAGE_PARAM,Integer.toString(pageNumber))
                .build();
        return builtUri;
    }

    private static Uri buildUriByRated() {
        Uri builtUri = Uri.parse(MOVIE_BASE_TOP_RATED_URL).buildUpon()
                .appendQueryParameter(API_PARAM,api_key)
                .appendQueryParameter(LANG_PARAM,language)
                .appendQueryParameter(PAGE_PARAM,Integer.toString(pageNumber))
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
