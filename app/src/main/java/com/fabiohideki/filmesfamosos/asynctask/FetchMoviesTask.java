package com.fabiohideki.filmesfamosos.asynctask;

import android.os.AsyncTask;

import com.fabiohideki.filmesfamosos.MainActivity;
import com.fabiohideki.filmesfamosos.model.ResultMovies;
import com.fabiohideki.filmesfamosos.utils.MoviesJsonUtils;
import com.fabiohideki.filmesfamosos.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class FetchMoviesTask extends AsyncTask<URL, Void, ResultMovies> {

    private static final String TAG = "FetchMoviesTask";

    private final MainActivity activity;
    //private TextView resultsTextView;
    //private ImageView imTeste;


    public FetchMoviesTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //resultsTextView = (TextView) activity.findViewById(R.id.tv_test);
        //imTeste = (ImageView) activity.findViewById(R.id.iv_teste);

        activity.showProgressBar(true);
    }

    @Override
    protected ResultMovies doInBackground(URL... params) {

        if (params.length == 0) {
            return null;
        }

        URL urlString = params[0];
        String jsonMoviesResult = null;
        ResultMovies resultMovies = null;

        try {
            jsonMoviesResult = NetworkUtils.getResponseFromHttpUrl(urlString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (jsonMoviesResult != null && !("").equals(jsonMoviesResult)) {
            try {
                resultMovies = MoviesJsonUtils.getResultMovieFromJson(activity, jsonMoviesResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return resultMovies;
    }

    @Override
    protected void onPostExecute(ResultMovies resultMovies) {
        activity.showProgressBar(false);

        if (resultMovies != null) {
            activity.showMoviesData();
            activity.setGridViewAdapter(resultMovies);

        } else {
            activity.showErrorMessage();
        }
    }
}
