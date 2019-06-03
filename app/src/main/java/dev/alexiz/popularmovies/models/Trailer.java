package dev.alexiz.popularmovies.models;

public class Trailer {

    private String id;
    private String name;
    private String key;
    private String site;
    private String type;

    public Trailer(String id, String name, String key, String site, String type) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.site = site;
        this.type = type;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getKey() {
        return this.key;
    }

    public String getSite() {
        return this.site;
    }

    public String getType() {
        return this.type;
    }

}