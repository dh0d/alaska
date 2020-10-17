package restapi;

import com.google.gson.JsonObject;

import static io.restassured.RestAssured.given;
import io.restassured.mapper.ObjectMapperType;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import org.testng.annotations.Test;

public class NegativeCreationTest extends CommonClass{
    
    public NegativeCreationTest() {
    }    
        
    @Test(priority = 1, dataProvider = "BearProvider", dataProviderClass = DataProviderSource.class)
    @TestDataProviderParameters(path = "src/test/java/restapi/datasets/negative_bear_items.json")
    public void InsertBear(JsonObject item) {
        //Check id is got on creation step
        System.out.println("NegativeCreationTest. Creating a bear. POST to /bear: " + item.toString());

        //PUT changes for the item
        given().spec(requestSpec).body(item,ObjectMapperType.GSON)
                                    .when().post("bear")
                                    .then().assertThat().statusCode(400);
    }
    @Test(priority = 2)
    public void CheckList() {
        System.out.println("NegativeCreationTest. Checking list is empty");

        given().spec(requestSpec).get("bear")
        .then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("empty_list.json"));
    }
}
