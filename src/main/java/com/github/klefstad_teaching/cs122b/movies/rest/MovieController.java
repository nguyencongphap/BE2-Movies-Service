package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieGetByMovieIdResponse;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.model.response.PersonGetByPersonIdResponse;
import com.github.klefstad_teaching.cs122b.movies.model.response.PersonSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.*;
import com.github.klefstad_teaching.cs122b.movies.util.Validate;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@RestController
public class MovieController
{
    private static final Logger LOG = LoggerFactory.getLogger(MovieController.class);

    private final MovieRepo repo;
    private final Validate validate;

    @Autowired
    public MovieController(MovieRepo repo, Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    /**
     * Movie Search
     */
    @GetMapping("/movie/search")
    public ResponseEntity<MovieSearchResponse> movieSearch (
            @AuthenticationPrincipal SignedJWT user,
            @RequestParam Optional<String> title,
            @RequestParam Optional<Integer> year,
            @RequestParam Optional<String> director,
            @RequestParam Optional<String> genre,
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> orderBy,
            @RequestParam Optional<String> direction
            )
    {
        JWTClaimsSet userJWTClaimsSet = getUserJWTClaimsSet(user);

        Long userID = getUserID(userJWTClaimsSet);

        List<String> userRoles = getUserRoles(userJWTClaimsSet);

        // use validate to validate request parameters orderBy, direction, limit, offset
        validate.validateLimit(limit);
        LOG.info("Passed validateLimit");
        validate.validatePage(page);
        LOG.info("Passed validatePage");
        validate.validateMovieOrderBy(orderBy);
        LOG.info("Passed validateOrderBy");
        validate.validateDirection(direction);
        LOG.info("Passed validateDirection");

        // use repo to Call functions that invoke db cals and return the data based on the userRoles
        Movie[] movies = repo.getMoviesSearchResultsByTitle(
                userRoles,
                title,
                year,
                director,
                genre,
                limit,
                page,
                orderBy,
                direction
        );

        MovieSearchResponse r = new MovieSearchResponse();

        if (movies == null) {
            return r
                    .setResult(MoviesResults.NO_MOVIES_FOUND_WITHIN_SEARCH)
                    .toResponse();
        }

        return r
                .setMovies(movies)
                .setResult(MoviesResults.MOVIES_FOUND_WITHIN_SEARCH)
                .toResponse();
    }

    /**
     * Movie Search By Person Id
     */
    @GetMapping("/movie/search/person/{personId}")
    public ResponseEntity<MovieSearchResponse> movieSearchByPersonId (
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Optional<String> personId,
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> orderBy,
            @RequestParam Optional<String> direction
    )
    {
        JWTClaimsSet userJWTClaimsSet = getUserJWTClaimsSet(user);

        Long userID = getUserID(userJWTClaimsSet);

        List<String> userRoles = getUserRoles(userJWTClaimsSet);

        // use validate to validate request parameters orderBy, direction, limit, offset
        validate.validateLimit(limit);
        LOG.info("Passed validateLimit");
        validate.validatePage(page);
        LOG.info("Passed validatePage");
        validate.validateMovieOrderBy(orderBy);
        LOG.info("Passed validateOrderBy");
        validate.validateDirection(direction);
        LOG.info("Passed validateDirection");

        // use repo to Call functions that invoke db cals and return the data based on the userRoles
        Movie[] movies = repo.getMoviesSearchResultsByPersonId(
                userRoles,
                personId,
                limit,
                page,
                orderBy,
                direction
        );

        MovieSearchResponse r = new MovieSearchResponse()
                .setMovies(movies);

        if (movies == null) {
            return r
                    .setResult(MoviesResults.NO_MOVIES_WITH_PERSON_ID_FOUND)
                    .toResponse();
        }
        return r
                .setResult(MoviesResults.MOVIES_WITH_PERSON_ID_FOUND)
                .toResponse();
    }

    /**
     * Movie Get By Movie Id
     */
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<MovieGetByMovieIdResponse> movieGetByMovieId(
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Optional<String> movieId
    )
    {
        JWTClaimsSet userJWTClaimsSet = getUserJWTClaimsSet(user);
        Long userID = getUserID(userJWTClaimsSet);
        List<String> userRoles = getUserRoles(userJWTClaimsSet);

        MovieGetByMovieIdResponse r = new MovieGetByMovieIdResponse();

        // use repo to Call functions that invoke db cals and return the data based on the userRoles
        MovieFullInfo movieFullInfo = repo.getMovieFullInfoByMovieId(
                userRoles,
                movieId
        );
        if (movieFullInfo == null) {
            return r
                    .setResult(MoviesResults.NO_MOVIE_WITH_ID_FOUND)
                    .toResponse();
        }

        r.setMovie(movieFullInfo);

        Genre[] movieGenres = repo.getGenresByMovieId(movieId);
        r.setGenres(movieGenres);

        Person[] moviePersons = repo.getPersonsByMovieId(movieId);
        r.setPersons(moviePersons);

        return r
                .setResult(MoviesResults.MOVIE_WITH_ID_FOUND)
                .toResponse();
    }

    /**
     * Person Search
     */
    @GetMapping("/person/search")
    public ResponseEntity<PersonSearchResponse> personSearch(
            @AuthenticationPrincipal SignedJWT user,
            @RequestParam Optional<String> name,
            @RequestParam Optional<String> birthday,
            @RequestParam Optional<String> movieTitle,
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> orderBy,
            @RequestParam Optional<String> direction
    )
    {
        JWTClaimsSet userJWTClaimsSet = getUserJWTClaimsSet(user);

        Long userID = getUserID(userJWTClaimsSet);

        List<String> userRoles = getUserRoles(userJWTClaimsSet);

        // use validate to validate request parameters orderBy, direction, limit, offset
        validate.validateLimit(limit);
        LOG.info("Passed validateLimit");
        validate.validatePage(page);
        LOG.info("Passed validatePage");
        validate.validatePersonOrderBy(orderBy);
        LOG.info("Passed validatePersonOrderBy");
        validate.validateDirection(direction);
        LOG.info("Passed validateDirection");

        PersonFullInfo[] personFullInfos = repo.getPersonSearchResults(
                name,
                birthday,
                movieTitle,
                limit,
                page,
                orderBy,
                direction
        );

        PersonSearchResponse r = new PersonSearchResponse();

        if (personFullInfos == null) {
            return r
                    .setResult(MoviesResults.NO_PERSONS_FOUND_WITHIN_SEARCH)
                    .toResponse();
        }

        return r
                .setPersons(personFullInfos)
                .setResult(MoviesResults.PERSONS_FOUND_WITHIN_SEARCH)
                .toResponse();
    }

    /**
     * Person Get By Person Id
     */
    @GetMapping("/person/{personId}")
    public ResponseEntity<PersonGetByPersonIdResponse> personGetByPersonId(
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Long personId
    )
    {
        JWTClaimsSet userJWTClaimsSet = getUserJWTClaimsSet(user);
        Long userID = getUserID(userJWTClaimsSet);
        List<String> userRoles = getUserRoles(userJWTClaimsSet);

        PersonGetByPersonIdResponse r = new PersonGetByPersonIdResponse();

        // use repo to Call functions that invoke db cals and return the data based on the userRoles
        PersonFullInfo personFullInfo = repo.getPersonByPersonId(
                personId
        );

        if (personFullInfo == null) {
            return r
                    .setResult(MoviesResults.NO_PERSON_WITH_ID_FOUND)
                    .toResponse();
        }

        return r
                .setPerson(personFullInfo)
                .setResult(MoviesResults.PERSON_WITH_ID_FOUND)
                .toResponse();
    }

    // Helper Functions
    private JWTClaimsSet getUserJWTClaimsSet(SignedJWT user) {
        JWTClaimsSet userJWTClaimsSet = null;
        try {
            userJWTClaimsSet = user.getJWTClaimsSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return userJWTClaimsSet;
    }

    private Long getUserID(JWTClaimsSet userJWTClaimsSet) {
        Long userID = null;
        try {
            userID = userJWTClaimsSet.getLongClaim(JWTManager.CLAIM_ID);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return userID;
    }

    private List<String> getUserRoles(JWTClaimsSet userJWTClaimsSet) {
        List<String> userRoles = null;
        try {
            userRoles = userJWTClaimsSet.getStringListClaim(JWTManager.CLAIM_ROLES);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return userRoles;
    }
}
