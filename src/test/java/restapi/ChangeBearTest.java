package restapi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static io.restassured.RestAssured.given;
import io.restassured.mapper.ObjectMapperType;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import io.restassured.response.ValidatableResponse;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class ChangeBearTest extends CommonClass{
    private String id;
    private final JsonObject InitBear;
    
    public ChangeBearTest() {
        this.id = "";
        String bear = "{ \"bear_type\": \"BLACK\", \"bear_name\": \"MIKHAIL\", \"bear_age\":17.5 }";
        this.InitBear = new JsonParser().parse(bear).getAsJsonObject();
    }
    
    @Test(priority = 1)
    public void CreateBear() {
        System.out.println("ChangeBearTest. Creating a bear. POST to /bear: " + this.InitBear.toString());

        // POST new bear and check the response code and schema
        ValidatableResponse resp = given().spec(requestSpec).body(this.InitBear, ObjectMapperType.GSON)
        .when().post("bear").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("id.json"));
        this.id = resp.extract().body().asString();
    }
    
    @Test(priority = 2)
    public void CheckBear() {
        //Check id is got on creation step
        if("".equals(this.id))
            throw new SkipException("Skipping this exception");   
        System.out.println("ChangeBearTest. Checking the bear. GET to /bear: " + this.InitBear.toString());

        //GET bear attributes and check the response code and schema 
        ValidatableResponse resp = given().spec(requestSpec).pathParams("id", this.id).get(bears).then().statusCode(200)
        .body(matchesJsonSchemaInClasspath("bear_item.json"));

        //Compare set and gotten data 
        JsonElement bear = new JsonParser().parse(resp.extract().body().asString());
        bear.getAsJsonObject().remove("bear_id");
        Assert.assertEquals(bear, this.InitBear);
    }
    
    @Test(priority = 3, dataProvider = "BearProvider", dataProviderClass = DataProviderSource.class)
    @TestDataProviderParameters(path = "src/test/java/restapi/datasets/positive_change_items.json")
    public void ChangeBear(JsonObject item) {
        //Check id is got on creation step
        if("".equals(this.id))
            throw new SkipException("Skipping this exception");           
        System.out.println("ChangeBearTest. Changing the bear. PUT to /bear: " + item.toString());

        //PUT changes for the item
        ValidatableResponse resp = given().spec(requestSpec).pathParams("id", this.id).body(item,ObjectMapperType.GSON)
                                    .when().put(bears)
                                    .then().statusCode(200);
        Assert.assertEquals(resp.extract().asString(),"OK");

        //GET the item and check changes
        resp = given().spec(requestSpec).pathParams("id", this.id).get(bears).then().assertThat().statusCode(200);
        //Compare set and gotten data 
        JsonElement bear = new JsonParser().parse(resp.extract().body().asString());
        bear.getAsJsonObject().remove("bear_id");
        Assert.assertEquals(bear, item);
    }

    @Test(priority = 4)
    public void DeleteBear() {
        System.out.println("ChangeBearTest. Deleting the bear. DELETE to /bear/" + this.id);

        ValidatableResponse resp = given().spec(requestSpec).pathParams("id", id).delete(bears)
        .then().assertThat().statusCode(200);
        org.testng.Assert.assertEquals(resp.extract().asString(),"OK");
    }

}
