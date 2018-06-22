package com.example.android.popular_movies.data;

import android.net.Uri;

import com.example.android.utils.OpenMovieJsonUtils;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popular_movies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";


        public static final String COLUMN_ID = OpenMovieJsonUtils.JSON_ID_KEY;
        public static final String COLUMN_TITLE = OpenMovieJsonUtils.JSON_TITLE_KEY;
        public static final String COLUMN_POSTER_PATH = OpenMovieJsonUtils.JSON_POSTER_PATH_KEY;
        public static final String COLUMN_OVERVIEW = OpenMovieJsonUtils.JSON_OVERVIEW_KEY;
        public static final String COLUMN_VOTE_AVERAGE = OpenMovieJsonUtils.JSON_VOTE_AVG_KEY;
        public static final String COLUMN_RELEASE_DATE = OpenMovieJsonUtils.JSON_RELEASE_DATE_KEY;


    }
}
