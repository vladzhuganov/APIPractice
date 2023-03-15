package stepdefs;

import endpoints.RESTUsers;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class UsersSteps {

    RESTUsers users = RESTUsers.getInstance();

    @Given("Create new user")
    public void create_new_user() {
        users.createNewUser();
    }

    @Then("Get user by id")
    public void get_user_by_id() {
        users.getUserById();
    }

    @Then("Verify that user was created")
    public void verify_that_user_was_created() {
        users.verifyUserWasCreated();
    }

    @Then("Delete user by id")
    public void delete_user_by_id() {
        users.deleteUserById();
    }


    @Then("Update user")
    public void updateUser() {
        users.updateUserById();
    }

    @And("Verify user is updated")
    public void verifyUserIsUpdated() {
        users.verifyUserIsUpdated();
    }

    @And("Verify user does not exist")
    public void verifyUserDoesNotExist() {
        users.verifyUserDoesNotExist();
    }
}
