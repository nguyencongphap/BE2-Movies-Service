package com.github.klefstad_teaching.cs122b.movies.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MovieRepo
{
    private static final Logger LOG = LoggerFactory.getLogger(MovieRepo.class);

    private final ObjectMapper objectMapper;
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public MovieRepo(ObjectMapper objectMapper, NamedParameterJdbcTemplate template)
    {
        this.objectMapper = objectMapper;
        this.template = template;
    }

    public Movie[] getMoviesSearchResultsByTitle(
            List<String> userRoles,
            Optional<String> title,
            Optional<Integer> year,
            Optional<String> director,
            Optional<String> genre,
            Optional<Integer> limit,
            Optional<Integer> page,
            Optional<String> orderBy,
            Optional<String> direction
    )
    {
        // Construct WHERE title statement based on given args
        String titleStatement = title
                .map(titleVal -> "movie.title LIKE " + "\'%" + titleVal + "%\'")
                .orElse("");

        // Construct WHERE year statement based on given args
        String yearStatement = year
                .map(yearVal -> "movie.year = " + Integer.toString(year.get()))
                .orElse("");

        // Construct WHERE director statement based on given args
        String directorStatement = director
                .map(directorVal -> "person.name LIKE " + "\'%" + directorVal + "%\'")
                .orElse("");

        // Construct WHERE genre statement based on given args
        String genreStatement = genre
                .map(genreVal -> "genre.name LIKE " + "\'%" + genreVal + "%\'")
                .orElse("");

        // Construct WHERE hidden statement based on given args
        String hiddenStatement = "movie.hidden = 0";
        for (String role : userRoles) {
            if (role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("Employee")) {
                hiddenStatement = "";
                break;
            }
        }

        // Construct WHERE statement by joining conditions with AND, omit WHERE if there's no condition
        String whereConditionStatement = "";
        if (
            !titleStatement.isEmpty() ||
            !yearStatement.isEmpty() ||
            !directorStatement.isEmpty() ||
            !genreStatement.isEmpty() ||
            !hiddenStatement.isEmpty()
        ) {
            whereConditionStatement =
                "WHERE " +
                Stream.of(
                    titleStatement,
                    yearStatement,
                    directorStatement,
                    genreStatement,
                    hiddenStatement)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" AND "));
        }

        // Construct LIMIT statement based on given args
        String limitStatement = constructLimitStatement(limit);

        // Construct PAGE statement based on given args
        String pageStatement = constructPageStatement(page, limit);

        // Construct ORDER BY with DIRECTION statement based on given args
        String orderByAndDirectionStatement = constructMovieOrderByAndDirectionStatement(orderBy, direction);

        String sql =
                "SELECT JSON_ARRAYAGG(JSON_OBJECT( " +
                        "'id', joinResult.id, " +
                        "'title', joinResult.title, " +
                        "'year', joinResult.year, " +
                        "'director', joinResult.name, " +
                        "'rating', joinResult.rating, " +
                        "'backdropPath', joinResult.backdrop_path, " +
                        "'posterPath', joinResult.poster_path, " +
                        "'hidden', joinResult.hidden " +
                        ")) AS jsonArrayString " +
                        "FROM " +
                        "( " +
                        "SELECT DISTINCT movie.id, movie.title, movie.year, person.name, movie.rating, movie.backdrop_path, movie.poster_path, movie.hidden " +
                        "FROM " +
                        "movies.movie as movie " +
                        "JOIN movies.movie_genre as movie_genre ON movie.id = movie_genre.movie_id " +
                        "JOIN movies.genre as genre ON genre.id = movie_genre.genre_id " +
                        "JOIN movies.person as person ON person.id = movie.director_id " +
                        whereConditionStatement + " " +
                        orderByAndDirectionStatement + " " +
                        limitStatement + " " +
                        pageStatement + " " +
                        ") AS joinResult"
                ;

        LOG.info(sql);

        MapSqlParameterSource source =
                new MapSqlParameterSource()
        ;

        String resultJSONArrayString = null;
        try {
            resultJSONArrayString = this.template.queryForObject(
                    sql,
                    source,
                    String.class
            );
        }
        catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            resultJSONArrayString = null;
        }

        Movie[] movieArray = null;
        if (resultJSONArrayString != null) {
            try {
                movieArray =
                        objectMapper.readValue(resultJSONArrayString, Movie[].class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return movieArray;
    }


    public Movie[] getMoviesSearchResultsByPersonId(
            List<String> userRoles,
            Optional<String> personId,
            Optional<Integer> limit,
            Optional<Integer> page,
            Optional<String> orderBy,
            Optional<String> direction)
    {
        // Construct WHERE title statement based on given args

        // Construct WHERE person.id statement based on given args
        String personIdStatement = personId
                .map(personIdVal -> "person.id = " + personIdVal)
                .orElse("");

        // Construct WHERE hidden statement based on given args
        String hiddenStatement = "movie.hidden = 0";
        for (String role : userRoles) {
            if (role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("Employee")) {
                hiddenStatement = "";
                break;
            }
        }
        String whereConditionStatement = "";
        if (
                !personIdStatement.isEmpty() ||
                !hiddenStatement.isEmpty()
        )
        {
            whereConditionStatement =
                "WHERE " +
                Stream.of(
                    personIdStatement,
                    hiddenStatement
                )
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" AND "));
        }

        // Construct LIMIT statement based on given args
        String limitStatement = constructLimitStatement(limit);

        // Construct PAGE statement based on given args
        String pageStatement = constructPageStatement(page, limit);

        // Construct ORDER BY with DIRECTION statement based on given args
        String orderByAndDirectionStatement = constructMovieOrderByAndDirectionStatement(orderBy, direction);

        String sql =
                "SELECT JSON_ARRAYAGG(JSON_OBJECT( \n" +
                        "'id', joinResult.id, \n" +
                        "'title', joinResult.title, \n" +
                        "'year', joinResult.year, \n" +
                        "'director', joinResult.name, \n" +
                        "'rating', joinResult.rating, \n" +
                        "'backdropPath', joinResult.backdrop_path, \n" +
                        "'posterPath', joinResult.poster_path, \n" +
                        "'hidden', joinResult.hidden )) AS jsonArrayString \n" +
                        "FROM ( \n" +
                        "SELECT DISTINCT movie.id, movie.title, movie.year, director.name, movie.rating, movie.backdrop_path, movie.poster_path, movie.hidden \n" +
                        "FROM \n" +
                        "movies.movie as movie \n" +
                        "JOIN movies.movie_person as movie_person ON movie_person.movie_id = movie.id\n" +
                        "JOIN movies.person as person ON movie_person.person_id = person.id\n" +
                        "JOIN movies.person as director ON movie.director_id = director.id\n" +
                        whereConditionStatement + " " +
                        orderByAndDirectionStatement + " " +
                        limitStatement + " " +
                        pageStatement + " " +
                        ") AS joinResult \n"
                ;

        MapSqlParameterSource source =
                new MapSqlParameterSource()
        ;

        LOG.info(sql);

        String resultJSONArrayString = null;
        try {
            resultJSONArrayString = this.template.queryForObject(
                    sql,
                    source,
                    String.class
            );
        }
        catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            resultJSONArrayString = null;
        }

        Movie[] movieArray = null;
        if (resultJSONArrayString != null) {
            try {
                movieArray =
                        objectMapper.readValue(resultJSONArrayString, Movie[].class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return movieArray;
    }

    public MovieFullInfo getMovieFullInfoByMovieId(List<String> userRoles, Optional<String> movieId) {
        // Construct WHERE person.id statement based on given args
        String movieIdStatement = movieId
                .map(movieIdVal -> "movie.id = " + movieIdVal)
                .orElse("");

        // Construct WHERE hidden statement based on given args
        String hiddenStatement = "movie.hidden = 0";
        for (String role : userRoles) {
            if (role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("Employee")) {
                hiddenStatement = "";
                break;
            }
        }
        String whereConditionStatement = "";
        if (
                !movieIdStatement.isEmpty() ||
                        !hiddenStatement.isEmpty()
        )
        {
            whereConditionStatement =
                    "WHERE " +
                            Stream.of(
                                            movieIdStatement,
                                            hiddenStatement
                                    )
                                    .filter(s -> !s.isEmpty())
                                    .collect(Collectors.joining(" AND "));
        }

        String sql =
                "SELECT DISTINCT \n" +
                        "movie.id, \n" +
                        "movie.title, \n" +
                        "movie.year, \n" +
                        "director.name, \n" +
                        "movie.rating, \n" +
                        "movie.num_votes,\n" +
                        "movie.budget,\n" +
                        "movie.revenue,\n" +
                        "movie.overview,\n" +
                        "movie.backdrop_path, \n" +
                        "movie.poster_path, \n" +
                        "movie.hidden \n" +
                        "FROM \n" +
                        "movies.movie as movie \n" +
                        "JOIN movies.person as director ON movie.director_id = director.id\n" +
                        whereConditionStatement
                ;

        MapSqlParameterSource source =
                new MapSqlParameterSource()
                ;

        LOG.info(sql);

        MovieFullInfo movieFullInfo = null;
        try {
            movieFullInfo = this.template.queryForObject(
                    sql,
                    source,
                    (rs, rowNum) ->
                            new MovieFullInfo()
                                    .setId(rs.getLong("id"))
                                    .setTitle(rs.getString("title"))
                                    .setYear(rs.getInt("year"))
                                    .setDirector(rs.getString("name"))
                                    .setRating(rs.getDouble("rating"))
                                    .setNumVotes(rs.getLong("num_votes"))
                                    .setBudget(rs.getLong("budget"))
                                    .setRevenue(rs.getLong("revenue"))
                                    .setOverview(rs.getString("overview"))
                                    .setBackdropPath(rs.getString("backdrop_path"))
                                    .setPosterPath(rs.getString("poster_path"))
                                    .setHidden(rs.getBoolean("hidden"))
            );
        }
        catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            movieFullInfo = null;
        }

        return movieFullInfo;
    }

    public Genre[] getGenresByMovieId(Optional<String> movieId) {
        String sql =
                "SELECT JSON_ARRAYAGG(JSON_OBJECT( \n" +
                "'id', joinResult.id, \n" +
                "'name', joinResult.name \n" +
                ")) AS jsonArrayString \n" +
                "FROM ( \n" +
                "SELECT DISTINCT genre.id, genre.name\n" +
                "FROM \n" +
                "movies.movie as movie \n" +
                "JOIN movies.movie_genre as movie_genre ON movie_genre.movie_id = movie.id\n" +
                "JOIN movies.genre as genre ON movie_genre.genre_id = genre.id\n" +
                "WHERE movie.id = :movieIdVal\n" +
                "ORDER BY genre.name ASC\n" +
                ") AS joinResult\n"
                ;

        String movieIdVal = movieId.orElse("");
        MapSqlParameterSource source =
                new MapSqlParameterSource()
                        .addValue("movieIdVal", movieIdVal, Types.INTEGER)
                ;

        String resultJSONArrayString = null;
        try {
            resultJSONArrayString = this.template.queryForObject(
                    sql,
                    source,
                    String.class
            );
        }
        catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            resultJSONArrayString = null;
        }

        Genre[] genreArray = null;
        if (resultJSONArrayString != null) {
            try {
                genreArray =
                        objectMapper.readValue(resultJSONArrayString, Genre[].class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return genreArray;
    }

    public Person[] getPersonsByMovieId(Optional<String> movieId)
    {
        String sql =
                "SELECT JSON_ARRAYAGG(JSON_OBJECT( \n" +
                "'id', joinResult.id, \n" +
                "'name', joinResult.name \n" +
                ")) AS jsonArrayString \n" +
                "FROM ( \n" +
                "SELECT DISTINCT person.id, person.name, person.popularity\n" +
                "FROM \n" +
                "movies.movie as movie \n" +
                "JOIN movies.movie_person as movie_person ON movie_person.movie_id = movie.id\n" +
                "JOIN movies.person as person ON movie_person.person_id = person.id\n" +
                "WHERE movie.id = :movieIdVal\n" +
                "ORDER BY person.popularity DESC, person.id ASC\n" +
                ") AS joinResult\n"
                ;

        String movieIdVal = movieId.orElse("");
        MapSqlParameterSource source =
                new MapSqlParameterSource()
                        .addValue("movieIdVal", movieIdVal, Types.INTEGER)
                ;

        LOG.info(sql);

        String resultJSONArrayString = null;
        try {
            resultJSONArrayString = this.template.queryForObject(
                    sql,
                    source,
                    String.class
            );
        }
        catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            resultJSONArrayString = null;
        }

        Person[] personArray = null;
        if (resultJSONArrayString != null) {
            try {
                personArray =
                        objectMapper.readValue(resultJSONArrayString, Person[].class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return personArray;
    }

    public PersonFullInfo[] getPersonSearchResults(
            Optional<String> name,
            Optional<String> birthday,
            Optional<String> title,
            Optional<Integer> limit,
            Optional<Integer> page,
            Optional<String> orderBy,
            Optional<String> direction
    ) {
        // Construct FROM statement, only JOIN when there's a title provided
        String fromStatement = "FROM movies.person \n";
        if (title.isPresent()) {
            fromStatement =
                    "FROM movies.movie as movie \n" +
                    "JOIN movies.movie_person as movie_person ON movie_person.movie_id = movie.id \n" +
                    "JOIN movies.person as person ON movie_person.person_id = person.id \n"
                    ;
        }

        // Construct WHERE name statement based on given args
        String nameStatement = name
                .map(nameVal -> "person.name LIKE " + "\'%" + nameVal + "%\'")
                .orElse("");

        // Construct WHERE birthday statement based on given args
        String birthdayStatement = birthday
                .map(nameVal -> "person.birthday = " + "\'" + birthday.get() + "\'")
                .orElse("");

        // Construct WHERE title statement based on given args
        String titleStatement = title
                .map(titleVal -> "movie.title LIKE " + "\'%" + titleVal + "%\'")
                .orElse("");

        // Construct WHERE statement by joining conditions with AND, omit WHERE if there's no condition
        String whereConditionStatement = "";
        if (
            !nameStatement.isEmpty() ||
            !birthdayStatement.isEmpty() ||
            !titleStatement.isEmpty()
        ) {
            whereConditionStatement =
                "WHERE " +
                Stream.of(
                    nameStatement,
                    birthdayStatement,
                    titleStatement
                )
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" AND "));
        }

        // Construct LIMIT statement based on given args
        String limitStatement = constructLimitStatement(limit);

        // Construct PAGE statement based on given args
        String pageStatement = constructPageStatement(page, limit);

        // Construct ORDER BY with DIRECTION statement based on given args
        String primaryOrderByVal = orderBy.orElse("name");
        String directionVal = direction.orElse("asc");    // set asc or desc for primary orderBy

        String orderByAndDirectionStatement =
            Stream.of(
                "ORDER BY person.",
                primaryOrderByVal,
                " ",
                directionVal,
                ", person.id "
            )
            .collect(Collectors.joining(""));

        String sql =
            "SELECT JSON_ARRAYAGG(JSON_OBJECT( \n" +
            "'id', joinResult.id,\n" +
            "'name', joinResult.name,\n" +
            "'birthday', joinResult.birthday,\n" +
            "'biography', joinResult.biography,\n" +
            "'birthplace', joinResult.birthplace,\n" +
            "'popularity', joinResult.popularity,\n" +
            "'profilePath', joinResult.profile_path\n" +
            ")) AS jsonArrayString \n" +
            "FROM ( \n" +
            "SELECT DISTINCT \n" +
            "person.id, \n" +
            "person.name, \n" +
            "person.birthday, \n" +
            "person.biography, \n" +
            "person.birthplace,\n" +
            "person.popularity,\n" +
            "person.profile_path\n" +
            fromStatement +
            whereConditionStatement + " " +
            orderByAndDirectionStatement + " " +
            limitStatement + " " +
            pageStatement + " " +
            ") AS joinResult\n"
        ;

        LOG.info(sql);

        MapSqlParameterSource source =
                new MapSqlParameterSource()
                ;

        String resultJSONArrayString = null;
        try {
            resultJSONArrayString = this.template.queryForObject(
                    sql,
                    source,
                    String.class
            );
        }
        catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            resultJSONArrayString = null;
        }

        PersonFullInfo[] personFullInfos = null;
        if (resultJSONArrayString != null) {
            try {
                personFullInfos =
                        objectMapper.readValue(resultJSONArrayString, PersonFullInfo[].class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return personFullInfos;
    }

    public PersonFullInfo getPersonByPersonId(Long personId) {
        String sql =
            "SELECT p.id, p.name, p.birthday, p.biography, p.birthplace, p.popularity, p.profile_path\n" +
            "FROM movies.person AS p\n" +
            "WHERE p.id = :personId"
        ;

        MapSqlParameterSource source =
                new MapSqlParameterSource()
                        .addValue("personId", personId, Types.INTEGER)
                ;

        PersonFullInfo personFullInfo = null;
        try {
            personFullInfo = this.template.queryForObject(
                sql,
                source,
                (rs, rowNum) ->
                    new PersonFullInfo()
                        .setId(rs.getLong("id"))
                        .setName(rs.getString("name"))
                        .setBirthday(rs.getString("birthday"))
                        .setBiography(rs.getString("biography"))
                        .setBirthplace(rs.getString("birthplace"))
                        .setPopularity(rs.getFloat("popularity"))
                        .setProfilePath(rs.getString("profile_path"))
            );
        }
        catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            personFullInfo = null;
        }

        return personFullInfo;
    }

    // Helper Functions
    private String constructLimitStatement(Optional<Integer> limit) {
        String limitStatement = "LIMIT ";
        Integer limitVal = limit.orElse(10);
        limitStatement += limitVal;
        return limitStatement;
    }

    private String constructPageStatement(Optional<Integer> page, Optional<Integer> limit) {
        String pageStatement = "OFFSET ";
        Integer pageVal = page.orElse(1);
        pageStatement += Integer.toString((pageVal-1) * limit.orElse(10));
        return pageStatement;
    }

    private String constructMovieOrderByAndDirectionStatement(
            Optional<String> orderBy,
            Optional<String> direction)
    {
        String primaryOrderByVal = orderBy.orElse("title");
        String directionVal = direction.orElse("asc");    // set asc or desc for primary orderBy

        String orderByAndDirectionStatement =
            Stream.of(
                "ORDER BY movie.",
                primaryOrderByVal,
                " ",
                directionVal,
                ", movie.id "
            )
            .collect(Collectors.joining(""));

        return orderByAndDirectionStatement;
    }



}
