package com.example.android.popularmovies1b;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

public final class OpenMovieJsonUtils {

    public static Movie[] getSimpleMovieStringFromJson(Context context, String movieJsonStr)
            throws JSONException {

        final String OM_RESULTS = "results";

        final String OM_ORIG_TITLE = "original_title";
        final String OM_POST_PATH = "poster_path";
        final String OM_OVERVIEW = "overview";
        final String OM_VOTE_AVG = "vote_average";
        final String OM_REL_DATE = "release_date";

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

            String movieTitle = jsonObject.getString(OM_ORIG_TITLE);
            String moviePosterPath = jsonObject.getString(OM_POST_PATH);
            Log.v("OpenMovieJsonUtils", "Movie Poster Path String: " + moviePosterPath);

            String updatedPosterPath = null;

            if(moviePosterPath != "null") {
                updatedPosterPath = MOV_POSTER_PATH_BASE_URL + moviePosterPath;
            }

            String movieOverview = jsonObject.getString(OM_OVERVIEW);
            double movieUserRating = jsonObject.getDouble(OM_VOTE_AVG);
            String movieReleaseDate = jsonObject.getString(OM_REL_DATE);

            Movie movie =
                    new Movie(movieTitle, updatedPosterPath, movieOverview, movieUserRating, movieReleaseDate);

            parsedMovieData[i] = movie;
        }
        return parsedMovieData;
    }
}
