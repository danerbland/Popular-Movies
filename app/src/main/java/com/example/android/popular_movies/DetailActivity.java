package com.example.android.popular_movies;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


import com.example.android.popular_movies.databinding.ActivityDetailBinding;
import com.example.android.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    ActivityDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        //get the extras from the bundle
        Bundle bundle = getIntent().getExtras();
        String backdropPath = bundle.getString(getString(R.string.extra_string_backdrop_path));
        String posterPath = bundle.getString(getString(R.string.extra_string_poster_path));
        String title = bundle.getString(getString(R.string.extra_string_title));
        String overview = bundle.getString(getString(R.string.extra_string_overview));
        Float voteAverage = bundle.getFloat(getString(R.string.extra_float_vote_average));
        String releaseDate = bundle.getString(getString(R.string.extra_string_release_date));

        String[] dateParts = releaseDate.split("-");

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






    }
}
