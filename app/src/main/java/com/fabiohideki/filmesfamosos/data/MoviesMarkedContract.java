package com.fabiohideki.filmesfamosos.data;

import android.provider.BaseColumns;

/**
 * Created by hidek on 18/12/2017.
 */

public class MoviesMarkedContract {

    public static final class MoviesMarkedEntry implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_BACK_DROP_PATH = "backdrop_path";

    }

}
