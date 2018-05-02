package com.example.android.popular_movies;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {


    @Override
    //TODO declare the context and layoutID.  Inflate the View
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    //TODO Access Data to bind image to the viewholder's imageview.
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {

    }

    @Override
    //TODO return the number of movies in the data set.
    public int getItemCount() {
        return 0;
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder {

        public final ImageView mImageView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.movie_iv);
        }
    }
}
