package com.fabiohideki.filmesfamosos.utils;

import android.content.Context;
import android.util.Log;

import com.fabiohideki.filmesfamosos.model.ResultTrailers;
import com.fabiohideki.filmesfamosos.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by fabio.lagoa on 18/12/2017.
 */

public class TrailersJsonUtils {

    private static final String TAG = "TrailersJsonUtils";

    //array
    private static final String ID = "id";
    private static final String RESULTS = "results";

    private static final String NAME = "name";
    private static final String SIZE = "size";
    private static final String SITE = "site";
    private static final String KEY = "key";

    public static ResultTrailers getResultTrailersFromJson(Context context, String jsonTrailesResult) throws JSONException {

        Log.d("TrailersJsonUtils", "getResultTrailersFromJson: " + jsonTrailesResult);

        ResultTrailers resultTrailers = new ResultTrailers();

        JSONObject resultJson = new JSONObject(jsonTrailesResult);

        resultTrailers.setId(resultJson.getString(ID));

        JSONArray trailersArrayJson = resultJson.getJSONArray(RESULTS);

        ArrayList<Trailer> trailers = new ArrayList<>();

        for (int i = 0; i < trailersArrayJson.length(); i++) {
            JSONObject trailerJson = trailersArrayJson.getJSONObject(i);

            Trailer trailer = new Trailer(
                    trailerJson.getString(ID),
                    trailerJson.getString(NAME),
                    trailerJson.getString(SIZE),
                    trailerJson.getString(SITE),
                    trailerJson.getString(KEY)

            );

            Log.d("TrailersJsonUtils",
                    "id: " + trailerJson.getString(ID) + "\n" +
                            "name: " + trailerJson.getString(NAME) + "\n" +
                            "site: " + trailerJson.getString(SIZE) + "\n" +
                            "key: " + trailerJson.getString(KEY) + "\n" +
                            "size: " + trailerJson.getString(SITE) + "\n\n");

            trailers.add(trailer);
        }

        resultTrailers.setTrailers(trailers);

        return resultTrailers;
    }

}
