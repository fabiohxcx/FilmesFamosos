package com.fabiohideki.filmesfamosos.model;

import java.util.ArrayList;
import java.util.List;

public class ResultMovies {
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
            movies = new ArrayList<>();
        }
    }
}
