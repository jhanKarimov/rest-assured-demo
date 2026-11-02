package com.jhankarimov.restdemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Post(Integer userId, Integer id, String title, String body) {

    public static Post draft(int userId, String title, String body) {
        return new Post(userId, null, title, body);
    }
}
