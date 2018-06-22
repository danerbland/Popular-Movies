package com.example.android.popular_movies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
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

    //TAG for logging purposes
    private static final String TAG = MainActivity.class.getSimpleName();
    //ID for Movie Loader
    private static final int MOVIE_LOADER_ID = 33;


    //Recyclerview and Adapter
    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private final int mColumns = 2;

    //FavoritesLoader for populating the favorites list.
    private FavoritesLoader mFavoritesLoader;

    //Rotation Sentinel
    private boolean mRotationSentinel = false;

    private ContentValues[] mMovieData = null;

    private SharedPreferences mSharedPreferences;
    private String mlistPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null) {
            mRotationSentinel = savedInstanceState.getBoolean(getString(R.string.saved_state_bool_key));
        }

        if(!mRotationSentinel){
            Toast.makeText(this, getString(R.string.movieDB_Attribution), Toast.LENGTH_LONG).show();
        }
        //Initialize our Recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_mainlist);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, mColumns);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(false);

        //Set our Adapter on our RecyclerView
        mAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        //load the shared preferences
        mSharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE);
        mlistPreference = mSharedPreferences.getString(getString(R.string.preference_list_order_key), getString(R.string.preference_popular));

        setActionBarHeader(getHeader());

        //If mlistPreference is not 'favorites', initialize the loader.  Instead, load the data from the DB.
        if(!mlistPreference.equals(getString(R.string.preference_favorites))) {
            //Time to initialize the loader and begin the background tasks
            LoaderCallbacks<ContentValues[]> callback = MainActivity.this;

            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, callback);
        } else {
            mFavoritesLoader = new FavoritesLoader(this);
            ContentValues[] values = mFavoritesLoader.getFavoritesContentValues();
            mMovieData = values;
            if(values != null) {
                mAdapter.setmContentValues(values);
                mAdapter.notifyDataSetChanged();

            } else {
                Log.e(TAG, "getFavorites returned Null Cursor");
            }
        }
        if(savedInstanceState != null){
            mAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public Loader<ContentValues[]> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<ContentValues[]>(this) {

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

                if(movieDbURL != null) {
                    try {

                        String jsonMovieResponse = NetworkUtils
                                .getResponseFromHttpUrl(movieDbURL);

                        return OpenMovieJsonUtils
                                .getMovieContentValuesFromJson(jsonMovieResponse);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                } else{
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
        mMovieData = null;
        mAdapter.setmContentValues(null);
    }


    //put extras into the activity
    @Override
    public void onClick(int index) {
        Intent intentToStartDetailActivity = new Intent(MainActivity.this, DetailActivity.class);

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, mAdapter.getImageView(), getString(R.string.image_transition_name));

        if(mMovieData!= null) {
            //Extras to add to DetailActivity:  String Title, String Overview, float average vote, String poster path, String backdrop path, String release date
            intentToStartDetailActivity.putExtra(getString(R.string.extra_string_id), mMovieData[index].getAsString(OpenMovieJsonUtils.JSON_ID_KEY));
            intentToStartDetailActivity.putExtra(getString(R.string.extra_string_title), mMovieData[index].getAsString(OpenMovieJsonUtils.JSON_TITLE_KEY));
            intentToStartDetailActivity.putExtra(getString(R.string.extra_string_overview), mMovieData[index].getAsString(OpenMovieJsonUtils.JSON_OVERVIEW_KEY));
            intentToStartDetailActivity.putExtra(getString(R.string.extra_float_vote_average), mMovieData[index].getAsFloat(OpenMovieJsonUtils.JSON_VOTE_AVG_KEY));
            intentToStartDetailActivity.putExtra(getString(R.string.extra_string_poster_path), mMovieData[index].getAsString(OpenMovieJsonUtils.JSON_POSTER_PATH_KEY));
            intentToStartDetailActivity.putExtra(getString(R.string.extra_string_backdrop_path), mMovieData[index].getAsString(OpenMovieJsonUtils.JSON_BACKDROP_PATH_KEY));
            intentToStartDetailActivity.putExtra(getString(R.string.extra_string_release_date), mMovieData[index].getAsString(OpenMovieJsonUtils.JSON_RELEASE_DATE_KEY));

            startActivity(intentToStartDetailActivity, options.toBundle());
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

        if(id == R.id.list_menu_popular || id == R.id.list_menu_top_rated) {

            if (id == R.id.list_menu_popular) {
                editor.putString(getString(R.string.preference_list_order_key), getString(R.string.preference_popular));
            }
            if (id == R.id.list_menu_top_rated) {
                editor.putString(getString(R.string.preference_list_order_key), getString(R.string.preference_top_rated));
            }
            editor.apply();

            mlistPreference = sharedPreferences.getString(getString(R.string.preference_list_order_key), getString(R.string.preference_popular));
            mMovieData = null;

            //We need to check if the loader is already running.  If it is, we will restart it. If not, we will initialize it.

            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);

            mAdapter.notifyDataSetChanged();
        } else if (id == R.id.list_menu_favorites){

            editor.putString(getString(R.string.preference_list_order_key), getString(R.string.preference_favorites));
            editor.apply();

            mlistPreference = sharedPreferences.getString(getString(R.string.preference_list_order_key), getString(R.string.preference_popular));

            mFavoritesLoader = new FavoritesLoader(this);
            ContentValues[] values = mFavoritesLoader.getFavoritesContentValues();
            mMovieData = values;

            if(values != null) {
                mAdapter.setmContentValues(values);
                mAdapter.notifyDataSetChanged();

            } else {
                Log.e(TAG, "getFavorites returned Null Cursor");
            }

        }

        setActionBarHeader(getHeader());

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        //Re-load the favorites list, as it may have changed since looking at a movie.
        if(mlistPreference.equals(getString(R.string.preference_favorites))){
            mFavoritesLoader = new FavoritesLoader(this);
            ContentValues[] values = mFavoritesLoader.getFavoritesContentValues();
            mMovieData = values;

            if(values != null) {
                mAdapter.setmContentValues(values);
                mAdapter.notifyDataSetChanged();

            } else {
                Log.e(TAG, "getFavorites returned Null Cursor");
            }
        }

        super.onResume();

    }

    @Override
    public void onRestoreInstanceState(Bundle state){
        super.onRestoreInstanceState(state);
        if(mlistPreference.equals(getString(R.string.preference_popular)) || mlistPreference.equals(getString(R.string.preference_top_rated))) {
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle b){
        super.onSaveInstanceState(b);
        b.putBoolean(getString(R.string.saved_state_bool_key), true);
    }

    //TODO set the action bar to have a different heading.
    private void setActionBarHeader(String heading) {
        // TODO Auto-generated method stub

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(heading);
        actionBar.show();

    }

    private String getHeader(){
        if(mlistPreference.equals(getString(R.string.preference_top_rated))){
            return getString(R.string.header_top_rated);
        } else if (mlistPreference.equals(getString(R.string.preference_popular))){
            return getString(R.string.header_popular);
        } else {
            return getString(R.string.header_favorites);
        }
    }


}



/*
TODO add infinite scroll/load more movies.  Fix Rating Bar.


 */
