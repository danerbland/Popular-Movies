package com.example.android.utils;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OpenTrailerJsonUtils {

    //JSON keys as found by querying tmdb for trailer info.
    public static final String JSON_ID_KEY ="id";
    public static final String JSON_RESULTS_KEY = "results";
    public static final String JSON_KEY_KEY = "key";
    public static final String JSON_NAME_KEY = "name";
    public static final String JSON_SITE_KEY = "site";
    public static final String JSON_SIZE_KEY = "size";


    //getTrailerContentValuesFromJson takes a string of JSON and returns an array of ContentValues, each
    //ContentValue representing an individual trailer.
    public static ContentValues[] getTrailerContentValuesFromJson(String trailersString) throws JSONException {

        JSONObject trailersJSONObject = new JSONObject(trailersString);

        JSONArray trailersJSONArray = trailersJSONObject.getJSONArray(JSON_RESULTS_KEY);

        if (trailersJSONArray.length() == 0) {
            return null;
        }

        ContentValues[] trailersContentValues = new ContentValues[trailersJSONArray.length()];

        for(int i = 0; i < trailersJSONArray.length(); i++){

            String key;
            String site;
            String size;

            JSONObject trailerInfo = trailersJSONArray.getJSONObject(i);

            key = trailerInfo.getString(JSON_KEY_KEY);
            site = trailerInfo.getString(JSON_SITE_KEY);
            size = trailerInfo.getString(JSON_SIZE_KEY);

            ContentValues trailerValues = new ContentValues();

            trailerValues.put(JSON_KEY_KEY, key);
            trailerValues.put(JSON_SITE_KEY, site);
            trailerValues.put(JSON_SIZE_KEY, size);

            trailersContentValues[i] = trailerValues;

        }


        return trailersContentValues;
    }
}
