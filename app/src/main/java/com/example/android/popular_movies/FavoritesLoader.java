package com.example.android.popular_movies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import android.util.Log;

import com.example.android.popular_movies.data.MovieContract;
import com.example.android.utils.OpenMovieJsonUtils;

public class FavoritesLoader {

    private static String TAG = FavoritesLoader.class.getSimpleName();
    private static final int FAVORITES_LOADER_ID = 44;
    private Cursor mCursor;
    private Context mContext;

    FavoritesLoader(Context context){
        mContext = context;
    }

    private Cursor getFavorites(){

        mCursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        return mCursor;
    }


    public ContentValues[] getFavoritesContentValues(){

        getFavorites();

        ContentValues[] favoritesContentValues = new ContentValues[mCursor.getCount()];

        for(int i = 0; i < mCursor.getCount(); i++){
            mCursor.moveToPosition(i);
            ContentValues cv = new ContentValues();
            cv.put(OpenMovieJsonUtils.JSON_ID_KEY, mCursor.getString(mCursor.getColumnIndex(OpenMovieJsonUtils.JSON_ID_KEY)));
            cv.put(OpenMovieJsonUtils.JSON_TITLE_KEY, mCursor.getString(mCursor.getColumnIndex(OpenMovieJsonUtils.JSON_TITLE_KEY)));
            cv.put(OpenMovieJsonUtils.JSON_POSTER_PATH_KEY, mCursor.getString(mCursor.getColumnIndex(OpenMovieJsonUtils.JSON_POSTER_PATH_KEY)));
            cv.put(OpenMovieJsonUtils.JSON_RELEASE_DATE_KEY, mCursor.getString(mCursor.getColumnIndex(OpenMovieJsonUtils.JSON_RELEASE_DATE_KEY)));
            cv.put(OpenMovieJsonUtils.JSON_VOTE_AVG_KEY, mCursor.getString(mCursor.getColumnIndex(OpenMovieJsonUtils.JSON_VOTE_AVG_KEY)));
            cv.put(OpenMovieJsonUtils.JSON_OVERVIEW_KEY, mCursor.getString(mCursor.getColumnIndex(OpenMovieJsonUtils.JSON_OVERVIEW_KEY)));

            favoritesContentValues[i] = cv;

        }

        return favoritesContentValues;
    }
}
