package dev.alexiz.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    // The Movie DB API key information
    private static final String TMDB_API_KEY_PARAM = "api_key";
    private static final String TMDB_API_KEY = "8a388f15d989ddf243f89224d71de2db";

    // Base URLs for fetching movies or movie reviews/trailers
    private static final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String IMAGES_BASE_URL = "http://image.tmdb.org/t/p";

    // Path for trailers in The Movie DB
    private static final String TRAILERS_PATH = "videos";

    // Movie poster and backdrop file sizes
    private static final String IMAGE_SIZE_W92 = "w92";
    private static final String IMAGE_SIZE_W154 = "w154";
    private static final String IMAGE_SIZE_W185 = "w185";
    private static final String IMAGE_SIZE_W342 = "w342";
    private static final String IMAGE_SIZE_W500 = "w500";
    private static final String IMAGE_SIZE_W780 = "w780";
    private static final String IMAGE_SIZE_ORIGINAL = "original";

    // Builds URL to fetch movies from The Movie DB
    public static URL buildQueryUrl(String sortQuery) {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(sortQuery)
                .appendQueryParameter(TMDB_API_KEY_PARAM, TMDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // Builds URL to fetch a specific movie's trailers from The Movie DB
    public static URL buildTrailersUrl(String id) {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(TRAILERS_PATH)
                .appendQueryParameter(TMDB_API_KEY_PARAM, TMDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // Builds Uri for specific movie's poster image
    public static Uri buildPosterUri(String posterPath) {
        return Uri.parse(IMAGES_BASE_URL).buildUpon()
                .appendPath(IMAGE_SIZE_W780)
                .appendEncodedPath(posterPath)
                .build();
    }

    // Builds Uri for specific movie's backdrop image
    public static Uri buildBackdropUri(String backdropPath) {
        return Uri.parse(IMAGES_BASE_URL).buildUpon()
                .appendPath(IMAGE_SIZE_ORIGINAL)
                .appendEncodedPath(backdropPath)
                .build();
    }

    // Gets response from URL and returns results String
    public static String getResponseFromHttp(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}