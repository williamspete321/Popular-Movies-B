package com.example.android.popularmovies1b;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    static final String MOVIE = "movie";

    private static final String TAG = DetailActivity.class.getSimpleName();

    private ImageView posterImageView;
    private TextView noImageMessageTextView;
    private TextView titleTextView;
    private TextView relDateTextView;
    private TextView userRatingTextView;
    private TextView overviewTextView;
    private CheckBox favoriteCheckBox;

    private ImageButton imageStartTrailer1;
    private ImageButton imageStartTrailer2;
    private ImageButton imageStartTrailer3;
    private TextView textTrailer1;
    private TextView textTrailer2;
    private TextView textTrailer3;

    private String[] movieTrailers;
    private AppDatabase mDb;
    private boolean favoriteState;
    private static final String FAVORITE = "favorite";
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDb = AppDatabase.getInstance(getApplicationContext());
        mMovie = null;

        if(savedInstanceState != null && savedInstanceState.containsKey(FAVORITE)) {
            favoriteState = savedInstanceState.getBoolean(FAVORITE);
        } else {
            favoriteState = false;
        }

        initViews();

        Intent intent = getIntent();

        if(intent.getParcelableExtra(MOVIE) != null) {
            mMovie = intent.getParcelableExtra(MOVIE);
            setupUI(mMovie);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(FAVORITE, favoriteState);
        super.onSaveInstanceState(outState);
    }

    private void initViews() {
        posterImageView = findViewById(R.id.iv_poster);
        noImageMessageTextView = findViewById(R.id.tv_detail_no_image);
        titleTextView = findViewById(R.id.tv_title);
        relDateTextView = findViewById(R.id.tv_release_date);
        userRatingTextView = findViewById(R.id.tv_user_rating);
        overviewTextView = findViewById(R.id.tv_overview);
        favoriteCheckBox = findViewById(R.id.cb_favorite_selector);

        imageStartTrailer1 = findViewById(R.id.iv_play);
        imageStartTrailer2 = findViewById(R.id.iv_play_two);
        imageStartTrailer3 = findViewById(R.id.iv_play_three);
        textTrailer1 = findViewById(R.id.tv_trailer_one);
        textTrailer2 = findViewById(R.id.tv_trailer_two);
        textTrailer3 = findViewById(R.id.tv_trailer_three);

        imageStartTrailer1.setVisibility(View.INVISIBLE);
        imageStartTrailer2.setVisibility(View.INVISIBLE);
        imageStartTrailer3.setVisibility(View.INVISIBLE);
        textTrailer1.setVisibility(View.INVISIBLE);
        textTrailer2.setVisibility(View.INVISIBLE);
        textTrailer3.setVisibility(View.INVISIBLE);
    }

    public void onClickCheckFavorite(View view) {
        favoriteState = !favoriteState;
        favoriteCheckBox.setChecked(favoriteState);
        Log.d("favoriteState=",favoriteState + "");

        final Movie movie = mMovie;
        movie.setFavorite(favoriteState);

        AppExecuters.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(favoriteState) {
                    mDb.movieDao().insertMovie(movie);
                } else {
                    mDb.movieDao().deleteMovie(movie);
                }
            }
        });

    }

    public void onClickOpenTrailer(View view) {
        int id = view.getId();
        String videoId = null;

        switch (id) {
            case R.id.iv_play:
                videoId = movieTrailers[0];
                break;
            case R.id.iv_play_two:
                videoId = movieTrailers[1];
                break;
            case R.id.iv_play_three:
                videoId = movieTrailers[2];
                break;
        }

        if(videoId != null) startTrailerIntent(videoId);
    }

    private void startTrailerIntent(String videoId) {
        Uri uri = Uri.parse(getString(R.string.youtube_uri, videoId));
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, uri);
        this.startActivity(trailerIntent);
    }

    private void setupUI(Movie movie) {

        int id = movie.getId();
        String title = movie.getTitle();
        String releaseDate = movie.getReleaseDate();
        double userRating = movie.getUserRating();
        String posterPath = movie.getPosterPath();
        String overview = movie.getOverview();
        favoriteState = movie.getFavorite();

        if(posterPath != null) {
            Picasso.get()
                    .load(posterPath)
                    .placeholder(R.drawable.icon_image)
                    .error(R.drawable.icon_error)
                    .into(posterImageView);
        } else {
            posterImageView.setVisibility(View.VISIBLE);
            noImageMessageTextView.setVisibility(View.VISIBLE);
        }

        titleTextView.setText(title);
        relDateTextView.setText(getString(R.string.release_date, releaseDate));
        userRatingTextView.setText(getString(R.string.rating, userRating));
        overviewTextView.setText(overview);
        favoriteCheckBox.setChecked(favoriteState);

        new FetchTrailerDataTask(
                new FetchTrailerDataTask.AsyncResponse() {
                    @Override
                    public void processFinish(String[] trailersResult) {
                        movieTrailers = trailersResult;
                        setupTrailerViews(trailersResult);
                    }
                }
        ).execute(id);
    }

    private void setupTrailerOneUI() {
        imageStartTrailer1.setVisibility(View.VISIBLE);
        textTrailer1.setVisibility(View.VISIBLE);
    }

    private void setupTrailerTwoUI() {
        imageStartTrailer2.setVisibility(View.VISIBLE);
        textTrailer2.setVisibility(View.VISIBLE);
    }

    private void setupTrailerThreeUI() {
        imageStartTrailer3.setVisibility(View.VISIBLE);
        textTrailer3.setVisibility(View.VISIBLE);
    }

    //up to three views will be visible for trailers
    //a trailer play button will not be visible if there's less than 3 total trailers
    private void setupTrailerViews(String[] movieTrailers) {
        int numOfTrailers = 0;

        if(movieTrailers != null) {
             numOfTrailers = movieTrailers.length;
        }

        switch (numOfTrailers) {
            case 1:
                setupTrailerOneUI();
                break;
            case 2:
                setupTrailerOneUI();
                setupTrailerTwoUI();
                break;
            case 3:
                setupTrailerOneUI();
                setupTrailerTwoUI();
                setupTrailerThreeUI();
                break;
            default:
                break;
        }
    }

    public static class FetchTrailerDataTask extends AsyncTask<Integer, Void, String[]> {

        public interface AsyncResponse {
            void processFinish(String[] trailersResult);
        }

        public AsyncResponse delegate = null;

        public FetchTrailerDataTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String[] doInBackground(Integer... integers) {
            if(integers.length == 0) {
                return null;
            }

            int movieId = integers[0];
            URL videoListRequestURL = NetworkUtils.buildUrlForSpecificMovieVideoList(movieId);

            try {
                String jsonVideoResponse =
                        NetworkUtils.getResponseFromHttpUrl(videoListRequestURL);

                String[] simpleVideoKeysForOneMovie =
                        OpenMovieJsonUtils.getSimpleVideoStringFromJson(jsonVideoResponse);

                return simpleVideoKeysForOneMovie;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] trailers) {
            if(trailers != null) {
                delegate.processFinish(trailers);
            }
        }
    }
}
