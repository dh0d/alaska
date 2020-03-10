package restapi;

import com.google.gson.JsonObject;

import static io.restassured.RestAssured.given;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import io.restassured.specification.RequestSpecification;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class CommonClass {
    JsonObject item;
    RequestSpecification requestSpec;
    String ServerAddress = "http://localhost";
    final static String bears = "/bear/{id}";

    public CommonClass() {
    }
 
    @BeforeTest
    @Parameters ({"server"})
    public void CheckStartConditions(@Optional String server) {
        if (server != null) {
            ServerAddress = server;
        }

        requestSpec = new RequestSpecBuilder().setBaseUri(ServerAddress).setPort(8091)
        .setContentType(ContentType.JSON).build();
        System.out.println("Check start conitions" );
        given().spec(requestSpec).get("bear")
        .then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("empty_list.json"));
    }
    
    @AfterTest
    public void AfterwardCheck() {
        System.out.println("Afterward checking" );
        given().spec(requestSpec).get("bear")
        .then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("empty_list.json"));
    }
    
}
