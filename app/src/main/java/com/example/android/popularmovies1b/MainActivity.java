package com.example.android.popularmovies1b;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener {

    private RecyclerView recyclerView;
    private static MovieAdapter movieAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    //default is to sort by most popular
    private String sortByMethod = "popular";

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

        loadMovieData(sortByMethod);
    }

    private void loadMovieData(String sortByMethod) {
        showMovieDataView();

        new FetchMovieDataTask().execute(sortByMethod);
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
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
                sortByMethod = "popular";
                refreshData();
                return true;
            case R.id.action_highest_rated:
                sortByMethod = "top_rated";
                refreshData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshData() {
        movieAdapter.setMovieData(null);
        loadMovieData(sortByMethod);
    }

    public class FetchMovieDataTask extends AsyncTask<String, Void, Movie[]> {

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
            URL movieRequestUrl = NetworkUtils.buildUrl(sortByPopular);

            try {
                String jsonMovieResponse =
                        NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                Movie[] simpleMovieData = OpenMovieJsonUtils
                        .getSimpleMovieStringFromJson(MainActivity.this, jsonMovieResponse);

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
    public void onMovieItemClick(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.MOVIE, movie);
        startActivity(intent);
    }
}
