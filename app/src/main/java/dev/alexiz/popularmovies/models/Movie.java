package dev.alexiz.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private int id;

    private String title;
    private String posterPath;
    private String backdropPath;
    private String synopsis;
    private String releaseDate;

    private double rating;

    private float popularity;

    public Movie(int id, String title, String posterPath, String backdropPath,
                 String synopsis, String releaseDate, double rating, float popularity) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.popularity = popularity;
    }

    private Movie(Parcel parcel) {
        this.id = parcel.readInt();
        this.title = parcel.readString();
        this.posterPath = parcel.readString();
        this.backdropPath = parcel.readString();
        this.synopsis = parcel.readString();
        this.releaseDate = parcel.readString();
        this.rating = parcel.readDouble();
        this.popularity = parcel.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(posterPath);
        parcel.writeString(backdropPath);
        parcel.writeString(synopsis);
        parcel.writeString(releaseDate);
        parcel.writeDouble(rating);
        parcel.writeFloat(popularity);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }
        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getPosterPath() {
        return this.posterPath;
    }

    public String getBackdropPath() {
        return this.backdropPath;
    }

    public String getSynopsis() {
        return this.synopsis;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public double getRating() {
        return this.rating;
    }

    public float getPopularity() {
        return this.popularity;
    }

}