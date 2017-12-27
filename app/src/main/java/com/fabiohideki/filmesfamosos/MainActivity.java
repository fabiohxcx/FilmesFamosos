package com.fabiohideki.filmesfamosos;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fabiohideki.filmesfamosos.adapters.GridRecyclerViewAdapter;
import com.fabiohideki.filmesfamosos.asynctask.FetchMoviesTask;
import com.fabiohideki.filmesfamosos.data.MoviesMarkedContract;
import com.fabiohideki.filmesfamosos.listener.HidingScrollListener;
import com.fabiohideki.filmesfamosos.model.Movie;
import com.fabiohideki.filmesfamosos.model.ResultMovies;
import com.fabiohideki.filmesfamosos.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.github.kobakei.materialfabspeeddial.FabSpeedDial;

import static com.fabiohideki.filmesfamosos.R.string.app_name;
import static com.fabiohideki.filmesfamosos.R.string.favorite;
import static com.fabiohideki.filmesfamosos.R.string.most_popular;
import static com.fabiohideki.filmesfamosos.R.string.top_rated;

public class MainActivity extends AppCompatActivity implements GridRecyclerViewAdapter.ItemClickListener {

    private Toolbar mToolbar;
    private FabSpeedDial mFabButton;
    private LinearLayout displayErrorView;
    private ProgressBar progressBar;
    private RecyclerView gridRecyclerView;
    private GridRecyclerViewAdapter gridRecyclerViewAdapter;
    private GridLayoutManager layoutManager;

    //save
    private String resourcePath;
    private static final String ON_SAVE_INSTANCE_STATE_MOVIES = "onSaveInstanceStateMovies";
    private static final String ON_SAVE_INSTANCE_STATE_RESOURCE = "onSaveInstanceStateResource";
    private static ResultMovies resultMovies;
    public static final int NOTIFY_CHANGE = 100;

    private Cursor mData;
    private ArrayList<String> arrayNames = new ArrayList<>();


    public static void setResultMovies(ResultMovies resultMoviesParam) {
        resultMovies = resultMoviesParam;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (resourcePath != null) {
            outState.putString(ON_SAVE_INSTANCE_STATE_RESOURCE, resourcePath);
        }

        if (resultMovies != null) {
            outState.putParcelable(ON_SAVE_INSTANCE_STATE_MOVIES, resultMovies);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();

        //finds
        gridRecyclerView = (RecyclerView) findViewById(R.id.main_grid_recycler);
        displayErrorView = (LinearLayout) findViewById(R.id.error_layout);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mFabButton = (FabSpeedDial) findViewById(R.id.fabButton);


        layoutManager = new GridLayoutManager(this, numberOfColumns());
        gridRecyclerView.setLayoutManager(layoutManager);
        gridRecyclerView.setHasFixedSize(true);
        gridRecyclerView.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });

        mFabButton.addOnMenuItemClickListener(new FabSpeedDial.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(FloatingActionButton fab, TextView textView, int itemId) {
                if (itemId == R.id.top_rated) {
                    setTitle(getString(top_rated));
                    resourcePath = NetworkUtils.TOP_RATED;
                    loadMoviesData(resourcePath, null, false);

                } else if (itemId == R.id.most_popular) {
                    setTitle(getString(most_popular));
                    resourcePath = NetworkUtils.POPULAR;
                    loadMoviesData(resourcePath, null, false);

                } else if (itemId == R.id.fab_favorites) {
                    setTitle(getString(favorite));
                    resourcePath = NetworkUtils.FAVORITES;

                    new MarkedMoviesFetchTask().execute();
                }
            }
        });

        if (savedInstanceState != null) {
            resultMovies = savedInstanceState.getParcelable(ON_SAVE_INSTANCE_STATE_MOVIES);
            resourcePath = savedInstanceState.getString(ON_SAVE_INSTANCE_STATE_RESOURCE);

            if (NetworkUtils.TOP_RATED.equals(resourcePath)) {
                setTitle(getString(top_rated));

            } else if (NetworkUtils.POPULAR.equals(resourcePath)) {
                setTitle(getString(most_popular));

            } else if (NetworkUtils.FAVORITES.equals(resourcePath)) {
                setTitle(getString(favorite));

            }
            loadMoviesData(resourcePath, resultMovies, true);

        } else {
            resourcePath = NetworkUtils.TOP_RATED;
            loadMoviesData(resourcePath, null, false);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /*
    * Fetch Movies Marked
    * */

    private class MarkedMoviesFetchTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {

            ContentResolver resolver = getContentResolver();

            Cursor cursor = resolver.query(
                    MoviesMarkedContract.MoviesMarkedEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);

            mData = cursor;

            resultMovies = new ResultMovies();
            List<Movie> movies = new ArrayList<>();

            if (mData != null) {
                while (mData.moveToNext()) {
                    Movie movie = new Movie(
                            mData.getString(mData.getColumnIndex(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_ID)),
                            mData.getString(mData.getColumnIndex(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_POSTER_PATH)),
                            mData.getString(mData.getColumnIndex(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_TITLE)),
                            mData.getString(mData.getColumnIndex(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_OVERVIEW)),
                            mData.getString(mData.getColumnIndex(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_VOTE_AVERAGE)),
                            mData.getString(mData.getColumnIndex(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_RELEASE_DATE)),
                            mData.getString(mData.getColumnIndex(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_BACK_DROP_PATH))
                    );

                    movies.add(movie);
                }
            }

            resultMovies.setMovies(movies);

            loadMoviesData(resourcePath, resultMovies, false);
        }
    }


    @SuppressWarnings("deprecation")
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(getString(top_rated));
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(getString(app_name) + " - " + title);
    }

    private void loadMoviesData(String resourcePath, ResultMovies resultMovies, boolean savedInstance) {
        showMoviesData();

        URL urlTheMovieDB = NetworkUtils.buildUrlMovie(resourcePath, getString(R.string.movie_db_api_key), null);
        if (urlTheMovieDB != null) {
            new FetchMoviesTask(this, resultMovies, savedInstance).execute(urlTheMovieDB);
        }
    }

    public void retryFetch(View view) {
        loadMoviesData(resourcePath, null, false);
    }

    public void showMoviesData() {
        displayErrorView.setVisibility(View.INVISIBLE);
        gridRecyclerView.setVisibility(View.VISIBLE);
    }

    public void showProgressBar(boolean visible) {
        progressBar.setVisibility((visible) ? View.VISIBLE : View.INVISIBLE);
    }

    public void showErrorMessage() {
        displayErrorView.setVisibility(View.VISIBLE);
        gridRecyclerView.setVisibility(View.INVISIBLE);
    }

    @SuppressWarnings("deprecation")
    public void setGridViewAdapter(ResultMovies resultMovies) {
        gridRecyclerViewAdapter = new GridRecyclerViewAdapter(this, resultMovies.getMovies());
        gridRecyclerViewAdapter.setClickListener(this);
        gridRecyclerView.setAdapter(gridRecyclerViewAdapter);
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    private void hideViews() {
        mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabButton.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        mFabButton.animate().translationY(mFabButton.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        mFabButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onItemPosterClick(View view, Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(Intent.EXTRA_COMPONENT_NAME, movie);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "poster_transition");
        startActivityForResult(intent, NOTIFY_CHANGE, options.toBundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NOTIFY_CHANGE) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                if ("true".equals(result)) {
                    if (NetworkUtils.FAVORITES.equals(resourcePath)) {
                        new MarkedMoviesFetchTask().execute();
                        showViews();
                    }

                }
            }
        }
    }
}
