package com.example.android.popularmovies1b;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener {

    private static RecyclerView recyclerView;
    private static MovieAdapter movieAdapter;
    private static TextView mErrorMessageDisplay;
    private static ProgressBar mLoadingIndicator;

    final static String POPULAR = "popular";
    final static String TOP_RATED = "top_rated";
    final static String FAVORITE = "favorite";
    //default is to sort by most popular
    private String selectionMethod = POPULAR;

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String SELECTION_METHOD = "selection method";

    private Movie[] favoriteMoviesArray;
    private static AppDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = AppDatabase.getInstance(getApplicationContext());

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

        loadFavoriteMoviesFromDb();
        loadMovieData(selectionMethod);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SELECTION_METHOD, selectionMethod);
    }

    private void refreshData() {
        movieAdapter.setMovieData(null);
        loadMovieData(selectionMethod);
    }

    private void loadMovieData(String selectionMethod) {
        showMovieDataView();

        switch (selectionMethod) {
            case POPULAR:
                new FetchMovieDataTask().execute(POPULAR);
                break;
            case TOP_RATED:
                new FetchMovieDataTask().execute(TOP_RATED);
                break;
            case FAVORITE:
                movieAdapter.setMovieData(favoriteMoviesArray);
                break;
        }
    }

    private void loadFavoriteMoviesFromDb() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavoriteMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                Log.d(TAG, "Updating list of favorite movies from LiveData in ViewModel");
                setFavoriteMovieList(movies);
                refreshData();
            }
        });
    }

    private void setFavoriteMovieList(List<Movie> movieList) {
        favoriteMoviesArray = new Movie[movieList.size()];

        for(int i = 0; i < favoriteMoviesArray.length; i++) {
            favoriteMoviesArray[i] = movieList.get(i);
        }
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

    public static class FetchMovieDataTask extends AsyncTask<String, Void, Movie[]> {

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

    private void startDetailActivity(Movie movie) {
        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.MOVIE, movie);
        startActivity(intent);
    }

    @Override
    public void onMovieItemClick(final Movie movie) {
        AppExecuters.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Movie movieFromDB = mDb.movieDao().getMovieById(movie.getId());
                if(movieFromDB != null) {
                    startDetailActivity(movieFromDB);
                } else {
                    startDetailActivity(movie);
                }
            }
        });
    }
}
