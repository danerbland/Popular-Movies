package com.example.android.utils;

import android.content.ContentValues;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OpenReviewsJsonUtils {

    public static final String JSON_ID_KEY = "id";
    public static final String JSON_RESULTS_KEY = "results";
    public static final String JSON_AUTHOR_KEY = "author";
    public static final String JSON_CONTENT_KEY = "content";
    public static final String JSON_URL_KEY = "url";

    public static ContentValues[] getReviewsContentValuesFromJson(String reviewsString) throws JSONException {

        JSONObject reviewsJSONObject = new JSONObject(reviewsString);

        JSONArray reviewsJSONArray = reviewsJSONObject.getJSONArray(JSON_RESULTS_KEY);

        if (reviewsJSONArray.length() == 0) {
            return null;
        }

        ContentValues[] trailersContentValues = new ContentValues[reviewsJSONArray.length()];

        for(int i = 0; i < reviewsJSONArray.length(); i++){

            String author;
            String content;
            String url;

            JSONObject trailerInfo = reviewsJSONArray.getJSONObject(i);

            author = trailerInfo.getString(JSON_AUTHOR_KEY);
            content = trailerInfo.getString(JSON_CONTENT_KEY);
            url = trailerInfo.getString(JSON_URL_KEY);

            ContentValues trailerValues = new ContentValues();

            trailerValues.put(JSON_AUTHOR_KEY, author);
            trailerValues.put(JSON_CONTENT_KEY, content);
            trailerValues.put(JSON_URL_KEY, url);

            trailersContentValues[i] = trailerValues;
        }
        return trailersContentValues;
    }


}
