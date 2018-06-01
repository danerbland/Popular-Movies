package com.example.android.utils;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OpenMovieJsonUtils {
    //Define our json keys here
    public static final String JSON_RESULTS_KEY = "results";
    public static final String JSON_VOTE_COUNT_KEY = "vote_count";
    public static final String JSON_ID_KEY = "id";
    public static final String JSON_VIDEO_KEY = "video";
    public static final String JSON_VOTE_AVG_KEY = "vote_average";
    public static final String JSON_TITLE_KEY = "title";
    public static final String JSON_POPULARITY_KEY = "popularity";
    public static final String JSON_POSTER_PATH_KEY = "poster_path";
    public static final String JSON_ORIGINAL_LANGUAGE_KEY = "original_language";
    public static final String JSON_ORIGINAL_TITLE_KEY = "original_title";
    public static final String JSON_GENRE_IDS_KEY = "genre_ids";
    public static final String JSON_BACKDROP_PATH_KEY = "backdrop_path";
    public static final String JSON_ADULT_KEY = "adult";
    public static final String JSON_OVERVIEW_KEY = "overview";
    public static final String JSON_RELEASE_DATE_KEY = "release_date";


    public static ContentValues[] getMovieContentValuesFromJson(Context context, String movieJsonStr)
            throws JSONException {

        JSONObject movieJsonObject = new JSONObject(movieJsonStr);


        JSONArray jsonMovieArray = movieJsonObject.getJSONArray(JSON_RESULTS_KEY);



        ContentValues[] movieContentValues = new ContentValues[jsonMovieArray.length()];



        //Goes through the 'results' JSONArray and creates appends each movie's information to our
        //ContentValues
        for (int i = 0; i < jsonMovieArray.length(); i++) {

            //Declare variables for all of the information we expect about each movie in our JSONArray
            int vote_count;
            int id;
            boolean video;
            String vote_average;
            String title;
            String popularity;
            String poster_path;
            String original_language;
            String original_title;
            JSONArray genre_ids;
            String backdrop_path;
            boolean adult;
            String overview;
            String release_date;

            JSONObject movieInfo = jsonMovieArray.getJSONObject(i);


            //assign values to our variables from the JSON Object.
            vote_count = movieInfo.optInt(JSON_VOTE_COUNT_KEY);
            id = movieInfo.optInt(JSON_ID_KEY);
            video = movieInfo.optBoolean(JSON_VIDEO_KEY);
            vote_average = movieInfo.optString(JSON_VOTE_AVG_KEY);
            title = movieInfo.optString(JSON_TITLE_KEY);
            popularity = movieInfo.optString(JSON_POPULARITY_KEY);
            poster_path = movieInfo.optString(JSON_POSTER_PATH_KEY);
            original_language = movieInfo.optString(JSON_ORIGINAL_LANGUAGE_KEY);
            original_title = movieInfo.optString(JSON_ORIGINAL_TITLE_KEY);
            genre_ids = movieInfo.optJSONArray(JSON_GENRE_IDS_KEY);
            backdrop_path = movieInfo.optString(JSON_BACKDROP_PATH_KEY);
            adult = movieInfo.optBoolean(JSON_ADULT_KEY);
            overview = movieInfo.optString(JSON_OVERVIEW_KEY);
            release_date = movieInfo.optString(JSON_RELEASE_DATE_KEY);


            //We will let our content values use the same keys as the JSON object to prevent confusion.
            //TODO handle the genre_ids tags.
            ContentValues movieValues = new ContentValues();
            movieValues.put(JSON_VOTE_COUNT_KEY, vote_count);
            movieValues.put(JSON_ID_KEY, id);
            movieValues.put(JSON_VIDEO_KEY, video);
            movieValues.put(JSON_VOTE_AVG_KEY, vote_average);
            movieValues.put(JSON_TITLE_KEY, title);
            movieValues.put(JSON_POPULARITY_KEY, popularity);
            movieValues.put(JSON_POSTER_PATH_KEY, poster_path);
            movieValues.put(JSON_ORIGINAL_LANGUAGE_KEY, original_language);
            movieValues.put(JSON_ORIGINAL_TITLE_KEY, original_title);
            //movieValues.put(JSON_GENRE_IDS_KEY, genre_ids);
            movieValues.put(JSON_BACKDROP_PATH_KEY, backdrop_path);
            movieValues.put(JSON_ADULT_KEY, adult);
            movieValues.put(JSON_OVERVIEW_KEY, overview);
            movieValues.put(JSON_RELEASE_DATE_KEY, release_date);


            movieContentValues[i] = movieValues;
        }

        return movieContentValues;
    }

    public static String getMoviePosterPathfromJSONContentValues (ContentValues [] contentValues, int index){
        return contentValues[index].getAsString(JSON_POSTER_PATH_KEY);
    }


}

