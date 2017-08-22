package com.fabiohideki.filmesfamosos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fabiohideki.filmesfamosos.asynctask.FetchMoviesTask;
import com.fabiohideki.filmesfamosos.utils.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRvMovies;
    private TextView mTvErrorMessage;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //finds
        mRvMovies = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mTvErrorMessage = (TextView) findViewById(R.id.tv_error_message_display);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        //ImageView imageView = (ImageView) findViewById(R.id.iv_example);
        //Picasso.with(this).load("http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg").into(imageView);

        loadMoviesData();

    }

    private void loadMoviesData() {
        showMoviesData();

        URL urlTheMovieDB = NetworkUtils.buildUrlMovie(NetworkUtils.POPULAR, getString(R.string.movie_db_api_key), null);
        if (urlTheMovieDB != null) {
            new FetchMoviesTask(this).execute(urlTheMovieDB);
        }
    }

    public void showMoviesData() {
        mTvErrorMessage.setVisibility(View.INVISIBLE);
        mRvMovies.setVisibility(View.VISIBLE);
    }

    public void showProgressBar(boolean visible) {
        progressBar.setVisibility((visible) ? View.VISIBLE : View.INVISIBLE);
    }

    public void showErrorMessage() {
        mTvErrorMessage.setVisibility(View.VISIBLE);
        mRvMovies.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Item name");
        return super.onCreateOptionsMenu(menu);
    }
}
