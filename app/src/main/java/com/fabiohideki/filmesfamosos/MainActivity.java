package com.fabiohideki.filmesfamosos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fabiohideki.filmesfamosos.adapters.GridRecyclerViewAdapter;
import com.fabiohideki.filmesfamosos.asynctask.FetchMoviesTask;
import com.fabiohideki.filmesfamosos.listener.HidingScrollListener;
import com.fabiohideki.filmesfamosos.model.ResultMovies;
import com.fabiohideki.filmesfamosos.utils.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements GridRecyclerViewAdapter.ItemClickListener {
    private static final String TAG = "MainActivity";

    private Toolbar mToolbar;
    private ImageButton mFabButton;

    private LinearLayout displayErrorView;
    private ProgressBar progressBar;

    private GridRecyclerViewAdapter gridRecyclerViewAdapter;

    private RecyclerView gridRecyclerView;

    private String resourcePath = NetworkUtils.TOP_RATED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        mFabButton = (ImageButton) findViewById(R.id.fabButton);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "fab", Toast.LENGTH_SHORT).show();
            }
        });

        //finds
        gridRecyclerView = (RecyclerView) findViewById(R.id.main_grid_recycler);
        displayErrorView = (LinearLayout) findViewById(R.id.error_layout);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        //ImageView imageView = (ImageView) findViewById(R.id.iv_example);
        //Picasso.with(this).load("http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg").into(imageView);

        loadMoviesData(resourcePath);

    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.app_name));
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

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Item name");
        menu.add("dsadasds");
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public void onItemClick(String title) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onItemClick: " + title);
    }
}
