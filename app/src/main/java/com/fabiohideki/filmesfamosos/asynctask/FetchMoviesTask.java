package com.fabiohideki.filmesfamosos.asynctask;

import android.os.AsyncTask;
import android.widget.TextView;

import com.fabiohideki.filmesfamosos.MainActivity;
import com.fabiohideki.filmesfamosos.R;
import com.fabiohideki.filmesfamosos.model.ResultMovies;
import com.fabiohideki.filmesfamosos.utils.MoviesJsonUtils;
import com.fabiohideki.filmesfamosos.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class FetchMoviesTask extends AsyncTask<URL, Void, String> {

    private final MainActivity activity;
    private TextView resultsTextView;


    public FetchMoviesTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        resultsTextView = (TextView) activity.findViewById(R.id.tv_test);

        activity.showProgressBar(true);
    }

    @Override
    protected String doInBackground(URL... params) {

        if (params.length == 0) {
            return null;
        }

        URL urlString = params[0];
        String jsonMoviesResult = null;
        ResultMovies resultMovies = new ResultMovies();

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

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < resultMovies.getMovies().size(); i++) {
            stringBuilder.append(resultMovies.getMovies().get(i).getTitle());
            stringBuilder.append("\r\n");
        }


        return stringBuilder.toString();
    }

    @Override
    protected void onPostExecute(String moviesResult) {
        activity.showProgressBar(false);

        if (moviesResult != null && !moviesResult.equals("")) {
            activity.showMoviesData();
            resultsTextView.setText(moviesResult);
        } else {
            activity.showErrorMessage();
        }
    }
}
