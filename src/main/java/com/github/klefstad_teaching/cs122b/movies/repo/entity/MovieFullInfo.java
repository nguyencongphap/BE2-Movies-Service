package com.github.klefstad_teaching.cs122b.movies.repo.entity;

public class MovieFullInfo {
    private Long id;
    private String title;
    private Integer year;
    private String director;
    private Double rating;
    private Long numVotes;
    private Long budget;
    private Long revenue;
    private String overview;
    private String backdropPath;
    private String posterPath;
    private Boolean hidden;

    public Long getId() {
        return id;
    }

    public MovieFullInfo setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MovieFullInfo setTitle(String title) {
        this.title = title;
        return this;
    }

    public Integer getYear() {
        return year;
    }

    public MovieFullInfo setYear(Integer year) {
        this.year = year;
        return this;
    }

    public String getDirector() {
        return director;
    }

    public MovieFullInfo setDirector(String director) {
        this.director = director;
        return this;
    }

    public Double getRating() {
        return rating;
    }

    public MovieFullInfo setRating(Double rating) {
        this.rating = rating;
        return this;
    }

    public Long getNumVotes() {
        return numVotes;
    }

    public MovieFullInfo setNumVotes(Long numVotes) {
        this.numVotes = numVotes;
        return this;
    }

    public Long getBudget() {
        return budget;
    }

    public MovieFullInfo setBudget(Long budget) {
        this.budget = budget;
        return this;
    }

    public Long getRevenue() {
        return revenue;
    }

    public MovieFullInfo setRevenue(Long revenue) {
        this.revenue = revenue;
        return this;
    }

    public String getOverview() {
        return overview;
    }

    public MovieFullInfo setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public MovieFullInfo setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
        return this;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public MovieFullInfo setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public MovieFullInfo setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }
}
