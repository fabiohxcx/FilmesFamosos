package com.fabiohideki.filmesfamosos;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fabiohideki.filmesfamosos.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class ReviewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    private static final String SEARCH_QUERY_URL_EXTRA = "query";
    private static final int MOVIE_DETAIL_LOADER = 23;
    private Bundle queryBundle = null;
    private String movieID;
    private TextView textView;


    public ReviewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        movieID = getArguments().getString("id_movie");
        textView = getView().findViewById(R.id.text_review);

        textView.append(" id:" + movieID);


        getActivity().getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER, queryBundle, this);

        URL urlReviews = NetworkUtils.buildUrlReviews(movieID, getString(R.string.movie_db_api_key));


        if (urlReviews != null) {
            //new FetchTrailersTask().execute(urlTrailers);
            queryBundle = new Bundle();
            queryBundle.putString(SEARCH_QUERY_URL_EXTRA, urlReviews.toString());

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

            String reviewsJson;

            @Override
            protected void onStartLoading() {

                if (bundle == null) {
                    return;
                }

                if (reviewsJson != null) {
                    Log.d("MovieDetailActivity2", "onStartLoading: trailersJson=null");
                    deliverResult(reviewsJson);
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

                String jsonReviewsResult = null;

                try {
                    jsonReviewsResult = NetworkUtils.getResponseFromHttpUrl(new URL(searchQueryUrlString));

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                return jsonReviewsResult;
            }

            @Override
            public void deliverResult(@Nullable String jsonReviewsResult) {
                Log.d("MovieDetailActivity2", "deliverResult: ");
                reviewsJson = jsonReviewsResult;
                super.deliverResult(jsonReviewsResult);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String jsonReviewsResult) {

        Log.d("MovieDetailActivity2", "onLoadFinished: ");
        if (jsonReviewsResult != null && !("").equals(jsonReviewsResult)) {
            textView.append("\n" + jsonReviewsResult);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }


}
