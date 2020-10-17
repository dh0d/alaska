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
import org.testcontainers.containers.GenericContainer;

public class CommonClass {
    JsonObject item;
    RequestSpecification requestSpec;
    String ServerAddress = "http://localhost";
    final static String bears = "/bear/{id}";
    private GenericContainer alaska;
    Integer port;

    public CommonClass() {
    }
 
    @BeforeTest
    @Parameters ({"server"})
    public void CheckStartConditions(@Optional String server) {
        System.out.println("Run container");
        alaska = new GenericContainer("azshoo/alaska:1.0").withExposedPorts(8091);
        ServerAddress = "http://" + alaska.getContainerIpAddress();
        alaska.start();
        port = alaska.getMappedPort(8091);

        if (server != null) {
            ServerAddress = server;
        }

        requestSpec = new RequestSpecBuilder().setBaseUri(ServerAddress).setPort(port)
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
        System.out.println("Stop container");
        alaska.stop();
    }
    
}
