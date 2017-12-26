package com.fabiohideki.filmesfamosos;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fabiohideki.filmesfamosos.adapters.ReviewsAdapter;
import com.fabiohideki.filmesfamosos.model.ResultReviews;
import com.fabiohideki.filmesfamosos.utils.NetworkUtils;
import com.fabiohideki.filmesfamosos.utils.ReviewsJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class ReviewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    private static final String SEARCH_QUERY_URL_EXTRA = "query";
    private static final int REVIEW_DETAIL_LOADER = 23;
    private Bundle queryBundle = null;
    private String movieID;
    private TextView textView;

    private static ResultReviews resultReviews;

    private RecyclerView recyclerView;

    private ReviewsAdapter mAdapter;

    NestedScrollView nestedScrollView;

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

        getActivity().getSupportLoaderManager().initLoader(REVIEW_DETAIL_LOADER, queryBundle, this);


        URL urlReviews = NetworkUtils.buildUrlReviews(movieID, getString(R.string.movie_db_api_key));


        if (urlReviews != null) {
            //new FetchTrailersTask().execute(urlTrailers);
            queryBundle = new Bundle();
            queryBundle.putString(SEARCH_QUERY_URL_EXTRA, urlReviews.toString());

            LoaderManager loaderManager = getActivity().getSupportLoaderManager();

            Loader<String> trailerLoader = loaderManager.getLoader(REVIEW_DETAIL_LOADER);

            if (trailerLoader == null) {
                loaderManager.initLoader(REVIEW_DETAIL_LOADER, queryBundle, this);
            } else {
                loaderManager.restartLoader(REVIEW_DETAIL_LOADER, queryBundle, this);
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

            try {
                resultReviews = ReviewsJsonUtils.getResultReviewsFromJson(getContext(), jsonReviewsResult);
                recyclerView = getActivity().findViewById(R.id.rv_reviews);
                mAdapter = new ReviewsAdapter(getContext(), resultReviews);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));


                nestedScrollView = getActivity().findViewById(R.id.nsv_movie_detail);
                nestedScrollView.scrollTo(0, 0);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error: ReviewsJsonUtils.getResultReviewsFromJson", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }


}
