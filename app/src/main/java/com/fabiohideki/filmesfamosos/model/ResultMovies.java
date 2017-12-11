package com.fabiohideki.filmesfamosos.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ResultMovies implements Parcelable {
    private String page;
    private String totalResults;
    private String totalPages;
    private List<Movie> movies;

    public ResultMovies() {
        this.page = "";
        this.totalResults = "";
        this.totalPages = "";
        this.movies = new ArrayList<>();
    }

    public static final Creator<ResultMovies> CREATOR = new Creator<ResultMovies>() {
        @Override
        public ResultMovies createFromParcel(Parcel in) {
            return new ResultMovies(in);
        }

        @Override
        public ResultMovies[] newArray(int size) {
            return new ResultMovies[size];
        }
    };

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {

        if (movies != null) {
            this.movies = movies;
        } else {
            this.movies = new ArrayList<>();
        }
    }

    protected ResultMovies(Parcel in) {
        page = in.readString();
        totalResults = in.readString();
        totalPages = in.readString();
        if (in.readByte() == 0x01) {
            movies = new ArrayList<Movie>();
            in.readList(movies, Movie.class.getClassLoader());
        } else {
            movies = null;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(page);
        dest.writeString(totalResults);
        dest.writeString(totalPages);
        if (movies == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(movies);
        }
    }
}
