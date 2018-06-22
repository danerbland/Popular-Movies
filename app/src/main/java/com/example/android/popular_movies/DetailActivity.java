package com.example.android.popular_movies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.example.android.popular_movies.data.MovieContract;
import com.example.android.popular_movies.databinding.ActivityDetailBinding;
import com.example.android.utils.NetworkUtils;
import com.example.android.utils.OpenMovieJsonUtils;
import com.example.android.utils.OpenReviewsJsonUtils;
import com.example.android.utils.OpenTrailerJsonUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ContentValues[][]>,
        TrailerAdapter.TrailerOnClickListener{

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final int TRAILER_LOADER_ID = 44;

    private ActivityDetailBinding mBinding;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private ContentValues[][] mContentValues = new ContentValues[2][];
    private ContentValues mMovieContentValues  = new ContentValues();
    private String mMovieID;
    private boolean mFavorite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);


        //get the extras from the bundle
        Bundle bundle = getIntent().getExtras();
        String movieID = bundle.getString(OpenMovieJsonUtils.JSON_ID_KEY);
        mMovieContentValues.put(MovieContract.MovieEntry.COLUMN_ID, Integer.valueOf(movieID));
        String title = bundle.getString(getString(R.string.extra_string_title));
        mMovieContentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
        String posterPath = bundle.getString(getString(R.string.extra_string_poster_path));
        mMovieContentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
        String overview = bundle.getString(getString(R.string.extra_string_overview));
        mMovieContentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
        String releaseDate = bundle.getString(getString(R.string.extra_string_release_date));
        mMovieContentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
        Float voteAverage = bundle.getFloat(getString(R.string.extra_float_vote_average));
        mMovieContentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);

        //split the date into parts and save the movie ID for use by the loader.
        String[] dateParts = releaseDate.split("-");
        mMovieID = movieID;

        mFavorite = getIsFavorite();
        Log.d(TAG, "mFavorite = " + mFavorite);

        getSupportActionBar().setTitle(title);

        //Load the movie poster into the ImageView via Picasso
       URL moviePosterUrl = NetworkUtils.buildMoviePosterURL(posterPath);
        if (moviePosterUrl != null) {
            Picasso.with(this).load(moviePosterUrl.toString()).into(mBinding.detailPosterImageview);
        }

        //update the detail Textviews via Data Binding
        mBinding.detailTitleTextview.setText(title);
        mBinding.detailOverviewTextview.setText(overview);
        mBinding.detailRatingBar.setRating(voteAverage);
        mBinding.detailRatingBar.setVisibility(View.GONE);
        mBinding.detailVoteAverageTextview.setText(Float.toString(voteAverage)+ getString(R.string.detail_rating_rubric));
        mBinding.detailReleaseDateTextview.setText(dateParts[0]);

        //Animate the Favorite button as needed
        animateFavoriteButton(getIsFavorite(), mBinding.detailFavoriteButton);

        //Instantiate Trailer RecyclerView
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mBinding.detailTrailersRecyclerview.setLayoutManager(horizontalLayoutManager);
        mTrailerAdapter = new TrailerAdapter(DetailActivity.this, this);
        mBinding.detailTrailersRecyclerview.setAdapter(mTrailerAdapter);

        //Instantiate Reviews Recyclerview
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.detailReviewsRecyclerview.setLayoutManager(verticalLayoutManager);
        mReviewAdapter = new ReviewAdapter(this);
        mBinding.detailReviewsRecyclerview.setAdapter(mReviewAdapter);


        //Time to query TMDb again for trailer info.  Another Loader is in order
        //Time to initialize the loader and begin the background tasks
        LoaderManager.LoaderCallbacks<ContentValues[][]> callback = DetailActivity.this;

        getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, callback);

        if(savedInstanceState != null){
            mTrailerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public Loader<ContentValues[][]> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<ContentValues[][]>(this) {

            @Override
            protected void onStartLoading() {
                if (mContentValues[0] != null && mContentValues[1] != null){
                    deliverResult(mContentValues);
                } else {
                    forceLoad();
                }
            }


            /* This load in Background callback will also load the reviews for us.



             */
            @Override
            public ContentValues[][] loadInBackground() {
                URL trailerDataURL = NetworkUtils.buildMovieTrailersURL(mMovieID);
                URL reviewDataURL = NetworkUtils.buildMovieReviewsURL(mMovieID);

                if(reviewDataURL != null) {
                    try {
                        String jsonReviewsResponse = NetworkUtils
                                .getResponseFromHttpUrl(reviewDataURL);

                        mContentValues[0] = OpenReviewsJsonUtils
                                .getReviewsContentValuesFromJson(jsonReviewsResponse);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(trailerDataURL != null) {
                    try {
                        String jsonMovieResponse = NetworkUtils
                                .getResponseFromHttpUrl(trailerDataURL);

                        mContentValues[1] =  OpenTrailerJsonUtils
                                .getTrailerContentValuesFromJson(jsonMovieResponse);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                return mContentValues;
            }


            public void deliverResult(ContentValues[][] values) {
                super.deliverResult(values);
            }
        };
    }


    //TODO handle data = null situation. Remove Toast?
    @Override
    public void onLoadFinished(Loader<ContentValues[][]> loader, ContentValues[][] data) {
        mTrailerAdapter.setmContentValues(data[1]);
        mReviewAdapter.setmContentValues(data[0]);

        if(data[1] == null){
            mBinding.detailTrailerLabel.setVisibility(View.GONE);
        }
        if(data[0] == null){
            mBinding.detailReviewsLabel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ContentValues[][]> loader) {
        mContentValues = null;
        mTrailerAdapter.setmContentValues(null);
        mReviewAdapter.setmContentValues(null);
    }

    @Override
    public void onTrailerClick(String key) {
        URL youtubeVideoURL = NetworkUtils.buildTrailerYoutubeURL(key);
        Uri youtubeVideoUri = Uri.parse(youtubeVideoURL.toString());
        Log.e(TAG, youtubeVideoURL.toString());

        Intent intentToLaunchVideo = new Intent(Intent.ACTION_VIEW, youtubeVideoUri);
        startActivity(intentToLaunchVideo);
    }


    //When the Favorite Button is clicked, check if it is already a favorite.  If not, add this movie to the favorites database.
    //If it is a favorite, delete it from the database.  In either case, change the Button to reflect the
    public void onFavoriteButtonClick(View v){
        mFavorite = !mFavorite;
        String uriString = MovieContract.MovieEntry.CONTENT_URI.toString() + "/" + mMovieID;
        Uri MovieUri = Uri.parse(uriString);
        ContentResolver contentResolver = getContentResolver();


        if(mFavorite){
            contentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, mMovieContentValues);
            //Insert the movie into the favorites
        } else{
            //Delete the movie from the favorites;
            contentResolver.delete(MovieUri, null, null);
        }

        animateFavoriteButton(mFavorite, mBinding.detailFavoriteButton);

    }

    private void animateFavoriteButton(boolean isFavorite, Button button){
        if(isFavorite){
            button.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            button.setTextColor(getResources().getColor(R.color.colorOffWhite));
            button.setText(getResources().getString(R.string.detail_unfavorite_label));
        } else{
            button.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            button.setTextColor(getResources().getColor(R.color.colorBlack));
            button.setText(getResources().getString(R.string.detail_favorite_label));
        }
    }

    private boolean getIsFavorite(){

        String uriString = MovieContract.MovieEntry.CONTENT_URI.toString() + "/" + mMovieID;
        Uri MovieUri = Uri.parse(uriString);
        ContentResolver contentResolver = getContentResolver();
        Cursor favoriteCursor = contentResolver.query(MovieUri, null, null, null, null);
        boolean isFavorite = (favoriteCursor.getCount() != 0);
        favoriteCursor.close();
        return(isFavorite);

    }

}
