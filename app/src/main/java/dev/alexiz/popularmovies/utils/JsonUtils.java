package dev.alexiz.popularmovies.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dev.alexiz.popularmovies.models.Movie;
import dev.alexiz.popularmovies.models.Trailer;

public class JsonUtils {

    private static final String RESULTS_KEY = "results";

    private static final String ID_KEY = "id";

    private static final String MOVIE_TITLE_KEY = "original_title";
    private static final String MOVIE_POSTER_PATH_KEY = "poster_path";
    private static final String MOVIE_BACKDROP_PATH_KEY = "backdrop_path";
    private static final String MOVIE_SYNOPSIS_KEY = "overview";
    private static final String MOVIE_RELEASE_DATE_KEY = "release_date";
    private static final String MOVIE_RATING_KEY = "vote_average";
    private static final String MOVIE_POPULARITY_KEY = "popularity";

    private static final String TRAILER_KEY_KEY = "key";
    private static final String TRAILER_NAME_KEY = "name";
    private static final String TRAILER_SITE_KEY = "site";
    private static final String TRAILER_TYPE_KEY = "type";

    public static ArrayList<Movie> getMovies(String jsonResponseString) throws JSONException {
        JSONObject jsonResponse = new JSONObject(jsonResponseString);

        JSONArray jsonMovieObjects = jsonResponse.getJSONArray(RESULTS_KEY);

        ArrayList<Movie> movies = new ArrayList<>();

        int id;

        String title;
        String posterPath;
        String backdropPath;
        String synopsis;
        String releaseDate;

        double rating;

        float popularity;

        for (int i = 0; i < jsonMovieObjects.length(); i++) {
            JSONObject current = jsonMovieObjects.getJSONObject(i);
            id = jsonMovieObjects.getJSONObject(i).getInt(ID_KEY);

            title = current.getString(MOVIE_TITLE_KEY);
            posterPath = current.getString(MOVIE_POSTER_PATH_KEY);
            backdropPath = current.getString(MOVIE_BACKDROP_PATH_KEY);
            synopsis = current.getString(MOVIE_SYNOPSIS_KEY);
            releaseDate = current.getString(MOVIE_RELEASE_DATE_KEY);

            rating = current.getDouble(MOVIE_RATING_KEY);

            popularity = ((Number) current.get(MOVIE_POPULARITY_KEY)).floatValue();

            movies.add(new Movie(id, title, posterPath, backdropPath, synopsis, releaseDate, rating, popularity));
        }
        return movies;
    }

    public static Trailer getTrailer(String jsonResponseString) throws JSONException {
        JSONObject jsonResponse = new JSONObject(jsonResponseString);

        JSONArray jsonTrailerObjects = jsonResponse.getJSONArray(RESULTS_KEY);

        for (int i = 0; i < jsonTrailerObjects.length(); i++) {
            JSONObject current = jsonTrailerObjects.getJSONObject(i);
            if (!current.getString(TRAILER_TYPE_KEY).equalsIgnoreCase("trailer") ||
                    !current.getString(TRAILER_SITE_KEY).equalsIgnoreCase("youtube")) {
                continue;
            }

            String id = current.getString(ID_KEY);
            String name = current.getString(TRAILER_NAME_KEY);
            String key = current.getString(TRAILER_KEY_KEY);
            String site = current.getString(TRAILER_SITE_KEY);
            String type = current.getString(TRAILER_TYPE_KEY);

            return new Trailer(id, name, key, site, type);

        }
        return null;
    }

}