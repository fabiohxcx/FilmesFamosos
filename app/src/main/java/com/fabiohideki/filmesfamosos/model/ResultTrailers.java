package com.fabiohideki.filmesfamosos.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabio.lagoa on 18/12/2017.
 */

public class ResultTrailers {

    private String id;
    private List<Trailer> trailers;

    public ResultTrailers(String id, List<Trailer> trailers) {
        this.id = id;
        this.trailers = trailers;
    }

    public ResultTrailers() {
        this.id = "";
        trailers = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {

        if (trailers != null && !trailers.isEmpty())
            this.trailers = trailers;
    }
}
