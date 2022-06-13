package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.Movie;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieSearchResponse extends ResponseModel<MovieSearchResponse> {
    private Movie[] movies;

    public Movie[] getMovies() {
        return movies;
    }

    public MovieSearchResponse setMovies(Movie[] movies) {
        this.movies = movies;
        return this;
    }
}
