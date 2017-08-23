package com.fabiohideki.filmesfamosos;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MovieDetailActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String title = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        setTitle(title);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setBackground(getResources().getDrawable(R.drawable.placeholder));

        fab = (FloatingActionButton) findViewById(R.id.fab_mark);
        fab.setOnClickListener(new View.OnClickListener() {

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

}
