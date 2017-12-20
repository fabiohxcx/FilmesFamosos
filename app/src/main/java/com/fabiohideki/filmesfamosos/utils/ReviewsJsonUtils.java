package com.fabiohideki.filmesfamosos.utils;

import android.content.Context;
import android.util.Log;

import com.fabiohideki.filmesfamosos.model.ResultReviews;
import com.fabiohideki.filmesfamosos.model.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hidek on 19/12/2017.
 */

public class ReviewsJsonUtils {
    private static final String TAG = "ReviewsJsonUtils";

    private static final String ID = "id";
    private static final String PAGE = "page";
    private static final String RESULTS = "results";

    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";
    private static final String URL = "url";

    public static ResultReviews getResultReviewsFromJson(Context context, String jsonReviewsResult) throws JSONException {

        Log.d("ReviewsJsonUtils", "getResultReviewsFromJson: " + jsonReviewsResult);

        ResultReviews resultReviews = new ResultReviews();

        JSONObject resultJson = new JSONObject(jsonReviewsResult);

        resultReviews.setId(resultJson.getString(ID));
        resultReviews.setPage(resultJson.getInt(PAGE));

        JSONArray reviewsArrayJson = resultJson.getJSONArray(RESULTS);

        ArrayList<Review> reviews = new ArrayList<>();

        for (int i = 0; i < reviewsArrayJson.length(); i++) {
            JSONObject reviewsJson = reviewsArrayJson.getJSONObject(i);

            Review review = new Review(
                    reviewsJson.getString(ID),
                    reviewsJson.getString(AUTHOR),
                    reviewsJson.getString(CONTENT),
                    reviewsJson.getString(URL)
            );

            reviews.add(review);
        }

        resultReviews.setReviews(reviews);

        return resultReviews;
    }

}
