package com.example.android.utils;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.example.android.popular_movies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_MOVIEDB_POSTER_URL =
            "http://image.tmdb.org/t/p/";

    private static final String BASE_MOVIEDB_QUERY_URL =
            "http://api.themoviedb.org/3/movie/";

    private static final String MOVIEDB_IMAGE_SIZE_ORIGINAL = "original";
    private static final String MOVIEDB_IMAGE_SIZE_S = "w92";
    private static final String MOVIEDB_IMAGE_SIZE_M = "w154";
    private static final String MOVIEDB_IMAGE_SIZE_L = "w185";
    private static final String MOVIEDB_IMAGE_SIZE_XL = "w342";
    private static final String MOVIEDB_IMAGE_SIZE_XXL = "w500";
    private static final String MOVIEDB_IMAGE_SIZE_XXXL = "w780";

    private static final String MOVIEDB_API_PARAM = "api_key";


    //INSERT MOVIEDB API KEY HERE
    private static final String MOVIEDB_API_KEY =
            "";


    //TODO Define a method to query the MOVIEDb API and return poster paths for movies.

        public static URL buildMovieDBQueryURL (String preference) {

        String basePathUrl = BASE_MOVIEDB_QUERY_URL + preference;
        Uri moviePathQueryUri = Uri.parse(basePathUrl).buildUpon()
                .appendQueryParameter(MOVIEDB_API_PARAM, MOVIEDB_API_KEY).build();

        try {
            URL moviePathQueryUrl = new URL(moviePathQueryUri.toString());
            return moviePathQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        //TODO incorporate the preference (either popular or top rated) into this path.  To start, we'll default to
        //using popular.

    }

        public static URL buildMoviePosterURL (String path) {
            String baseUrl = BASE_MOVIEDB_POSTER_URL + MOVIEDB_IMAGE_SIZE_L + path;
            try {
                URL moviePosterQueryUrl = new URL(baseUrl);
                Log.v(TAG, "URL: " + moviePosterQueryUrl);
                return moviePosterQueryUrl;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

        }

    //Based strongly off the getResponseFromHttpUrl method in the Sunshine Project from earlier.
    public static String getResponseFromHttpUrl(URL url) throws IOException {

        //Create a HttpURLConnection object to handle the streams.
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inStream = urlConnection.getInputStream();

            Scanner scanner = new Scanner(inStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }


}
