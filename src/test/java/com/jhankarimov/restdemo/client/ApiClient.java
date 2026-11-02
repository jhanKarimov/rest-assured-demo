package com.jhankarimov.restdemo.client;

import com.jhankarimov.restdemo.model.Post;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

/** Thin wrapper over the API so tests read like intent, not plumbing. */
public class ApiClient {

    private final RequestSpecification spec;

    public ApiClient() {
        this(Specs.request());
    }

    public ApiClient(RequestSpecification spec) {
        this.spec = spec;
    }

    public Response getPosts() {
        return given().spec(spec).get("/posts");
    }

    public Response getPosts(Map<String, ?> query) {
        return given().spec(spec).queryParams(query).get("/posts");
    }

    public Response getPost(int id) {
        return given().spec(spec).get("/posts/{id}", id);
    }

    public Response getPostComments(int postId) {
        return given().spec(spec).get("/posts/{id}/comments", postId);
    }

    public Response createPost(Post post) {
        return given().spec(spec).body(post).post("/posts");
    }

    public Response replacePost(int id, Post post) {
        return given().spec(spec).body(post).put("/posts/{id}", id);
    }

    public Response patchPost(int id, Map<String, ?> fields) {
        return given().spec(spec).body(fields).patch("/posts/{id}", id);
    }

    public Response deletePost(int id) {
        return given().spec(spec).delete("/posts/{id}", id);
    }

    public Response getUsers() {
        return given().spec(spec).get("/users");
    }

    public Response getUser(int id) {
        return given().spec(spec).get("/users/{id}", id);
    }

    public Response get(String path) {
        return given().spec(spec).get(path);
    }

    public Response postRaw(String path, String body) {
        return given().spec(spec).body(body).post(path);
    }
}
