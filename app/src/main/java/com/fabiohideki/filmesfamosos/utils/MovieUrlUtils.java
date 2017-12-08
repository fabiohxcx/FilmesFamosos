package com.fabiohideki.filmesfamosos.utils;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by fabio.lagoa on 23/08/2017.
 */

public class MovieUrlUtils {
    public static final String URL_BASE = "http://image.tmdb.org/t/p/";
    public static final String IMG_SIZE_W92 = "w92";
    public static final String IMG_SIZE_W154 = "w154";
    public static final String IMG_SIZE_W185 = "w185";
    public static final String IMG_SIZE_W342 = "w342";
    public static final String IMG_SIZE_W500 = "w500";
    public static final String IMG_SIZE_W780 = "w780";
    public static final String IMG_SIZE_ORIGINAL = "original";

    public static String buildUrlPoster(String posterPath) {
        Log.i("url", "buildUrlPoster: posterPath" + posterPath);
        Uri builtUri = Uri.parse(URL_BASE).buildUpon().
                appendPath(IMG_SIZE_W500)
                .build();
        URL url;

        try {

            url = new URL(builtUri.toString());
            Log.i("url", "buildUrlPoster: " + url.toString());

            return url.toString() + posterPath;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return "";
    }

}
