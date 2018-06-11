package com.example.android.popular_movies;

import android.content.ContentValues;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.utils.OpenReviewsJsonUtils;

public class ReviewAdapter  extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder>{

    //TODO implement this

    private static final String TAG = ReviewAdapter.class.getSimpleName();
    private ContentValues[] mContentValues;
    private final Context mContext;

    public interface ReviewOnClickListener{
        public void onReviewClick();
    }

    public ReviewAdapter (Context context){
        mContext = context;
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutID = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutID, parent, false);

        return new ReviewAdapter.ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        if(mContentValues != null){
            String Author = mContentValues[position].getAsString(OpenReviewsJsonUtils.JSON_AUTHOR_KEY);
            String Content = mContentValues[position].getAsString(OpenReviewsJsonUtils.JSON_CONTENT_KEY);

            holder.mAuthorTextview.setText(Author);
            holder.mContentTextview.setText(Content);
        }
    }

    @Override
    public int getItemCount() {
        if(mContentValues!=null){
            return mContentValues.length;
        } else {
            return 0;
        }
    }

    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView mAuthorTextview;
        public final TextView mContentTextview;
        public final TextView mShowMoreTextView;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            mAuthorTextview = itemView.findViewById(R.id.detail_author_textview);
            mContentTextview = itemView.findViewById(R.id.detail_review_content_textview);
            mShowMoreTextView = itemView.findViewById(R.id.detail_show_more_textview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.e(TAG, "onClickFired");
            final ViewGroup.LayoutParams contentTextviewLayoutParams = mContentTextview.getLayoutParams();
            contentTextviewLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mContentTextview.setLayoutParams(contentTextviewLayoutParams);
            mShowMoreTextView.setVisibility(View.GONE);
        }
    }

    public void setmContentValues (ContentValues[] values){
        mContentValues = values;
        notifyDataSetChanged();
    }
}
