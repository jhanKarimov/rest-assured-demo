package com.jhankarimov.restdemo.client;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.containsString;

/** Reusable request/response spec builders. */
public final class Specs {

    public static final String DEFAULT_BASE_URI = "https://jsonplaceholder.typicode.com";

    private Specs() {
    }

    public static String baseUri() {
        return System.getProperty("base.uri", DEFAULT_BASE_URI);
    }

    public static RequestSpecification request() {
        return new RequestSpecBuilder()
                .setBaseUri(baseUri())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                // some public APIs reject the default Java user agent
                .addHeader("User-Agent", "rest-assured-demo/1.0")
                .setConfig(RestAssuredConfig.config().logConfig(
                        LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
                .build();
    }

    /** 200 + JSON body. */
    public static ResponseSpecification okJson() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();
    }

    /** 201 + JSON body. */
    public static ResponseSpecification createdJson() {
        return new ResponseSpecBuilder()
                .expectStatusCode(201)
                .expectHeader("Content-Type", containsString("application/json"))
                .build();
    }
}
