package com.github.klefstad_teaching.cs122b.movies.util;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Validate
{
    // use validate to validate request parameters orderBy, direction, limit, page

    public void validateMovieOrderBy(Optional<String> orderBy) {
        String orderByVal = orderBy.orElse("title");
        if (!(
                orderByVal.equals("title") ||
                orderByVal.equals("rating") ||
                orderByVal.equals("year")
        )) {
            throw new ResultError(MoviesResults.INVALID_ORDER_BY);
        }
    }

    public void validatePersonOrderBy(Optional<String> orderBy) {
        String orderByVal = orderBy.orElse("name");
        if (!(
                orderByVal.equals("name") ||
                orderByVal.equals("popularity") ||
                orderByVal.equals("birthday")
        )) {
            throw new ResultError(MoviesResults.INVALID_ORDER_BY);
        }
    }

    public void validateDirection(Optional<String> direction) {
        String directionVal = direction.orElse("asc");
        if (!(
                directionVal.equals("asc") ||
                directionVal.equals("desc")
        )) {
            throw new ResultError(MoviesResults.INVALID_DIRECTION);
        }
    }

    public void validateLimit(Optional<Integer> limit) {
        Integer limitVal = limit.orElse(10);
        if (!(
                limitVal.equals(10) ||
                limitVal.equals(25) ||
                limitVal.equals(50) ||
                limitVal.equals(100)
        )) {
            throw new ResultError(MoviesResults.INVALID_LIMIT);
        }
    }

    public void validatePage(Optional<Integer> page) {
        Integer pageVal = page.orElse(1);
        if (!(
                pageVal > 0
        )) {
            throw new ResultError(MoviesResults.INVALID_PAGE);
        }
    }
}
