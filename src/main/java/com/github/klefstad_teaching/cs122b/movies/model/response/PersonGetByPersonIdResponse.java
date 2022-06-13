package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.Person;
import com.github.klefstad_teaching.cs122b.movies.repo.entity.PersonFullInfo;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonGetByPersonIdResponse extends ResponseModel<PersonGetByPersonIdResponse> {
    private PersonFullInfo person;

    public PersonFullInfo getPerson() {
        return person;
    }

    public PersonGetByPersonIdResponse setPerson(PersonFullInfo person) {
        this.person = person;
        return this;
    }
}
