package com.example.android.popular_movies;

import android.content.ContentValues;
import android.databinding.DataBindingUtil;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.example.android.popular_movies.databinding.ActivityDetailBinding;
import com.example.android.utils.NetworkUtils;
import com.example.android.utils.OpenMovieJsonUtils;
import com.example.android.utils.OpenTrailerJsonUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ContentValues[]>{

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final int TRAILER_LOADER_ID = 44;

    ActivityDetailBinding mBinding;
    private TrailerAdapter mAdapter;
    private ContentValues[] mTrailerContentValues = null;
    private String mMovieID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //TODO - set adapter for trailer recyclerview and load trailer data.
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);



        //get the extras from the bundle
        Bundle bundle = getIntent().getExtras();
        String movieID = bundle.getString(OpenMovieJsonUtils.JSON_ID_KEY);
        String backdropPath = bundle.getString(getString(R.string.extra_string_backdrop_path));
        String posterPath = bundle.getString(getString(R.string.extra_string_poster_path));
        String title = bundle.getString(getString(R.string.extra_string_title));
        String overview = bundle.getString(getString(R.string.extra_string_overview));
        Float voteAverage = bundle.getFloat(getString(R.string.extra_float_vote_average));
        String releaseDate = bundle.getString(getString(R.string.extra_string_release_date));

        //split the date into parts and save the movie ID for use by the loader.
        String[] dateParts = releaseDate.split("-");
        mMovieID = movieID;

        //Load the movie poster into the ImageView via Picasso
       URL moviePosterUrl = NetworkUtils.buildMoviePosterURL(posterPath);
        if (moviePosterUrl != null) {
            Picasso.with(this).load(moviePosterUrl.toString()).into(mBinding.detailPosterImageview);
        }

        Log.e(TAG, NetworkUtils.buildMovieTrailersURL(movieID).toString());

        //update the detail Textviews via Data Binding
        mBinding.detailTitleTextview.setText(title);
        mBinding.detailOverviewTextview.setText(overview);
        mBinding.detailRatingBar.setRating(voteAverage);
        mBinding.detailRatingBar.setVisibility(View.GONE);
        mBinding.detailVoteAverageTextview.setText(Float.toString(voteAverage)+ getString(R.string.detail_rating_rubric));
        mBinding.detailReleaseDateTextview.setText(dateParts[0]);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mBinding.detailTrailersRecyclerview.setLayoutManager(horizontalLayoutManager);
        mAdapter = new TrailerAdapter(DetailActivity.this);
        mBinding.detailTrailersRecyclerview.setAdapter(mAdapter);


        //Time to query TMDb again for trailer info.  Another Loader is in order
        //Time to initialize the loader and begin the background tasks
        LoaderManager.LoaderCallbacks<ContentValues[]> callback = DetailActivity.this;

        getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, callback);

        if(savedInstanceState != null){
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public Loader<ContentValues[]> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<ContentValues[]>(this) {

            @Override
            protected void onStartLoading() {
                if (mTrailerContentValues != null){
                    deliverResult(mTrailerContentValues);
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
                URL trailerDataURL = NetworkUtils.buildMovieTrailersURL(mMovieID);

                if(trailerDataURL != null) {
                    try {
                        String jsonMovieResponse = NetworkUtils
                                .getResponseFromHttpUrl(trailerDataURL);

                        return OpenTrailerJsonUtils
                                .getTrailerContentValuesFromJson(DetailActivity.this, jsonMovieResponse);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                } else{
                    return null;
                }
            }


            public void deliverResult(ContentValues[] values) {
                mTrailerContentValues = values;
                super.deliverResult(values);
            }
        };
    }


    //TODO handle data = null situation. Remove Toast?
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
        mTrailerContentValues = null;
        mAdapter.setmContentValues(null);
    }
}
