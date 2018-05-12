package com.example.android.popular_movies;

import android.content.ContentValues;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.utils.NetworkUtils;
import com.example.android.utils.OpenMovieJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ContentValues[]> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private int mColumns = 2;

    private static final int MOVIE_LOADER_ID = 33;

    //TODO load this preference from the last use.
    private String mlistPreference = "popular";

    //TODO find a solution so that the moviedb attribution doesn't appear on rotation.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, getString(R.string.movieDB_Attribution), Toast.LENGTH_LONG).show();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_mainlist);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, mColumns);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mAdapter = new MovieAdapter();
        mRecyclerView.setAdapter(mAdapter);


        //Time to initialize the loader and begin the background tasks
        LoaderCallbacks<ContentValues[]> callback = MainActivity.this;
        int loaderId = MOVIE_LOADER_ID;

        getSupportLoaderManager().initLoader(loaderId, null, callback);


    }

    @Override
    public Loader<ContentValues[]> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<ContentValues[]>(this) {

            //ContentValues array to hold movie data. Will be sent to MovieAdapter for processing a
            ContentValues[] mMovieData = null;

            // COMPLETED (3) Cache the weather data in a member variable and deliver it in onStartLoading.
            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from OpenWeatherMap in the background.
             *
             * @return Weather data from OpenWeatherMap as an array of Strings.
             *         null if an error occurs
             */
            @Override
            public ContentValues[] loadInBackground() {



                URL movieDbURL = NetworkUtils.buildMovieDBQueryURL(mlistPreference);


                try {
                    String jsonMovieResponse = NetworkUtils
                            .getResponseFromHttpUrl(movieDbURL);

                    ContentValues[] jsonMovieContentValues = OpenMovieJsonUtils
                            .getMovieContentValuesFromJson(MainActivity.this, jsonMovieResponse);

                    return jsonMovieContentValues;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }


            public void deliverResult(ContentValues[] values) {
                mMovieData = values;
                super.deliverResult(values);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ContentValues[]> loader, ContentValues[] data) {
        mAdapter.setmContentValues(data);
        if(data == null){
            Toast.makeText(this, getString(R.string.network_failure_message), Toast.LENGTH_LONG).show();
            Log.e(TAG, "onLoadFinished: Null Data Returned");
        }
    }

    @Override
    public void onLoaderReset(Loader<ContentValues[]> loader) {

    }


}
