package endpoints;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assumptions;
import utilities.ConfigReader;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RESTPosts extends RESTBase {
    private static RESTPosts instance = null;
    private String currentUserId = RESTUsers.getInstance().currentUserId;
    private String currentPostId = "";

    private RESTPosts() {
    }

    String title = FAKER.lordOfTheRings().character();
    String message = FAKER.chuckNorris().fact();


    String messageBody = "{\n" +
            "    \"title\":\"" + title + "\",\n" +
            "    \"body\":\"" + message + "\"\n" +
            "}";

    public static RESTPosts getInstance() {
        if (instance == null) {
            instance = new RESTPosts();
        }
        return instance;
    }


    private Response createPost(String AUTH, String body) {
        Response result = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + AUTH)
                .accept(ContentType.JSON)
                .body(body)
                .when()
                .post("/users/" + currentUserId + "/posts");
        currentPostId = result.jsonPath().getString("id");
        return result;

    }

    private Response getPostByIdResponse(String AUTH, String postId) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + AUTH)
                .accept(ContentType.JSON)
                .when()
                .pathParam("id", postId)
                .get("/posts/{id}");

    }

    private Response deletePostByIdResponse(String AUTH, String postId) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + AUTH)
                .accept(ContentType.JSON)
                .when()
                .pathParam("id", postId)
                .delete("/posts/{id}");

    }

    private Response updatePostByIdResponse(String AUTH, String body, String postId) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + AUTH)
                .accept(ContentType.JSON)
                .body(body)
                .when()
                .pathParam("id", postId)
                .put("/posts/{id}");
    }

    public void createNewPost() {
        Response createResponse = createPost(AUTH, messageBody);
        Assumptions.assumeTrue(createResponse.getStatusCode() == 201, "Create user didn't return 201 status code");

        assertAll(
                // assert 201 for create
                () -> assertEquals(201, createResponse.getStatusCode(), "Status codes are not the same"),
                // jsonPath.getString() -> gets value using a key from the json response
                () -> assertEquals(title, createResponse.jsonPath().getString("title"), "Titles are not the same"),
                // assert that name and email are correct in post response body
                () -> assertEquals(message, createResponse.jsonPath().getString("body"), "Bodies are not the same"));

    }

    public void getPostById() {
        Response getPost = getPostByIdResponse(AUTH, currentPostId);
        Assumptions.assumeTrue(getPost.getStatusCode() == 200, "Get user didn't return 200 status code");


    }

    public void verifyPostWasCreated() {
        Response getPost = getPostByIdResponse(AUTH, currentPostId);

        assertAll(
                () -> assertEquals(title, getPost.jsonPath().getString("title"), "Titles are not the same"),
                () -> assertEquals(message, getPost.jsonPath().getString("body"), "Bodies are not the same"),
                () -> assertEquals(currentUserId, getPost.jsonPath().getString("user_id"), "user_id are not the same"),
                () -> assertTrue(getPost.getBody().asString().contains("id"), "id key is not present in the response body"),
                () -> assertTrue(getPost.getBody().asString().contains("title"), "title key is not present in the response body"),
                () -> assertTrue(getPost.getBody().asString().contains("body"), "body key is not present in the response body"),
                () -> assertTrue(getPost.getBody().asString().contains("user_id"), "user_id key is not present in the response body")
        );

    }

    public void deletePostById() {

        Response deletePost = deletePostByIdResponse(AUTH, currentPostId);
        Assumptions.assumeTrue(deletePost.getStatusCode() == 204, "Delete user didn't return 204 status code");

    }

    public void updateCurrentPost() {
        title = FAKER.lordOfTheRings().character();
        message = FAKER.chuckNorris().fact();
        messageBody = "{\n" +
                "    \"title\":\"" + title + "\",\n" +
                "    \"body\":\"" + message + "\"\n" +
                "}";

        Response updateResponse = updatePostByIdResponse(AUTH, messageBody, currentPostId);
        Assumptions.assumeTrue(updateResponse.getStatusCode() == 200, "Update user didn't return 200 status code");

    }

    public void verifyPostIsUpdated() {
        Response getPost = getPostByIdResponse(AUTH, currentPostId);
        assertAll(
                () -> assertEquals(title, getPost.jsonPath().getString("title"), "Titles are not the same"),
                () -> assertEquals(message, getPost.jsonPath().getString("body"), "Bodies are not the same"),
                () -> assertEquals(currentUserId, getPost.jsonPath().getString("user_id"), "user_id are not the same")
        );
    }

    public void verifyPostDoesNotExists() {
        Response getPost = getPostByIdResponse(AUTH, currentPostId);
        assertAll(
                () -> assertEquals(404, getPost.getStatusCode(), "Status codes are not the same"),
                () -> assertEquals("Resource not found", getPost.jsonPath().getString("message"), "Message key is not present in the response with status code 404")
        );

    }
}
