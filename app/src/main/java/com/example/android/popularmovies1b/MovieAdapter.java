package com.example.android.popularmovies1b;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private Movie[] mMovieData;

    final private MovieItemClickListener mOnClickListener;

    public interface MovieItemClickListener {
        void onMovieItemClick(Movie movie);
    }

    public MovieAdapter(MovieItemClickListener listener) {
        mOnClickListener = listener;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        public final ImageView movieImageView;
        public final TextView movieNoImageMessage;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            movieImageView = itemView.findViewById(R.id.movie_image_view);
            movieNoImageMessage = itemView.findViewById(R.id.tv_no_image_available);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            int clickedPosition = getAdapterPosition();
            Movie movie = mMovieData[clickedPosition];
            mOnClickListener.onMovieItemClick(movie);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int itemId = R.layout.recycler_view_movie_item;
        boolean attachToParent = false;

        View view = inflater.inflate(itemId,viewGroup,attachToParent);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie movie = mMovieData[position];
        String moviePosterPath = movie.getPosterPath();

        if(moviePosterPath != null) {
            holder.movieImageView.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(moviePosterPath)
                    .placeholder(R.drawable.icon_image)
                    .error(R.drawable.icon_error)
                    .into(holder.movieImageView);
        } else {
            holder.movieImageView.setVisibility(View.INVISIBLE);
            holder.movieNoImageMessage.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        if(mMovieData == null) return 0;
        return mMovieData.length;
    }

    public void setMovieData(Movie[] movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    public void setFavoriteMovieList(List<Movie> movieList) {
        Movie[] favoriteMovies = new Movie[movieList.size()];

        for(int i = 0; i < favoriteMovies.length; i++) {
            favoriteMovies[i] = movieList.get(i);
        }

        mMovieData = favoriteMovies;
        notifyDataSetChanged();
    }

}
