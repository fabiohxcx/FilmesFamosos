package com.fabiohideki.filmesfamosos.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by fabio.lagoa on 21/08/2017.
 */

public class NetworkUtils {
    //http://api.themoviedb.org/3/movie/popular?api_key=d40dbb8726d34c6ec5244795f0a169f9

    private static final String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/";
    private static final String VERSION = "3";
    private static final String MOVIE_POPULAR = "/movie/popular";
    private static final String MOVIE_TOP_RATED = "/movie/top_rated";

    private static final String PARAM_API_KEY = "api_key";

    public static URL buildUrl(String resourcePath, String apiKey) {
        Uri builtUri = null;
        URL url = null;

        if (!("").equals(resourcePath) && resourcePath != null) {
            builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon().
                    appendPath(VERSION)
                    .appendPath(resourcePath)
                    .appendQueryParameter(PARAM_API_KEY, apiKey)
                    .build();

            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return url;

    }

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
