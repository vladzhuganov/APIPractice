package stepdefs;

import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.restassured.RestAssured;
import utilities.ConfigReader;

public class Hooks {

    @Before
    public static void setUp() {
        ConfigReader.initializeProperties();
        RestAssured.baseURI = ConfigReader.getConfigProperty("api.host") + ConfigReader.getConfigProperty("api.ver");
    }


}
