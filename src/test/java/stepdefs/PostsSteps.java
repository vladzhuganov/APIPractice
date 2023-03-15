package stepdefs;

import endpoints.RESTPosts;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class PostsSteps {
    RESTPosts posts = RESTPosts.getInstance();

    @Given("Create new post")
    public void create_new_post() {
        posts.createNewPost();
    }

    @Then("Verify post was created")
    public void verify_post_was_created() {
        posts.verifyPostWasCreated();
    }

    @Then("Delete post by using post id")
    public void delete_post_by_using_post_id() {
        posts.deletePostById();
    }

    @And("Get post by id")
    public void getPostById() {
        posts.getPostById();
    }

    @Then("Update post")
    public void updatePost() {
        posts.updateCurrentPost();
    }

    @And("Verify post is updated")
    public void verifyPostIsUpdated() {
        posts.verifyPostIsUpdated();
    }

    @And("Verify post no longer exists")
    public void verifyPostNoLongerExists() {
        posts.verifyPostDoesNotExists();
    }
}
