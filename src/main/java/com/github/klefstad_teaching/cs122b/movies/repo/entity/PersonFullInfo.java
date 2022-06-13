package com.github.klefstad_teaching.cs122b.movies.repo.entity;

public class PersonFullInfo {
    private Long id;
    private String name;
    private String birthday;
    private String biography;
    private String birthplace;
    private Float popularity;
    private String profilePath;

    public Long getId() {
        return id;
    }

    public PersonFullInfo setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PersonFullInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public PersonFullInfo setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getBiography() {
        return biography;
    }

    public PersonFullInfo setBiography(String biography) {
        this.biography = biography;
        return this;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public PersonFullInfo setBirthplace(String birthplace) {
        this.birthplace = birthplace;
        return this;
    }

    public Float getPopularity() {
        return popularity;
    }

    public PersonFullInfo setPopularity(Float popularity) {
        this.popularity = popularity;
        return this;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public PersonFullInfo setProfilePath(String profilePath) {
        this.profilePath = profilePath;
        return this;
    }
}
