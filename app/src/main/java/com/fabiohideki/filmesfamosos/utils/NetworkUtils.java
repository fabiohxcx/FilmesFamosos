package com.fabiohideki.filmesfamosos.utils;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private final static String TAG = "NetworkUtils";

    private static final String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/";
    private static final String VERSION = "3";
    private static final String PAGE = "page";
    private static final String MOVIE = "movie";
    private static final String VIDEOS = "videos";
    private static final String REVIEWS = "reviews";
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";

    private static final String PARAM_API_KEY = "api_key";

    public static URL buildUrlMovie(String resourcePath, String apiKey, String page) {
        Uri builtUri;
        URL url = null;

        page = (page != null && !("").equals(page)) ? page : "1";

        if (!("").equals(resourcePath) && resourcePath != null) {

            builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon().
                    appendPath(VERSION)
                    .appendPath(MOVIE)
                    .appendPath(resourcePath)
                    .appendQueryParameter(PARAM_API_KEY, apiKey)
                    .appendQueryParameter(PAGE, page)
                    .build();

            try {
                url = new URL(builtUri.toString());

                Log.i(TAG, "buildUrl: " + url.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return url;

    }

    public static URL buildUrlTrailers(String id, String apiKey) {
        Uri builtUri;
        URL url = null;

        builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon().
                appendPath(VERSION)
                .appendPath(MOVIE)
                .appendPath(id)
                .appendPath(VIDEOS)
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        try {
            url = new URL(builtUri.toString());
            Log.i(TAG, "buildUrl: " + url.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrlReviews(String id, String apiKey) {
        Uri builtUri;
        URL url = null;

        builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon().
                appendPath(VERSION)
                .appendPath(MOVIE)
                .appendPath(id)
                .appendPath(REVIEWS)
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        try {
            url = new URL(builtUri.toString());
            Log.i(TAG, "buildUrl: " + url.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        return url;
    }

    @Nullable
    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
