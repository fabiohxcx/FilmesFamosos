package com.fabiohideki.filmesfamosos.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fabiohideki.filmesfamosos.model.Movie;

import java.util.ArrayList;
import java.util.List;

import static com.fabiohideki.filmesfamosos.data.MoviesMarkedContract.MoviesMarkedEntry.TABLE_NAME;

/**
 * Created by hidek on 18/12/2017.
 */

public class MoviesMarkedDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies_marked.db";

    private static final int DATABASE_VERSION = 1;

    public MoviesMarkedDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIES_MARKED_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        MoviesMarkedContract.MoviesMarkedEntry.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        MoviesMarkedContract.MoviesMarkedEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        MoviesMarkedContract.MoviesMarkedEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                        MoviesMarkedContract.MoviesMarkedEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        MoviesMarkedContract.MoviesMarkedEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                        MoviesMarkedContract.MoviesMarkedEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                        MoviesMarkedContract.MoviesMarkedEntry.COLUMN_BACK_DROP_PATH + " TEXT NOT NULL, " +
                        " UNIQUE (" + MoviesMarkedContract.MoviesMarkedEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_MARKED_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long addMarkedMovie(Movie movie) {

        ContentValues cv = new ContentValues();
        cv.put(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_ID, movie.getId());
        cv.put(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_TITLE, movie.getTitle());
        cv.put(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        cv.put(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        cv.put(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_OVERVIEW, movie.getOverview());
        cv.put(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        cv.put(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_BACK_DROP_PATH, movie.getBackdropPath());

        SQLiteDatabase database = getWritableDatabase();

        return database.insert(MoviesMarkedContract.MoviesMarkedEntry.TABLE_NAME, null, cv);
    }

    public boolean removeMovieMarked(int id) {
        SQLiteDatabase database = getWritableDatabase();

        return database.delete(MoviesMarkedContract.MoviesMarkedEntry.TABLE_NAME, MoviesMarkedContract.MoviesMarkedEntry.COLUMN_ID + "=" + id, null) > 0;
    }

    public List<Movie> getAllMoviesMarked() {
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.query(
                MoviesMarkedContract.MoviesMarkedEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        List<Movie> movies = new ArrayList<>();

        while (cursor.moveToNext()) {
            Movie movie = new Movie(
                    Integer.toString(cursor.getInt(cursor.getColumnIndex(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_ID))),
                    cursor.getString(cursor.getColumnIndex(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_POSTER_PATH)),
                    cursor.getString(cursor.getColumnIndex(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_OVERVIEW)),
                    cursor.getString(cursor.getColumnIndex(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_VOTE_AVERAGE)),
                    cursor.getString(cursor.getColumnIndex(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_RELEASE_DATE)),
                    cursor.getString(cursor.getColumnIndex(MoviesMarkedContract.MoviesMarkedEntry.COLUMN_BACK_DROP_PATH))
            );

            movies.add(movie);
        }

        return movies;
    }

}
