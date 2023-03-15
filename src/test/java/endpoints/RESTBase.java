package endpoints;

import com.github.javafaker.Faker;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;

import utilities.ConfigReader;

public class RESTBase {
    public final Faker FAKER = new Faker();
    public final String APIHOST = ConfigReader.getConfigProperty("api.host");
    public final String APIV = ConfigReader.getConfigProperty("api.ver");
    public final String AUTH = ConfigReader.getConfigProperty("api.auth");


//@BeforeAll
//public static void setUp(){
//    ConfigReader.initializeProperties();
//}


//    @BeforeStep
//    public void baseUrlSetup() {
//        // Setting up baseURL by adding host to version of api
//
//        RestAssured.baseURI = APIHOST + APIV;
//    }
}
