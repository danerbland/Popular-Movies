package com.example.android.popular_movies;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.utils.NetworkUtils;
import com.example.android.utils.OpenTrailerJsonUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder>{

    private static final String TAG = TrailerAdapter.class.getSimpleName();
    private ContentValues [] mContentValues;
    private final Context mContext;
    private final TrailerOnClickListener mClickListener;

    public interface TrailerOnClickListener {
        void onTrailerClick(String key);
    }

    public TrailerAdapter (Context context, TrailerOnClickListener clickListener){
        mClickListener = clickListener;
        mContext = context;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutId, parent, false);

        return new TrailerAdapter.TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        if(mContentValues!=null){
            String key = mContentValues[position].getAsString(OpenTrailerJsonUtils.JSON_KEY_KEY);
            URL trailerThumbnailURL = NetworkUtils.buildTrailerThumbnailURL(key);
            if (trailerThumbnailURL != null) {
                Picasso.with(mContext).load(trailerThumbnailURL.toString()).into(holder.mImageView);
            }

        }else{
            holder.mImageView.setImageResource(R.drawable.popcorn);
        }

    }

    @Override
    public int getItemCount() {
        if(mContentValues!= null) {
            return mContentValues.length;
        }
        else{
            return 0;
        }
    }

    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView mImageView;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.trailer_imageview);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            String key = mContentValues[adapterPosition].getAsString(OpenTrailerJsonUtils.JSON_KEY_KEY);
            mClickListener.onTrailerClick(key);
        }
    }

    public void setmContentValues (ContentValues [] values){
        mContentValues = values;
        notifyDataSetChanged();
    }
}
