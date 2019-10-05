package com.example.android.popularmovies1b;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    static final String MOVIE = "movie";

    private ImageView posterImageView;
    private TextView noImageMessageTextview;
    private TextView titleTextView;
    private TextView relDateTextView;
    private TextView userRatingTextView;
    private TextView overviewTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        posterImageView = findViewById(R.id.iv_poster);
        noImageMessageTextview = findViewById(R.id.tv_detail_no_image);
        titleTextView = findViewById(R.id.tv_title);
        relDateTextView = findViewById(R.id.tv_release_date);
        userRatingTextView = findViewById(R.id.tv_user_rating);
        overviewTextView = findViewById(R.id.tv_overview);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(MOVIE);

        String releaseDate = movie.getReleaseDate();
        double userRating = movie.getUserRating();

        if(movie.getPosterPath() != null) {
            Picasso.get()
                    .load(movie.getPosterPath())
                    .placeholder(R.drawable.icon_image)
                    .error(R.drawable.icon_error)
                    .into(posterImageView);
        } else {
            posterImageView.setVisibility(View.VISIBLE);
            noImageMessageTextview.setVisibility(View.VISIBLE);
        }

        titleTextView.setText(movie.getTitle());
        relDateTextView.setText(getString(R.string.release_date, releaseDate));
        userRatingTextView.setText(getString(R.string.rating, userRating));
        overviewTextView.setText(movie.getOverview());
    }
}
