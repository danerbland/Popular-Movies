package com.example.android.utils;

import android.net.Uri;
import android.util.Log;

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

    private static final String BASE_TRAILER_THUMBNAIL_URL =
            "https://img.youtube.com/vi/";

    private static final String BASE_YOUTUBE_VIDEO_URL =
            "https://www.youtube.com/watch";


    private static final String MOVIEDB_IMAGE_SIZE_ORIGINAL = "original";
    private static final String MOVIEDB_IMAGE_SIZE_S = "w92";
    private static final String MOVIEDB_IMAGE_SIZE_M = "w154";
    private static final String MOVIEDB_IMAGE_SIZE_L = "w185";
    private static final String MOVIEDB_IMAGE_SIZE_XL = "w342";
    private static final String MOVIEDB_IMAGE_SIZE_XXL = "w500";
    private static final String MOVIEDB_IMAGE_SIZE_XXXL = "w780";

    private static final String MOVIEDB_API_PARAM = "api_key";
    private static final String YOUTUBE_KEY_PARAM = "v";


    //INSERT MOVIEDB API KEY HERE
    private static final String MOVIEDB_API_KEY =
            "";


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

        public static URL buildMovieTrailersURL (String id){
            String baseURL = BASE_MOVIEDB_QUERY_URL + id +"/videos";
            Uri moviePathQueryUri = Uri.parse(baseURL).buildUpon()
                    .appendQueryParameter(MOVIEDB_API_PARAM, MOVIEDB_API_KEY).build();

            try {
                URL moviePathQueryUrl = new URL(moviePathQueryUri.toString());
                return moviePathQueryUrl;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

        }

        public static URL buildMovieReviewsURL (String ID){
            String baseURLString = BASE_MOVIEDB_QUERY_URL + ID + "/reviews";
            Uri movieReviewsUri = Uri.parse(baseURLString).buildUpon()
                    .appendQueryParameter(MOVIEDB_API_PARAM, MOVIEDB_API_KEY).build();
            try {
                URL movieReviewsURL = new URL(movieReviewsUri.toString());
                Log.e(TAG, "URL: " + movieReviewsURL);
                return movieReviewsURL;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

        }

        public static URL buildTrailerThumbnailURL (String key){
            String baseURL = BASE_TRAILER_THUMBNAIL_URL + key + "/0.jpg";
            try {
                URL trailerThumbnailURL = new URL(baseURL);
                return trailerThumbnailURL;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

        public static URL buildTrailerYoutubeURL (String key){
            String baseURL = BASE_YOUTUBE_VIDEO_URL;
            Uri youtubeTrailerVideoUri = Uri.parse(baseURL).buildUpon()
                    .appendQueryParameter(YOUTUBE_KEY_PARAM, key).build();
            try {
                URL youtubeTrailerVideoURL = new URL(youtubeTrailerVideoUri.toString());
                return youtubeTrailerVideoURL;
            } catch (MalformedURLException e){
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
