package endpoints;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assumptions;
import pojo.PostsPojo;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RESTPosts extends RESTBase {
    private static RESTPosts instance = null;
    String title = FAKER.lordOfTheRings().character();
    String message = FAKER.chuckNorris().fact();
    private String currentPostId = "";
    PostsPojo deserializedPost;
    PostsPojo post = new PostsPojo(title, message);;
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    private RESTPosts() {
    }


//    String messageBody = "{\n" +
//            "    \"title\":\"" + title + "\",\n" +
//            "    \"body\":\"" + message + "\"\n" +
//            "}";

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
                .post("/users/" + RESTUsers.getInstance().currentUserId + "/posts");
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
        Response createResponse = createPost(AUTH, gson.toJson(post));
        Assumptions.assumeTrue(createResponse.getStatusCode() == 201, "Create post didn't return 201 status code");
        deserializedPost = gson.fromJson(createResponse.asString(), PostsPojo.class);
        assertAll(
                () -> assertEquals(201, createResponse.getStatusCode(), "Status codes are not the same"),
                () -> assertEquals(post.getTitle(), deserializedPost.getTitle(), "Titles are not the same"),
                () -> assertEquals(post.getBody(), deserializedPost.getBody(), "Bodies are not the same"));
    }

    public void getPostById() {
        Response getPost = getPostByIdResponse(AUTH, currentPostId);
        Assumptions.assumeTrue(getPost.getStatusCode() == 200, "Get post didn't return 200 status code");
    }

    public void verifyPostWasCreated() {
        Response getPost = getPostByIdResponse(AUTH, currentPostId);
        deserializedPost = gson.fromJson(getPost.asString(), PostsPojo.class);
        assertAll(
                () -> assertEquals(post.getTitle(), deserializedPost.getTitle(), "Titles are not the same"),
                () -> assertEquals(post.getBody(), deserializedPost.getBody(), "Bodies are not the same"),
                () -> assertTrue(deserializedPost.getId() > 0, "id key is not present in the response body or it equals 0"),
                () -> assertFalse(deserializedPost.getTitle().isEmpty(), "title key is not present in the response body"),
                () -> assertFalse(deserializedPost.getBody().isEmpty(), "body key is not present in the response body"),
                () -> assertFalse(deserializedPost.getUser_id().isEmpty(), "user_id key is not present in the response body")
        );
    }

    public void deletePostById() {
        Response deletePost = deletePostByIdResponse(AUTH, currentPostId);
        Assumptions.assumeTrue(deletePost.getStatusCode() == 204, "Delete post didn't return 204 status code");
    }

    public void updateCurrentPost() {
        title = FAKER.lordOfTheRings().character();
        message = FAKER.chuckNorris().fact();
        post.setTitle(title);
        post.setBody(message);
//        messageBody = "{\n" +
//                "    \"title\":\"" + title + "\",\n" +
//                "    \"body\":\"" + message + "\"\n" +
//                "}";
        Response updateResponse = updatePostByIdResponse(AUTH, gson.toJson(post), currentPostId);
        Assumptions.assumeTrue(updateResponse.getStatusCode() == 200, "Update post didn't return 200 status code");
    }

    public void verifyPostIsUpdated() {
        Response getPost = getPostByIdResponse(AUTH, currentPostId);
        deserializedPost = gson.fromJson(getPost.asString(), PostsPojo.class);
        assertAll(
                () -> assertEquals(post.getTitle(), deserializedPost.getTitle(), "Titles are not the same"),
                () -> assertEquals(post.getBody(), deserializedPost.getBody(), "Bodies are not the same"),
                () -> assertEquals(RESTUsers.getInstance().currentUserId, deserializedPost.getUser_id(), "user_id are not the same")
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
