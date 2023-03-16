package endpoints;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assumptions;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RESTUsers extends RESTBase {
    private static RESTUsers instance = null;
    public String currentUserId = "";

    private RESTUsers() {
    }

    String name = FAKER.name().fullName();
    String email = FAKER.internet().emailAddress();

    String body = "{\n" +
            "    \"name\": \"" + name + "\",\n" +
            "    \"email\": \"" + email + "\",\n" +
            "    \"gender\": \"male\",\n" +
            "    \"status\": \"inactive\"\n" +
            "}";

    public static RESTUsers getInstance() {
        if (instance == null) {
            instance = new RESTUsers();
        }
        return instance;
    }


    private Response createUserResponse(String AUTH, String jsonBody) {
        Response result = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + AUTH)
                .accept(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post("/users");
        currentUserId = result.jsonPath().getString("id");
        return result;
    }


    private Response getUserByIdResponse(String AUTH, String userId) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + AUTH)
                .accept(ContentType.JSON)
                .when()
                .pathParam("userId", userId)
                .get("/users/{userId}");
    }

    private Response deleteUserByIdResponse(String AUTH, String userId) {
        Response result = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + AUTH)
                .accept(ContentType.JSON)
                .when()
                .pathParam("userId", userId)
                .delete("/users/{userId}");
        return result;

    }

    private Response updateUserByIdResponse(String AUTH, String body, String userId) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + AUTH)
                .accept(ContentType.JSON)
                .body(body)
                .when()
                .pathParam("userId", userId)
                .put("/users/{userId}");
    }

    public void createNewUser() {
        Response responseCreateUser = createUserResponse(AUTH, body);
        Assumptions.assumeTrue(responseCreateUser.getStatusCode() == 201, "Create user didn't return 201 status code");
        assertAll(
                // assert 201 for create
                () -> assertEquals(201, responseCreateUser.getStatusCode(), "Status codes are not the same"),
                // jsonPath.getString() -> gets value using a key from the json response
                () -> assertEquals(name, responseCreateUser.jsonPath().getString("name"), "Names are not the same"),
                // assert that name and email are correct in post response body
                () -> assertEquals(email, responseCreateUser.jsonPath().getString("email"), "Emails are not the same"));
    }

    public void verifyUserWasCreated() {
        Response responseGetUserById = getUserByIdResponse(AUTH, currentUserId);
        assertAll(
                // assert name and email and keys in get response
                () -> assertEquals(name, responseGetUserById.jsonPath().getString("name"), "Names are not the same"),
                () -> assertEquals(email, responseGetUserById.jsonPath().getString("email"), "Emails are not the same"),
                () -> assertTrue(responseGetUserById.getBody().asString().contains("id"), "id key is not present in the response body"),
                () -> assertTrue(responseGetUserById.getBody().asString().contains("name"), "name key is not present in the response body"),
                () -> assertTrue(responseGetUserById.getBody().asString().contains("email"), "email key is not present in the response body"),
                () -> assertTrue(responseGetUserById.getBody().asString().contains("status"), "status key is not present in the response body"),
                () -> assertTrue(responseGetUserById.getBody().asString().contains("gender"), "gender key is not present in the response body")
        );
    }


    public void deleteUserById() {
        Response deleteResponse = deleteUserByIdResponse(AUTH, currentUserId);
        Assumptions.assumeTrue(deleteResponse.getStatusCode() == 204, "Delete user didn't return 204 status code");
    }

    public void getUserById() {
        Response getUserResponse = getUserByIdResponse(AUTH, currentUserId);
        Assumptions.assumeTrue(getUserResponse.getStatusCode() == 200, "Get user didn't return 200 status code");
    }

    public void updateUserById() {
        name = FAKER.name().fullName();
        email = FAKER.internet().emailAddress();
        body = "{\n" +
                "    \"name\": \"" + name + "\",\n" +
                "    \"email\": \"" + email + "\",\n" +
                "    \"gender\": \"female\",\n" +
                "    \"status\": \"active\"\n" +
                "}";
        Response updateResponse = updateUserByIdResponse(AUTH, body, currentUserId);
        Assumptions.assumeTrue(updateResponse.getStatusCode() == 200, "Update user didn't return 200 status code");
    }

    public void verifyUserIsUpdated() {
        Response getUserResponse = getUserByIdResponse(AUTH, currentUserId);
        assertAll(

                () -> assertEquals(name, getUserResponse.jsonPath().getString("name"), "Names are not the same"),
                () -> assertEquals(email, getUserResponse.jsonPath().getString("email"), "Emails are not the same"),
                () -> assertEquals("active", getUserResponse.jsonPath().getString("status"), "Statuses are not the same"),
                () -> assertEquals("female", getUserResponse.jsonPath().getString("gender"), "Names are not the same")
        );
    }

    public void verifyUserDoesNotExist() {
        Response responseGetUserByIdAfterDelete = getUserByIdResponse(AUTH, currentUserId);
        assertAll(
                () -> assertEquals(404, responseGetUserByIdAfterDelete.getStatusCode(), "Status codes are not the same"),
                () -> assertEquals("Resource not found", responseGetUserByIdAfterDelete.jsonPath().getString("message"), "Message key is not present in the response with status code 404")
        );
    }
}
