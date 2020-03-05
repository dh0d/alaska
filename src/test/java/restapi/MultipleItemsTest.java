package restapi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static io.restassured.RestAssured.given;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class MultipleItemsTest {
    private RequestSpecification requestSpec;
    private String ServerAddress = "http://localhost";

    public final String bears = "/bear/{id}";
    JsonObject variable_set = new JsonObject();
    private String FirstId;
    private String SecondId;
    String[] FirstItem = new String[2];
    String[] SecondItem = new String[2];
    
    public MultipleItemsTest() {
        this.FirstId = "";
        this.SecondId = "";
        this.FirstItem[0] = "{ \"bear_type\": \"BROWN\", \"bear_name\": \"123\", \"bear_age\":0 }";
        this.FirstItem[1] = "{ \"bear_type\": \"BROWN\", \"bear_name\": \"Correct name\", \"bear_age\":10 }";
        this.SecondItem[0] = "{ \"bear_type\": \"POLAR\", \"bear_name\": \"UMKA\", \"bear_age\":2 }";
        this.SecondItem[1] = "{ \"bear_type\": \"BROWN\", \"bear_name\": \"Paddington\", \"bear_age\":12 }";
    }
    @BeforeClass
    @Parameters ({"server"})
    public void CheckStartConditions(@Optional String server) {
        System.out.println("MultipleItemsTest. Check start conitions" );
        if (server != null) {
            ServerAddress = server;
        }
        requestSpec = new RequestSpecBuilder().setBaseUri(ServerAddress).setPort(8091)
        .setContentType(ContentType.JSON).build();
        given().spec(requestSpec).get("bear")
        .then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("empty_list.json"));
    }
    
    @Test(priority = 1)
    public void CreateFirstBear() {
        System.out.println("CreateFirstBear");
        JsonObject item = new JsonParser().parse(this.FirstItem[0]).getAsJsonObject();
        // POST new bear and check the response code and schema
        ValidatableResponse resp = given().spec(requestSpec).body(item, ObjectMapperType.GSON)
        .when().post("bear").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("id.json"));
        this.FirstId= resp.extract().body().asString();
    }
    
    @Test(priority = 2)
    public void CheckFirstBear() {
        //Check id is got on creation step
        if("".equals(this.FirstId))
            throw new SkipException("Skipping this exception");   
        System.out.println("CheckFirstBear");
        JsonObject item = new JsonParser().parse(this.FirstItem[0]).getAsJsonObject();

        //GET bear attributes and check the response code and schema 
        ValidatableResponse resp = given().spec(requestSpec).pathParams("id", this.FirstId).get(bears).then().statusCode(200)
        .body(matchesJsonSchemaInClasspath("bear_item.json"));

        //Compare set and gotten data 
        JsonElement bear = new JsonParser().parse(resp.extract().body().asString());
        bear.getAsJsonObject().remove("bear_id");
        Assert.assertEquals(bear, item);
    }
 
    @Test(priority = 3)
    public void CreateSecondBear() {
        System.out.println("CreateSecondBear");
        JsonObject item = new JsonParser().parse(this.SecondItem[0]).getAsJsonObject();
        // POST new bear and check the response code and schema
        ValidatableResponse resp = given().spec(requestSpec).body(item, ObjectMapperType.GSON)
        .when().post("bear").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("id.json"));
        this.SecondId= resp.extract().body().asString();
    }
    
    @Test(priority = 4)
    public void CheckSecondBear() {
        //Check id is got on creation step
        if("".equals(this.SecondId))
            throw new SkipException("Skipping this exception");   
        System.out.println("CheckSecondBear" );
        JsonObject item = new JsonParser().parse(this.SecondItem[0]).getAsJsonObject();

        //GET bear attributes and check the response code and schema 
        ValidatableResponse resp = given().spec(requestSpec).pathParams("id", this.SecondId).get(bears).then().statusCode(200)
        .body(matchesJsonSchemaInClasspath("bear_item.json"));

        //Compare set and gotten data 
        JsonElement bear = new JsonParser().parse(resp.extract().body().asString());
        bear.getAsJsonObject().remove("bear_id");
        Assert.assertEquals(bear, item);
    }

    @Test(priority = 5)    
    public void ChangeFirstBear() {
        //Check id is got on creation step
        if("".equals(this.FirstId))
            throw new SkipException("Skipping this exception");           
        System.out.println("ChangeFirstBear" );
        JsonObject item = new JsonParser().parse(this.FirstItem[1]).getAsJsonObject();
        
        //PUT changes for the item
        ValidatableResponse resp = given().spec(requestSpec).pathParams("id", this.FirstId).body(item,ObjectMapperType.GSON)
                                    .when().put(bears)
                                    .then().statusCode(200);
        Assert.assertEquals(resp.extract().asString(),"OK");
    }

    @Test(priority = 6)    
    public void ChangeSecondBear() {
        //Check id is got on creation step
        if("".equals(this.SecondId))
            throw new SkipException("Skipping this exception");           
        System.out.println("ChangeSecondBear" );
        JsonObject item = new JsonParser().parse(this.SecondItem[1]).getAsJsonObject();
        
        //PUT changes for the item
        ValidatableResponse resp = given().spec(requestSpec).pathParams("id", this.SecondId).body(item,ObjectMapperType.GSON)
                                    .when().put(bears)
                                    .then().statusCode(200);
        Assert.assertEquals(resp.extract().asString(),"OK");
    }
    @Test(priority = 7)
    public void CheckFirstBearAfterChange() {
        //Check id is got on creation step
        if("".equals(this.FirstId))
            throw new SkipException("Skipping this exception");   
        System.out.println("CheckFirstBear");
        JsonObject item = new JsonParser().parse(this.FirstItem[1]).getAsJsonObject();

        //GET bear attributes and check the response code and schema 
        ValidatableResponse resp = given().spec(requestSpec).pathParams("id", this.FirstId).get(bears).then().statusCode(200)
        .body(matchesJsonSchemaInClasspath("bear_item.json"));

        //Compare set and gotten data 
        JsonElement bear = new JsonParser().parse(resp.extract().body().asString());
        bear.getAsJsonObject().remove("bear_id");
        Assert.assertEquals(bear, item);
    }
    @Test(priority = 8)
    public void CheckSecondBearAfterChange() {
        //Check id is got on creation step
        if("".equals(this.SecondId))
            throw new SkipException("Skipping this exception");   
        System.out.println("CheckSecondBear" );
        JsonObject item = new JsonParser().parse(this.SecondItem[1]).getAsJsonObject();

        //GET bear attributes and check the response code and schema 
        ValidatableResponse resp = given().spec(requestSpec).pathParams("id", this.SecondId).get(bears).then().statusCode(200)
        .body(matchesJsonSchemaInClasspath("bear_item.json"));

        //Compare set and gotten data 
        JsonElement bear = new JsonParser().parse(resp.extract().body().asString());
        bear.getAsJsonObject().remove("bear_id");
        Assert.assertEquals(bear, item);
    }

    @Test(priority = 9)
    public void DeleteFirstBear() {
        System.out.println("DeleteFirstBear");

        ValidatableResponse resp = given().spec(requestSpec).pathParams("id", this.FirstId).delete(bears)
        .then().assertThat().statusCode(200);
        org.testng.Assert.assertEquals(resp.extract().asString(),"OK");
    }
    
    @Test(priority = 10)
    public void ReCheckSecondBear() {
        //Check id is got on creation step
        if("".equals(this.SecondId))
            throw new SkipException("Skipping this exception");   
        System.out.println("CheckSecondBear" );
        JsonObject item = new JsonParser().parse(this.SecondItem[1]).getAsJsonObject();

        //GET bear attributes and check the response code and schema 
        ValidatableResponse resp = given().spec(requestSpec).pathParams("id", this.SecondId).get(bears).then().statusCode(200)
        .body(matchesJsonSchemaInClasspath("bear_item.json"));

        //Compare set and gotten data 
        JsonElement bear = new JsonParser().parse(resp.extract().body().asString());
        bear.getAsJsonObject().remove("bear_id");
        Assert.assertEquals(bear, item);
    } 

    @Test(priority = 11)
    public void DeleteSecondBear() {
        System.out.println("DeleteSecondBear");

        ValidatableResponse resp = given().spec(requestSpec).pathParams("id", this.SecondId).delete(bears)
        .then().assertThat().statusCode(200);
        org.testng.Assert.assertEquals(resp.extract().asString(),"OK");
    }

    @Test(priority = 12)
    public void CheckList() {
        System.out.println("MultipleItemsTest. Checking list is empty" );

        given().spec(requestSpec).get("bear")
        .then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("empty_list.json"));
    }    

}