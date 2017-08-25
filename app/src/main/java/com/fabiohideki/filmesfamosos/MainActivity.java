package com.fabiohideki.filmesfamosos;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fabiohideki.filmesfamosos.adapters.GridRecyclerViewAdapter;
import com.fabiohideki.filmesfamosos.asynctask.FetchMoviesTask;
import com.fabiohideki.filmesfamosos.listener.HidingScrollListener;
import com.fabiohideki.filmesfamosos.model.Movie;
import com.fabiohideki.filmesfamosos.model.ResultMovies;
import com.fabiohideki.filmesfamosos.utils.NetworkUtils;

import java.net.URL;

import io.github.kobakei.materialfabspeeddial.FabSpeedDial;

import static com.fabiohideki.filmesfamosos.R.string.app_name;
import static com.fabiohideki.filmesfamosos.R.string.most_popular;
import static com.fabiohideki.filmesfamosos.R.string.top_rated;

public class MainActivity extends AppCompatActivity implements GridRecyclerViewAdapter.ItemClickListener {
    private static final String TAG = "MainActivity";

    private Toolbar mToolbar;
    private FabSpeedDial mFabButton;

    private LinearLayout displayErrorView;
    private ProgressBar progressBar;

    private GridRecyclerViewAdapter gridRecyclerViewAdapter;

    private RecyclerView gridRecyclerView;

    private String resourcePath = NetworkUtils.TOP_RATED;

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(getString(app_name) + " - " + title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();

        mFabButton = (FabSpeedDial) findViewById(R.id.fabButton);

        mFabButton.addOnMenuItemClickListener(new FabSpeedDial.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(FloatingActionButton fab, TextView textView, int itemId) {
                if (itemId == R.id.top_rated) {
                    setTitle(getString(top_rated));
                    resourcePath = NetworkUtils.TOP_RATED;
                    loadMoviesData(resourcePath);

                } else if (itemId == R.id.most_popular) {
                    setTitle(getString(most_popular));
                    resourcePath = NetworkUtils.POPULAR;
                    loadMoviesData(resourcePath);
                }
            }
        });

        mFabButton.addOnStateChangeListener(new FabSpeedDial.OnStateChangeListener() {
            @Override
            public void onStateChange(boolean open) {
                if (open) {

                }
            }
        });

        //finds
        gridRecyclerView = (RecyclerView) findViewById(R.id.main_grid_recycler);
        displayErrorView = (LinearLayout) findViewById(R.id.error_layout);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        loadMoviesData(resourcePath);

    }


    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(getString(top_rated));
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }

    private void loadMoviesData(String resourcePath) {
        showMoviesData();

        URL urlTheMovieDB = NetworkUtils.buildUrlMovie(resourcePath, getString(R.string.movie_db_api_key), null);
        if (urlTheMovieDB != null) {
            new FetchMoviesTask(this).execute(urlTheMovieDB);
        }
    }

    public void retryFetch(View view) {
        loadMoviesData(resourcePath);
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

    public void setGridViewAdapter(ResultMovies resultMovies) {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
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

        gridRecyclerViewAdapter = new GridRecyclerViewAdapter(this, resultMovies.getMovies());
        gridRecyclerViewAdapter.setClickListener(this);
        gridRecyclerView.setAdapter(gridRecyclerViewAdapter);
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


    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public void onItemPosterClick(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(Intent.EXTRA_COMPONENT_NAME, movie);
        startActivity(intent);
    }
}
