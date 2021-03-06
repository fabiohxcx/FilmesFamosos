package com.fabiohideki.filmesfamosos.model;

/**
 * Created by fabio.lagoa on 18/12/2017.
 */

public class Trailer {

    private String id;
    private String name;
    private String size;
    private String site;
    private String key;

    public Trailer(String id, String name, String size, String site, String key) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.site = site;
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
