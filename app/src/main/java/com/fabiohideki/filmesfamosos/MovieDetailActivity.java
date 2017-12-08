package com.fabiohideki.filmesfamosos;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fabiohideki.filmesfamosos.model.Movie;
import com.fabiohideki.filmesfamosos.utils.MovieUrlUtils;
import com.fabiohideki.filmesfamosos.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;

public class MovieDetailActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Movie movie;
    private TextView tvMovieTitle;
    private TextView tvMovieID;
    private TextView tvMovieReleaseDate;
    private TextView tvMovieOverview;
    private TextView tvMovieTrailersUrl;
    private RatingBar rbMovie;
    private ImageView ivMoviePosterDetail;
    private ImageView ivMovieToolBarPoster;

    private String trailers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        movie = getIntent().getParcelableExtra(Intent.EXTRA_COMPONENT_NAME);

        setTitle(movie.getTitle());

        tvMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        tvMovieID = (TextView) findViewById(R.id.tv_movie_id);
        tvMovieReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);
        tvMovieOverview = (TextView) findViewById(R.id.tv_movie_overview);
        tvMovieTrailersUrl = findViewById(R.id.tv_movie_trailers_url);
        rbMovie = (RatingBar) findViewById(R.id.rb_movie);
        ivMoviePosterDetail = (ImageView) findViewById(R.id.poster_image);
        ivMovieToolBarPoster = (ImageView) findViewById(R.id.movie_toolbar_poster);

        tvMovieTitle.setText(movie.getTitle());
        tvMovieID.setText(movie.getId());
        tvMovieReleaseDate.setText("(" + movie.getReleaseDate().split("-")[0] + ")");
        tvMovieOverview.setText(movie.getOverview());
        rbMovie.setRating(Float.parseFloat(movie.getVoteAverage()) / 2);
        Picasso.with(this).load(MovieUrlUtils.buildUrlPoster(movie.getPosterPath())).into(ivMoviePosterDetail);

        Picasso.with(this).load(MovieUrlUtils.buildUrlPoster(movie.getBackdropPath())).into(ivMovieToolBarPoster);

        URL urlTrailers = NetworkUtils.buildUrlTrailers(movie.getId(), getString(R.string.movie_db_api_key));
        tvMovieTrailersUrl.setText(urlTrailers.toString());

        if (urlTrailers != null) {
            new FetchTrailersTask().execute(urlTrailers);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab_mark);
        fab.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View view) {
                //action fab
                fab.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_check));

                Snackbar.make(view, getString(R.string.mark_favorite), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //action Undo
                                fab.setImageDrawable(getResources().getDrawable(R.drawable.bookmark));
                            }
                        }).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movie_detail, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.menu_share:
                Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                        .setType("text/plain")
                        .setText(getString(R.string.watch_movie) + ": " + movie.getTitle() + " (" + movie.getReleaseDate().split("-")[0] + ")")
                        .getIntent();
                startActivity(shareIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class FetchTrailersTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {

            if (urls.length == 0) {
                return null;
            }

            URL urlString = urls[0];
            String jsonTrailersResult = null;

            try {
                jsonTrailersResult = NetworkUtils.getResponseFromHttpUrl(urlString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return jsonTrailersResult;
        }

        @Override
        protected void onPostExecute(String jsonTrailersResult) {

            if (jsonTrailersResult != null && !("").equals(jsonTrailersResult)) {
                String text = tvMovieTrailersUrl.getText() + "\n\n" + jsonTrailersResult;
                tvMovieTrailersUrl.setText(text);
            }
            
        }
    }

}
