package com.fabiohideki.filmesfamosos;

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
import android.widget.Toast;

import com.fabiohideki.filmesfamosos.adapters.GridRecyclerViewAdapter;
import com.fabiohideki.filmesfamosos.asynctask.FetchMoviesTask;
import com.fabiohideki.filmesfamosos.data.MoviesMarkedContract;
import com.fabiohideki.filmesfamosos.listener.HidingScrollListener;
import com.fabiohideki.filmesfamosos.model.Movie;
import com.fabiohideki.filmesfamosos.model.ResultMovies;
import com.fabiohideki.filmesfamosos.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

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

    //salvar
    private String resourcePath;
    private static final String ON_SAVE_INSTANCE_STATE_MOVIES = "onSaveInstanceStateMovies";
    private static final String ON_SAVE_INSTANCE_STATE_RESOURCE = "onSaveInstanceStateResource";
    private static ResultMovies resultMovies;

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


                    class TestFetchTask extends AsyncTask<Void, Void, Cursor> {

                        @Override
                        protected Cursor doInBackground(Void... voids) {

                            ContentResolver resolver = getContentResolver();
                            Cursor cursor = resolver.query(MoviesMarkedContract.MoviesMarkedEntry.CONTENT_URI,
                                    null, null, null, null);
                            return cursor;
                        }

                        @Override
                        protected void onPostExecute(Cursor cursor) {
                            super.onPostExecute(cursor);

                            mData = cursor;

                            if (mData != null) {
                                while (mData.moveToNext()) {
                                    arrayNames.add(mData.getString(mData.getColumnIndex(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_TITLE)));
                                }
                            }

                            Toast.makeText(MainActivity.this, "names: " + arrayNames.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    new TestFetchTask().execute();


                    // MoviesMarkedDbHelper db = new MoviesMarkedDbHelper(MainActivity.this);
                    // List<Movie> list = db.getAllMoviesMarked();



                    setTitle(getString(favorite));
                    resourcePath = NetworkUtils.FAVORITES;
                    //loadMoviesData(resourcePath, null, false);
                }
            }
        });

        if (savedInstanceState != null) {
            resultMovies = savedInstanceState.getParcelable(ON_SAVE_INSTANCE_STATE_MOVIES);
            resourcePath = savedInstanceState.getString(ON_SAVE_INSTANCE_STATE_RESOURCE);

            if (resourcePath != null && resourcePath == NetworkUtils.TOP_RATED) {
                setTitle(getString(top_rated));
            } else if (resourcePath != null && resourcePath == NetworkUtils.POPULAR) {
                setTitle(getString(most_popular));
            }

            loadMoviesData(resourcePath, resultMovies, true);

        } else {
            resourcePath = NetworkUtils.TOP_RATED;
            loadMoviesData(resourcePath, null, false);
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
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns());
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

        GridRecyclerViewAdapter gridRecyclerViewAdapter = new GridRecyclerViewAdapter(this, resultMovies.getMovies());
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


    @Override
    public void onItemPosterClick(View view, Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(Intent.EXTRA_COMPONENT_NAME, movie);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, view, "poster_transition");
        startActivity(intent, options.toBundle());
    }
}
