package com.example.android.popular_movies;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.utils.NetworkUtils;
import com.example.android.utils.OpenMovieJsonUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    //TAG for logging purposes
    private static final String TAG = MovieAdapter.class.getSimpleName();

    //Content Values array to hold the movie info.
    private ContentValues[] mContentValues;
    private final Context mContext;
    private ImageView mSelectedImageView;
    private final MovieAdapterOnClickHandler mClickHandler;

    //OnClickHandler defined here, implemented in MainActivity.java
    public interface MovieAdapterOnClickHandler {
        void onClick(int index);
    }

    //Constructor for MovieAdapter
    public MovieAdapter(@NonNull Context context, MovieAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }



    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutId, parent, false);

        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {

        if(mContentValues != null) {
            String posterPath = OpenMovieJsonUtils.getMoviePosterPathfromJSONContentValues(mContentValues, position);
            URL moviePosterUrl = NetworkUtils.buildMoviePosterURL(posterPath);
            if (moviePosterUrl != null) {
                Picasso.with(mContext).load(moviePosterUrl.toString()).into(holder.mImageView);

            }
        }else{
            holder.mImageView.setImageResource(R.drawable.popcorn);
        }
    }

    @Override
    public int getItemCount() {
        if(mContentValues != null) {
            return mContentValues.length;
        }
        else{
            return 16;
        }
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mImageView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.movie_iv);

            itemView.setOnClickListener(this);
        }

        //
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mSelectedImageView = mImageView;
            mClickHandler.onClick(adapterPosition);
        }
    }

    public void setmContentValues (ContentValues[] contentValues){
        mContentValues = contentValues;
        notifyDataSetChanged();
    }

    public ImageView getImageView(){
        return mSelectedImageView;
    }
}
