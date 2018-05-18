package com.example.android.popular_movies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.utils.NetworkUtils;
import com.example.android.utils.OpenMovieJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ContentValues[]>,
        MovieAdapter.MovieAdapterOnClickHandler {


    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private int mColumns = 2;

    private ContentValues[] mMovieData = null;

    private static final int MOVIE_LOADER_ID = 33;

    //TODO load this preference from the last use.
    SharedPreferences mSharedPreferences;
    public String mlistPreference;

    //TODO find a solution so that the moviedb attribution doesn't appear on rotation.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, getString(R.string.movieDB_Attribution), Toast.LENGTH_LONG).show();

        //Initialize our Recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_mainlist);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, mColumns);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        //load the shared preferences
        mSharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE);
        mlistPreference = mSharedPreferences.getString(getString(R.string.preference_list_order_key), getString(R.string.preference_popular));



        //Time to initialize the loader and begin the background tasks
        LoaderCallbacks<ContentValues[]> callback = MainActivity.this;
        int loaderId = MOVIE_LOADER_ID;

        getSupportLoaderManager().initLoader(loaderId, null, callback);

        if(savedInstanceState != null){
            mAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public Loader<ContentValues[]> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<ContentValues[]>(this) {

            //ContentValues array to hold movie data. Will be sent to MovieAdapter for processing a

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
        return;
    }


    //put extras into the activity
    @Override
    public void onClick(int index) {
        Intent intentToStartDetailActivity = new Intent(MainActivity.this, DetailActivity.class);

        if(mMovieData!= null) {
            //Extras to add to DetailActivity:  String Title, String Overview, float average vote, String poster path, String backdrop path, String release date
            intentToStartDetailActivity.putExtra(getString(R.string.extra_string_title), mMovieData[index].getAsString(OpenMovieJsonUtils.JSON_TITLE_KEY));
            intentToStartDetailActivity.putExtra(getString(R.string.extra_string_overview), mMovieData[index].getAsString(OpenMovieJsonUtils.JSON_OVERVIEW_KEY));
            intentToStartDetailActivity.putExtra(getString(R.string.extra_float_vote_average), mMovieData[index].getAsFloat(OpenMovieJsonUtils.JSON_VOTE_AVG_KEY));
            intentToStartDetailActivity.putExtra(getString(R.string.extra_string_poster_path), mMovieData[index].getAsString(OpenMovieJsonUtils.JSON_POSTER_PATH_KEY));
            intentToStartDetailActivity.putExtra(getString(R.string.extra_string_backdrop_path), mMovieData[index].getAsString(OpenMovieJsonUtils.JSON_BACKDROP_PATH_KEY));
            intentToStartDetailActivity.putExtra(getString(R.string.extra_string_release_date), mMovieData[index].getAsString(OpenMovieJsonUtils.JSON_RELEASE_DATE_KEY));

            startActivity(intentToStartDetailActivity);
        } else {
            Toast.makeText(this, getString(R.string.network_failure_message), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = item.getItemId();

        if(id == R.id.list_menu_popular){
            editor.putString(getString(R.string.preference_list_order_key), getString(R.string.preference_popular));
        } if (id == R.id.list_menu_top_rated){
            editor.putString(getString(R.string.preference_list_order_key), getString(R.string.preference_top_rated));
        }
        editor.commit();

        mlistPreference = sharedPreferences.getString(getString(R.string.preference_list_order_key), getString(R.string.preference_popular));
        mMovieData = null;
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
        mAdapter.notifyDataSetChanged();
//        mRecyclerView.setAdapter(mAdapter);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRestoreInstanceState(Bundle state){
        super.onRestoreInstanceState(state);
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
        mAdapter.notifyDataSetChanged();
    }

}
