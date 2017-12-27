package com.fabiohideki.filmesfamosos.utils;

import android.util.Log;

import com.fabiohideki.filmesfamosos.model.Movie;
import com.fabiohideki.filmesfamosos.model.ResultMovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by fabio.lagoa on 22/08/2017.
 */

public class MoviesJsonUtils {

    private static final String TAG = "MoviesJsonUtils";

    //result
    private static final String PAGE = "page";
    private static final String TOTAL_RESULTS = "total_results";
    private static final String TOTAL_PAGES = "total_pages";
    private static final String RESULTS = "results";

    //array
    private static final String ID = "id";
    private static final String POSTER_PATH = "poster_path";
    private static final String TITLE = "title";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String BACKDROP_PATH = "backdrop_path";

    public static ResultMovies getResultMovieFromJson(String jsonMoviesResult) throws JSONException {

        ResultMovies resultMovies = new ResultMovies();

        JSONObject resultJson = new JSONObject(jsonMoviesResult);

        resultMovies.setPage(resultJson.getString(PAGE));
        resultMovies.setTotalPages(resultJson.getString(TOTAL_PAGES));
        resultMovies.setTotalResults(resultJson.getString(TOTAL_RESULTS));

        Log.i(TAG, "getResultMovieFromJson: " + "page: " + resultMovies.getPage() + " total_results: " + resultMovies.getTotalResults() + " total_pages: " + resultMovies.getTotalPages());

        JSONArray resultMoviesJson = resultJson.getJSONArray(RESULTS);

        ArrayList<Movie> resultMoviesArray = new ArrayList();

        for (int i = 0; i < resultMoviesJson.length(); i++) {
            JSONObject movieJson = resultMoviesJson.getJSONObject(i);
            Movie movie = new Movie(
                    movieJson.getString(ID),
                    movieJson.getString(POSTER_PATH),
                    movieJson.getString(TITLE),
                    movieJson.getString(OVERVIEW),
                    movieJson.getString(VOTE_AVERAGE),
                    movieJson.getString(RELEASE_DATE),
                    movieJson.getString(BACKDROP_PATH)
            );

            resultMoviesArray.add(movie);
        }

        resultMovies.setMovies(resultMoviesArray);

        return resultMovies;
    }

}
