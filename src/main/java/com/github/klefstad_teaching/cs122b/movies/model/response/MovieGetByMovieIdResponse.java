package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.Genre;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.MovieFullInfo;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.Person;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieGetByMovieIdResponse extends ResponseModel<MovieGetByMovieIdResponse> {
    private MovieFullInfo movie;
    private Genre[] genres;
    private Person[] persons;

    public MovieFullInfo getMovie() {
        return movie;
    }

    public MovieGetByMovieIdResponse setMovie(MovieFullInfo movie) {
        this.movie = movie;
        return this;
    }

    public Genre[] getGenres() {
        return genres;
    }

    public MovieGetByMovieIdResponse setGenres(Genre[] genres) {
        this.genres = genres;
        return this;
    }

    public Person[] getPersons() {
        return persons;
    }

    public MovieGetByMovieIdResponse setPersons(Person[] persons) {
        this.persons = persons;
        return this;
    }
}
