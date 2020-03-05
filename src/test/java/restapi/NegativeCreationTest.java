package restapi;

import com.google.gson.JsonObject;

import static io.restassured.RestAssured.given;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import io.restassured.specification.RequestSpecification;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class NegativeCreationTest {
    private RequestSpecification requestSpec;
    private String ServerAddress = "http://localhost";
    
    public NegativeCreationTest() {
    }    
        
    @BeforeClass
    @Parameters ({"server"})
    public void CheckConditions(@Optional String server) {
        System.out.println("NegativeCreationTest. Check start conitions" );
        if (server != null) {
            ServerAddress = server;
        }
        requestSpec = new RequestSpecBuilder().setBaseUri(ServerAddress).setPort(8091)
        .setContentType(ContentType.JSON).build();
        given().spec(requestSpec).get("bear")
        .then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("empty_list.json"));
    }
    
    @Test(priority = 1, dataProvider = "BearProvider", dataProviderClass = DataProviderSource.class)
    @TestDataProviderParameters(path = "src\\test\\java\\restapi\\datasets\\negative_bear_items.json")
    public void InsertBear(JsonObject item) {
        //Check id is got on creation step
        System.out.println("NegativeCreationTest. Creating a bear. POST to /bear: " + item.toString());

        //PUT changes for the item
        given().spec(requestSpec).body(item,ObjectMapperType.GSON)
                                    .when().post("bear")
                                    .then().assertThat().statusCode(400);
    }
    @Test(priority = 3)
    public void CheckList() {
        System.out.println("NegativeCreationTest. Checking list is empty");

        given().spec(requestSpec).get("bear")
        .then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("empty_list.json"));
    }
}
