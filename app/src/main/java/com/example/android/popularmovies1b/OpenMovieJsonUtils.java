package com.example.android.popularmovies1b;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

public final class OpenMovieJsonUtils {

    public static Movie[] getSimpleMovieStringFromJson(String movieJsonStr)
            throws JSONException {

        final String OM_RESULTS = "results";

        final String OM_ORIG_TITLE = "original_title";
        final String OM_POST_PATH = "poster_path";
        final String OM_OVERVIEW = "overview";
        final String OM_VOTE_AVG = "vote_average";
        final String OM_REL_DATE = "release_date";
        final String OM_ID = "id";

        final String OM_MESSAGE_CODE = "cod";

        final String MOV_POSTER_PATH_BASE_URL = "https://image.tmdb.org/t/p/w185/";

        Movie[] parsedMovieData = null;

        JSONObject movieJson = new JSONObject(movieJsonStr);

        if(movieJson.has(OM_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(OM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpsURLConnection.HTTP_OK:
                    break;
                case HttpsURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray movieJsonArray = movieJson.getJSONArray(OM_RESULTS);

        parsedMovieData = new Movie[movieJsonArray.length()];

        for(int i = 0; i < movieJsonArray.length(); i++) {
            JSONObject jsonObject = movieJsonArray.getJSONObject(i);

            int movieId = jsonObject.getInt(OM_ID);
            String movieTitle = jsonObject.getString(OM_ORIG_TITLE);
            String moviePosterPath = jsonObject.getString(OM_POST_PATH);

            String updatedPosterPath = null;

            if(moviePosterPath != "null") {
                updatedPosterPath = MOV_POSTER_PATH_BASE_URL + moviePosterPath;
            }

            String movieOverview = jsonObject.getString(OM_OVERVIEW);
            double movieUserRating = jsonObject.getDouble(OM_VOTE_AVG);
            String movieReleaseDate = jsonObject.getString(OM_REL_DATE);
            boolean isFavorite = false;

            Movie movie = new Movie(movieId, movieTitle, updatedPosterPath, movieOverview,
                            movieUserRating, movieReleaseDate, isFavorite);

            parsedMovieData[i] = movie;
        }
        return parsedMovieData;
    }

    public static String[] getSimpleVideoStringFromJson(String movieTrailerJsonStr)
            throws JSONException{

        final String OM_V_RESULTS = "results";
        final String OM_V_KEY = "key";
        final String OM_MESSAGE_CODE = "cod";

        String[] parsedTrailerKeys = null;

        JSONObject trailerJson = new JSONObject(movieTrailerJsonStr);

        if(trailerJson.has(OM_MESSAGE_CODE)) {
            int errorCode = trailerJson.getInt(OM_MESSAGE_CODE);

            switch(errorCode) {
                case HttpsURLConnection.HTTP_OK:
                    break;
                case HttpsURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray resultsArray = trailerJson.getJSONArray(OM_V_RESULTS);

        // we don't want more than three trailers max; no need to collect more
        if(resultsArray.length() > 3) {
            parsedTrailerKeys = new String[3];
        } else {
            parsedTrailerKeys = new String[resultsArray.length()];
        }

        for(int i = 0; i < parsedTrailerKeys.length; i++) {
            JSONObject jsonObject = resultsArray.getJSONObject(i);
            String movieKey = jsonObject.getString(OM_V_KEY);

            parsedTrailerKeys[i] = movieKey;
        }
        return parsedTrailerKeys;
    }

    public static String[] getSimpleReviewStringFromJson(String movieReviewsJsonStr)
            throws JSONException{

        final String OM_R_RESULTS = "results";
        final String OM_R_CONTENT = "content";
        final String OM_MESSAGE_CODE = "cod";

        String[] parsedReviews = null;

        JSONObject trailerJson = new JSONObject(movieReviewsJsonStr);

        if(trailerJson.has(OM_MESSAGE_CODE)) {
            int errorCode = trailerJson.getInt(OM_MESSAGE_CODE);

            switch(errorCode) {
                case HttpsURLConnection.HTTP_OK:
                    break;
                case HttpsURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray resultsArray = trailerJson.getJSONArray(OM_R_RESULTS);

        // we don't want more than three trailers max; no need to collect more
        if(resultsArray.length() > 3) {
            parsedReviews = new String[3];
        } else {
            parsedReviews = new String[resultsArray.length()];
        }

        for(int i = 0; i < parsedReviews.length; i++) {
            JSONObject jsonObject = resultsArray.getJSONObject(i);
            String movieReview = jsonObject.getString(OM_R_CONTENT);

            parsedReviews[i] = movieReview;
        }
        return parsedReviews;
    }

}
