package com.fabiohideki.filmesfamosos.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hidek on 19/12/2017.
 */

public class ResultReviews {
    private String id;
    private int page;
    private List<Review> reviews;

    public ResultReviews() {
        id = "";
        page = 0;
        reviews = new ArrayList<>();
    }

    public ResultReviews(String id, int page, List<Review> reviews) {
        this.id = id;
        this.page = page;
        this.reviews = reviews;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
