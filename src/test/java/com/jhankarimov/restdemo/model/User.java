package com.jhankarimov.restdemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record User(Integer id, String name, String username, String email, Address address) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Address(String street, String city, String zipcode) {
    }
}
