package com.jhankarimov.restdemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Comment(Integer postId, Integer id, String name, String email, String body) {
}
