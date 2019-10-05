package com.example.android.popularmovies1b;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    String title;
    String posterPath;
    String overview;
    double userRating;
    String releaseDate;


    public Movie(String title, String posterPath, String overview, double userRating, String releaseDate) {
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeDouble(userRating);
        dest.writeString(releaseDate);
    }

    public Movie(Parcel parcel) {
        title = parcel.readString();
        posterPath = parcel.readString();
        overview = parcel.readString();
        userRating = parcel.readDouble();
        releaseDate = parcel.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public double getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
