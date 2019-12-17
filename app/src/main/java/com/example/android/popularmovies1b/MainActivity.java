package com.example.android.popularmovies1b;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener {

    private static RecyclerView recyclerView;
    private static MovieAdapter movieAdapter;
    private static Movie[] moviesArray;
    private static TextView mErrorMessageDisplay;
    private static ProgressBar mLoadingIndicator;

    final static String POPULAR = "popular";
    final static String TOP_RATED = "top_rated";
    final static String FAVORITE = "favorite";
    //default is to sort by most popular
    private String selectionMethod = POPULAR;

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String SELECTION_METHOD = "selection method";

    private static AppDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.movies_recycler_view);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);


        int numberOfColumns = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,numberOfColumns);
        movieAdapter = new MovieAdapter(this);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(movieAdapter);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTION_METHOD)) {
            selectionMethod = savedInstanceState.getString(SELECTION_METHOD);
        }

        loadMovieData(selectionMethod);

        mDb = AppDatabase.getInstance(getApplicationContext());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SELECTION_METHOD, selectionMethod);
    }

    private void loadMovieData(String selectionMethod) {
        showMovieDataView();

        switch (selectionMethod) {
            case POPULAR:
                new FetchMovieDataTask(getApplication()).execute(POPULAR);
                break;
            case TOP_RATED:
                new FetchMovieDataTask(getApplication()).execute(TOP_RATED);
                break;
            case FAVORITE:
                loadFavoriteMovies();
                break;
        }
    }

    private void loadFavoriteMovies() {
        final LiveData<List<Movie>> favoriteMovies = mDb.movieDao().loadAllFavoriteMovies();
        favoriteMovies.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                Log.d(TAG, "Receiving database update from LiveData");
                if (selectionMethod.equals(FAVORITE)) {
                    movieAdapter.setFavoriteMovieList(movies);
                }
            }
        });
    }

    private static void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private static void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(item.getItemId()) {
            case R.id.action_most_popular:
                selectionMethod = POPULAR;
                refreshData();
                return true;
            case R.id.action_highest_rated:
                selectionMethod = TOP_RATED;
                refreshData();
                return true;
            case R.id.action_user_favorite:
                selectionMethod = FAVORITE;
                refreshData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshData() {
        movieAdapter.setMovieData(null);
        loadMovieData(selectionMethod);
    }

    public static class FetchMovieDataTask extends AsyncTask<String, Void, Movie[]> {
        private WeakReference<Application> mApplicationReference;

        FetchMovieDataTask(Application context) {
            mApplicationReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            if(params.length == 0) {
                return null;
            }

            String sortByPopular = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrlForMovieList(sortByPopular);

            try {
                String jsonMovieResponse =
                        NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                Movie[] simpleMovieData = OpenMovieJsonUtils
                        .getSimpleMovieStringFromJson(jsonMovieResponse);

                return simpleMovieData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movieData != null) {
                showMovieDataView();
                movieAdapter.setMovieData(movieData);
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public void onMovieItemClick(final Movie movie) {
        AppExecuters.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Movie movieFromDB = mDb.movieDao().getMovieById(movie.getId());
                if(movieFromDB != null) {
                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                    intent.putExtra(DetailActivity.MOVIE, movieFromDB);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                    intent.putExtra(DetailActivity.MOVIE, movie);
                    startActivity(intent);
                }
            }
        });
    }
}
