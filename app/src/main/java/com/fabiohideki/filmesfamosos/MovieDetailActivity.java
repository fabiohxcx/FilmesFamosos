package com.fabiohideki.filmesfamosos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

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

    private static final String SEARCH_QUERY_URL_EXTRA = "query";
    private static final int MOVIE_DETAIL_LOADER = 22;

    private Bundle queryBundle = null;


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

        getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER, queryBundle, this);

        //Trailers
        URL urlTrailers = NetworkUtils.buildUrlTrailers(movie.getId(), getString(R.string.movie_db_api_key));
        tvMovieTrailersUrl.setText(urlTrailers.toString());

        if (urlTrailers != null) {
            //new FetchTrailersTask().execute(urlTrailers);
            queryBundle = new Bundle();
            queryBundle.putString(SEARCH_QUERY_URL_EXTRA, urlTrailers.toString());

            LoaderManager loaderManager = getSupportLoaderManager();

            Loader<String> trailerLoader = loaderManager.getLoader(MOVIE_DETAIL_LOADER);

            if (trailerLoader == null) {
                loaderManager.initLoader(MOVIE_DETAIL_LOADER, queryBundle, this);
            } else {
                loaderManager.restartLoader(MOVIE_DETAIL_LOADER, queryBundle, this);
            }

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

                URL urlTrailers = NetworkUtils.buildUrlTrailers(movie.getId(), getString(R.string.movie_db_api_key));
                tvMovieTrailersUrl.setText(urlTrailers.toString());

                if (urlTrailers != null) {
                    //new FetchTrailersTask().execute(urlTrailers);
                    queryBundle = new Bundle();
                    queryBundle.putString(SEARCH_QUERY_URL_EXTRA, urlTrailers.toString());

                    LoaderManager loaderManager = getSupportLoaderManager();

                    Loader<String> trailerLoader = loaderManager.getLoader(MOVIE_DETAIL_LOADER);

                    if (trailerLoader == null) {
                        loaderManager.initLoader(MOVIE_DETAIL_LOADER, queryBundle, MovieDetailActivity.this);
                    } else {
                        loaderManager.restartLoader(MOVIE_DETAIL_LOADER, queryBundle, MovieDetailActivity.this);
                    }

                }

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


    //Loader
    @Override
    public Loader<String> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {

            String trailersJson;

            @Override
            protected void onStartLoading() {

                if (bundle == null) {
                    return;
                }

                if (trailersJson != null) {
                    Log.d("MovieDetailActivity2", "onStartLoading: trailersJson=null");
                    deliverResult(trailersJson);
                } else {
                    Log.d("MovieDetailActivity2", "onStartLoading: forceLoad");
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                String searchQueryUrlString = bundle.getString(SEARCH_QUERY_URL_EXTRA);

                Log.d("MovieDetailActivity2", "loadInBackground: " + searchQueryUrlString);

                if (searchQueryUrlString == null || TextUtils.isEmpty(searchQueryUrlString)) {
                    return null;
                }

                String jsonTrailersResult = null;

                try {
                    jsonTrailersResult = NetworkUtils.getResponseFromHttpUrl(new URL(searchQueryUrlString));

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                return jsonTrailersResult;
            }

            @Override
            public void deliverResult(@Nullable String jsonTrailersResult) {
                Log.d("MovieDetailActivity2", "deliverResult: ");
                trailersJson = jsonTrailersResult;
                super.deliverResult(jsonTrailersResult);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String jsonTrailersResult) {

        Log.d("MovieDetailActivity2", "onLoadFinished: ");
        if (jsonTrailersResult != null && !("").equals(jsonTrailersResult)) {
            String text = tvMovieTrailersUrl.getText() + "\n\n" + jsonTrailersResult;
            tvMovieTrailersUrl.setText(text);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

}
