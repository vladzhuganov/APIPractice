package endpoints;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assumptions;
import pojo.UsersPojo;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RESTUsers extends RESTBase {
    private static RESTUsers instance = null;
    public String currentUserId = "";

    private RESTUsers() {
    }

    String name = FAKER.name().fullName();
    String email = FAKER.internet().emailAddress();
    UsersPojo deserializedUser;
    UsersPojo user = new UsersPojo(name, email, "male", "inactive");
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

//    String body = "{\n" +
//            "    \"name\": \"" + name + "\",\n" +
//            "    \"email\": \"" + email + "\",\n" +
//            "    \"gender\": \"male\",\n" +
//            "    \"status\": \"inactive\"\n" +
//            "}";

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
        Response responseCreateUser = createUserResponse(AUTH, gson.toJson(user));
        deserializedUser = gson.fromJson(responseCreateUser.asString(), UsersPojo.class);
        Assumptions.assumeTrue(responseCreateUser.getStatusCode() == 201, "Create user didn't return 201 status code");
        assertAll(
                () -> assertEquals(201, responseCreateUser.getStatusCode(), "Status codes are not the same"),
                () -> assertEquals(user.getName(), deserializedUser.getName(), "Names are not the same"),
                () -> assertEquals(user.getEmail(), deserializedUser.getEmail(), "Emails are not the same"));
    }


    public void verifyUserWasCreated() {
        Response responseGetUserById = getUserByIdResponse(AUTH, currentUserId);
        deserializedUser = gson.fromJson(responseGetUserById.asString(), UsersPojo.class);
        assertAll(

                () -> assertEquals(user.getName(), deserializedUser.getName(), "Names are not the same"),
                () -> assertEquals(user.getEmail(), deserializedUser.getEmail(), "Emails are not the same"),
                () -> assertTrue(deserializedUser.getId() > 0, "id key is not present in the response body"),
                () -> assertFalse(deserializedUser.getName().isEmpty(), "name key is not present in the response body"),
                () -> assertFalse(deserializedUser.getEmail().isEmpty(), "email key is not present in the response body"),
                () -> assertFalse(deserializedUser.getStatus().isEmpty(), "status key is not present in the response body"),
                () -> assertFalse(deserializedUser.getGender().isEmpty(), "gender key is not present in the response body")
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
        user.setName(name);
        user.setEmail(email);
//        body = "{\n" +
//                "    \"name\": \"" + name + "\",\n" +
//                "    \"email\": \"" + email + "\",\n" +
//                "    \"gender\": \"female\",\n" +
//                "    \"status\": \"active\"\n" +
//                "}";
        Response updateResponse = updateUserByIdResponse(AUTH, gson.toJson(user), currentUserId);
        Assumptions.assumeTrue(updateResponse.getStatusCode() == 200, "Update user didn't return 200 status code");
    }

    public void verifyUserIsUpdated() {
        Response getUserResponse = getUserByIdResponse(AUTH, currentUserId);
        deserializedUser = gson.fromJson(getUserResponse.asString(), UsersPojo.class);
        assertAll(

                () -> assertEquals(user.getName(), deserializedUser.getName(), "Names are not the same"),
                () -> assertEquals(user.getEmail(), deserializedUser.getEmail(), "Emails are not the same"),
                () -> assertEquals(user.getStatus(), deserializedUser.getStatus(), "Statuses are not the same"),
                () -> assertEquals(user.getGender(), deserializedUser.getGender(), "Names are not the same")
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
