package com.fabiohideki.filmesfamosos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fabiohideki.filmesfamosos.adapters.TrailersAdapter;
import com.fabiohideki.filmesfamosos.model.ResultTrailers;
import com.fabiohideki.filmesfamosos.utils.NetworkUtils;
import com.fabiohideki.filmesfamosos.utils.TrailersJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class TrailersFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    private static final String SEARCH_QUERY_URL_EXTRA = "query";
    private static final int MOVIE_DETAIL_LOADER = 22;
    private Bundle queryBundle = null;
    private String movieID;

    private static ResultTrailers resultTrailers;

    RecyclerView recyclerView;
    private TrailersAdapter mAdapter;

    public TrailersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trailers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        movieID = getArguments().getString("id_movie");

        getActivity().getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER, queryBundle, this);

        //Trailers
        URL urlTrailers = NetworkUtils.buildUrlTrailers(movieID, getString(R.string.movie_db_api_key));

        //tvMovieTrailersUrl.setText(urlTrailers.toString());

        if (urlTrailers != null) {
            //new FetchTrailersTask().execute(urlTrailers);
            queryBundle = new Bundle();
            queryBundle.putString(SEARCH_QUERY_URL_EXTRA, urlTrailers.toString());

            LoaderManager loaderManager = getActivity().getSupportLoaderManager();

            Loader<String> trailerLoader = loaderManager.getLoader(MOVIE_DETAIL_LOADER);

            if (trailerLoader == null) {
                loaderManager.initLoader(MOVIE_DETAIL_LOADER, queryBundle, this);
            } else {
                loaderManager.restartLoader(MOVIE_DETAIL_LOADER, queryBundle, this);
            }

        }

    }

    //Loader
    @Override
    public Loader<String> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<String>(getActivity()) {

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

            try {
                resultTrailers = TrailersJsonUtils.getResultTrailersFromJson(getContext(), jsonTrailersResult);

                recyclerView = getActivity().findViewById(R.id.rv_trailers);
                mAdapter = new TrailersAdapter(getContext(), resultTrailers);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
                //mAdapter.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
