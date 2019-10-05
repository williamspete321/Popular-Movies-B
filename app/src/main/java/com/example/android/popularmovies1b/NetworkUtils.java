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

    private static final String MOVIE_BASE_URL =
            "https://api.themoviedb.org/3/discover/movie";

    // Insert your API key between quotation marks
    private static final String api_key = "";

    private static final String language = "en-US";
    private static final boolean includeAdult = false;
    private static final boolean includeVideo = false;
    private static final int pageNumber = 1;


    final static String API_PARAM = "api_key";
    final static String LANG_PARAM = "language";
    final static String SORT_PARAM = "sort_by";
    final static String ADULT_PARAM = "include_adult";
    final static String INCL_VID_PARAM = "include_video";
    final static String PAGE_PARAM = "page";

    public static URL buildUrl(String sortMethod) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendQueryParameter(API_PARAM,api_key)
                .appendQueryParameter(LANG_PARAM,language)
                .appendQueryParameter(SORT_PARAM,sortMethod)
                .appendQueryParameter(ADULT_PARAM,Boolean.toString(includeAdult))
                .appendQueryParameter(INCL_VID_PARAM,Boolean.toString(includeVideo))
                .appendQueryParameter(PAGE_PARAM,Integer.toString(pageNumber))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Bulti URI: " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
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
