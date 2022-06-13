package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.PersonFullInfo;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonSearchResponse extends ResponseModel<PersonSearchResponse> {
    PersonFullInfo[] persons;

    public PersonFullInfo[] getPersons() {
        return persons;
    }

    public PersonSearchResponse setPersons(PersonFullInfo[] persons) {
        this.persons = persons;
        return this;
    }
}
