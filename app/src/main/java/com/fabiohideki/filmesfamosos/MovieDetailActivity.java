package com.fabiohideki.filmesfamosos;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fabiohideki.filmesfamosos.customClass.WrapContentViewPager;
import com.fabiohideki.filmesfamosos.data.MoviesMarkedContract;
import com.fabiohideki.filmesfamosos.model.Movie;
import com.fabiohideki.filmesfamosos.utils.MovieUrlUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {


    private FloatingActionButton fabFavorite;
    private Movie movie;
    private TextView tvMovieTitle;
    private TextView tvMovieID;
    private TextView tvMovieReleaseDate;
    private TextView tvMovieOverview;
    private RatingBar rbMovie;
    private ImageView ivMoviePosterDetail;
    private ImageView ivMovieToolBarPoster;
    private boolean markedAsFavorite = false;
    private boolean markedAsFavoriteChanged = false;

    //tabs
    private TabLayout tabLayout;
    private WrapContentViewPager viewPager;

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
        fabFavorite = (FloatingActionButton) findViewById(R.id.fab_mark);


        //Check if already marked
        ContentResolver resolver = getContentResolver();
        String[] selectionArguments = new String[]{movie.getId()};
        String selection = MoviesMarkedContract.MoviesMarkedEntry.COLUMN_ID + " = ? ";

        Cursor cursor = resolver.query(MoviesMarkedContract.MoviesMarkedEntry.CONTENT_URI,
                null, selection, selectionArguments, null);


        if (cursor.getCount() >= 1) {
            //action fabFavorite
            fabFavorite.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_check));
            markedAsFavorite = true;
        }


        fabFavorite.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View view) {

                if (markedAsFavorite == false) {

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_ID, movie.getId());
                    contentValues.put(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_TITLE, movie.getTitle());
                    contentValues.put(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
                    contentValues.put(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                    contentValues.put(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_OVERVIEW, movie.getOverview());
                    contentValues.put(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                    contentValues.put(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_BACK_DROP_PATH, movie.getBackdropPath());

                    Uri uri = getContentResolver().insert(MoviesMarkedContract.MoviesMarkedEntry.CONTENT_URI, contentValues);

                    if (uri != null) {

                        Snackbar.make(view, getString(R.string.mark_favorite), Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.undo), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //action Undo
                                        fabFavorite.performClick();
                                    }
                                }).show();

                        markedAsFavorite = true;
                        fabFavorite.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_check));
                        markedAsFavoriteChanged = true;

                    }
                } else {

                    ContentResolver resolver = getContentResolver();
                    String[] selectionArguments = new String[]{movie.getId()};
                    String selection = MoviesMarkedContract.MoviesMarkedEntry.COLUMN_ID + " = ? ";

                    int numRowsDeleted = resolver.delete(MoviesMarkedContract.MoviesMarkedEntry.CONTENT_URI, selection, selectionArguments);

                    if (numRowsDeleted > 0) {

                        Snackbar.make(view, getString(R.string.unmark_favorite), Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.undo), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //action Undo
                                        fabFavorite.performClick();
                                    }
                                }).show();

                        fabFavorite.setImageDrawable(getResources().getDrawable(R.drawable.bookmark));
                        markedAsFavorite = false;
                        markedAsFavoriteChanged = true;
                    }

                }

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (WrapContentViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundleID = new Bundle();
        bundleID.putString("id_movie", movie.getId());

        TrailersFragment trailersFragment = new TrailersFragment();
        trailersFragment.setArguments(bundleID);
        adapter.addFragment(trailersFragment, "Trailers");

        ReviewsFragment reviewsFragment = new ReviewsFragment();
        reviewsFragment.setArguments(bundleID);
        adapter.addFragment(reviewsFragment, "Reviews");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", Boolean.toString(markedAsFavoriteChanged));
                setResult(Activity.RESULT_OK, returnIntent);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", Boolean.toString(markedAsFavoriteChanged));
        setResult(Activity.RESULT_OK, returnIntent);
        supportFinishAfterTransition();
    }
}
