package com.jhankarimov.restdemo.tests;

import com.jhankarimov.restdemo.client.Specs;
import com.jhankarimov.restdemo.model.Comment;
import com.jhankarimov.restdemo.model.Post;
import com.jhankarimov.restdemo.support.TestData;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.Matchers.*;

public class PostsTest extends BaseApiTest {

    // ---------- smoke ----------

    @Test(groups = "smoke", description = "GET /posts/1 returns the expected fields")
    public void getPostById_hasExpectedShape() {
        api.getPost(1).then()
                .spec(Specs.okJson())
                .body("id", equalTo(1))
                .body("userId", equalTo(1))
                .body("title", not(emptyOrNullString()))
                .body("body", not(emptyOrNullString()));
    }

    @Test(groups = "smoke", description = "Response deserializes into the Post POJO")
    public void getPostById_deserializesToPojo() {
        Post post = api.getPost(5).then().spec(Specs.okJson()).extract().as(Post.class);

        assertThat(post.id()).isEqualTo(5);
        assertThat(post.userId()).isEqualTo(1);
        assertThat(post.title()).isNotBlank();
    }

    @Test(groups = "smoke", description = "GET /posts returns the full list of 100")
    public void getPosts_returnsHundred() {
        api.getPosts().then()
                .spec(Specs.okJson())
                .body("size()", equalTo(100))
                .body("id", hasItems(1, 50, 100));
    }

    @Test(groups = "smoke", description = "Content-Type and cache headers are present")
    public void getPost_responseHeaders() {
        Response r = api.getPost(1);

        assertThat(r.statusCode()).isEqualTo(200);
        assertThat(r.header("Content-Type")).containsIgnoringCase("application/json");
        assertThat(r.header("Cache-Control")).isNotBlank();
    }

    // ---------- regression ----------

    @Test(groups = "regression", description = "Filtering by userId only returns that user's posts")
    public void filterPostsByUserId() {
        api.getPosts(Map.of("userId", 3)).then()
                .spec(Specs.okJson())
                .body("size()", equalTo(10))
                .body("userId", everyItem(equalTo(3)));
    }

    @Test(groups = "regression", description = "_page/_limit paging returns the right slice and total header")
    public void pagination_returnsRequestedSlice() {
        Response r = api.getPosts(Map.of("_page", 2, "_limit", 10));

        r.then().spec(Specs.okJson())
                .body("size()", equalTo(10))
                .body("[0].id", equalTo(11))
                .body("[9].id", equalTo(20));
        assertThat(r.header("x-total-count")).isEqualTo("100");
    }

    @Test(groups = "regression", description = "Nested comments resource belongs to the post and has valid emails")
    public void postComments_belongToPost() {
        List<Comment> comments = api.getPostComments(1).then()
                .spec(Specs.okJson())
                .extract().jsonPath().getList(".", Comment.class);

        assertThat(comments).hasSize(5);
        assertThat(comments).allSatisfy(c -> {
            assertThat(c.postId()).isEqualTo(1);
            assertThat(c.email()).contains("@");
        });
    }

    @Test(groups = "regression", dataProvider = "newPosts", dataProviderClass = TestData.class,
            description = "POST /posts echoes the payload and returns 201 with a new id")
    public void createPost_echoesPayload(int userId, String title, String body) {
        Post sent = Post.draft(userId, title, body);

        Post created = api.createPost(sent).then()
                .spec(Specs.createdJson())
                .extract().as(Post.class);

        assertThat(created.id()).isEqualTo(101); // mock API always hands out 101
        assertThat(created.userId()).isEqualTo(userId);
        assertThat(created.title()).isEqualTo(title);
        assertThat(created.body()).isEqualTo(body);
    }

    @Test(groups = "regression", description = "PUT replaces the resource and returns it")
    public void replacePost_returnsUpdated() {
        api.replacePost(1, new Post(1, 1, "new title", "new body")).then()
                .spec(Specs.okJson())
                .body("id", equalTo(1))
                .body("title", equalTo("new title"))
                .body("body", equalTo("new body"));
    }

    @Test(groups = "regression", description = "PATCH changes only the supplied field")
    public void patchPost_changesOnlyGivenField() {
        api.patchPost(1, Map.of("title", "patched")).then()
                .spec(Specs.okJson())
                .body("title", equalTo("patched"))
                .body("userId", equalTo(1))
                .body("body", not(emptyOrNullString()));
    }

    @Test(groups = "regression", description = "DELETE returns 200 with an empty object")
    public void deletePost_returnsEmptyObject() {
        api.deletePost(1).then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }

    // ---------- negative ----------

    @Test(groups = {"regression", "negative"}, dataProvider = "missingPostIds",
            dataProviderClass = TestData.class, description = "Unknown post ids return 404")
    public void getMissingPost_returns404(int id) {
        api.getPost(id).then()
                .statusCode(404)
                .body("size()", equalTo(0));
    }

    @Test(groups = {"regression", "negative"}, description = "Unknown resource path returns 404")
    public void unknownResource_returns404() {
        api.get("/does-not-exist").then().statusCode(404);
    }

    @Test(groups = {"regression", "negative"}, description = "POST to a single-item URL is not routable")
    public void postToItemUrl_isRejected() {
        api.postRaw("/posts/1", "{\"title\":\"nope\"}").then()
                .statusCode(anyOf(equalTo(404), equalTo(405)));
    }

    // ---------- contract + performance ----------

    @Test(groups = {"smoke", "contract"}, description = "A single post matches post.json schema")
    public void post_matchesSchema() {
        api.getPost(1).then()
                .spec(Specs.okJson())
                .body(matchesJsonSchemaInClasspath("schemas/post.json"));
    }

    @Test(groups = {"regression", "contract"}, description = "The posts list matches the array schema")
    public void postList_matchesSchema() {
        api.getPosts().then()
                .spec(Specs.okJson())
                .body(matchesJsonSchemaInClasspath("schemas/post-list.json"));
    }

    @Test(groups = {"regression", "contract"}, dataProvider = "userIds",
            dataProviderClass = TestData.class, description = "Users match schema and carry a username")
    public void user_matchesSchema(int id) {
        api.getUser(id).then()
                .spec(Specs.okJson())
                .body(matchesJsonSchemaInClasspath("schemas/user.json"))
                .body("id", equalTo(id))
                .body("username", not(emptyOrNullString()));
    }

    @Test(groups = "regression", description = "Latency: soft warning at 1.5s, hard fail only at 8s")
    public void responseTime_withinBudget() {
        long ms = api.getPost(1).then().statusCode(200).extract().time();

        if (ms > SOFT_BUDGET_MS) {
            System.out.printf("WARN: GET /posts/1 took %d ms (soft budget %d ms)%n", ms, SOFT_BUDGET_MS);
        }
        assertThatCode(() -> assertThat(ms).isLessThan(HARD_BUDGET_MS)).doesNotThrowAnyException();
    }
}
