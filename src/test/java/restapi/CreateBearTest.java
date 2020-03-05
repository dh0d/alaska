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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class CreateBearTest {
    private JsonObject item;
    RequestSpecification requestSpec;
    private String ServerAddress = "http://localhost";
    
    public final static String bears = "/bear/{id}";
    private String id;
    

    @Factory(dataProvider = "getBearItem")
    public CreateBearTest(JsonObject item){
        this.item = item;
        this.id = "";
    }
    
    @BeforeTest
    @Parameters ({"server"})
    public void CheckStartConditions(@Optional String server) {
        if (server != null) {
            ServerAddress = server;
        }
        requestSpec = new RequestSpecBuilder().setBaseUri(ServerAddress).setPort(8091)
        .setContentType(ContentType.JSON).build();
        System.out.println("CreateBearTest. Check start conitions" );
        given().spec(requestSpec).get("bear")
        .then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("empty_list.json"));
    }

    @BeforeClass
    @Parameters ({"server"})
    public void Init(@Optional String server){
        if (server != null) {
            ServerAddress = server;
        }
        requestSpec = new RequestSpecBuilder().setBaseUri(ServerAddress).setPort(8091)
        .setContentType(ContentType.JSON).build();        
    }
    
    @Test()
    public void Step1Create() {
        System.out.println("CreateBearTest. Creating a bear. POST to /bear: " + item.toString());
        
        // POST new bear and check the response code and schema
        ValidatableResponse resp = given().spec(requestSpec).body(item, ObjectMapperType.GSON)
        .when().post("bear").then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("id.json"));
        this.id = resp.extract().body().asString();
    }

    @Test()
    public void Step2Check() {
        //Check id is got on creation step
        if("".equals(this.id))
            throw new SkipException("Skipping this exception");   
        System.out.println("CreateBearTest. Checking the bear. GET to /bear: " + item.toString());

        //GET bear attributes and check the response code and schema 
        ValidatableResponse resp = given().spec(requestSpec).pathParams("id", this.id).get(bears).then().statusCode(200)
        .body(matchesJsonSchemaInClasspath("bear_item.json"));

        //Compare set and gotten data 
        JsonElement bear = new JsonParser().parse(resp.extract().body().asString());
        bear.getAsJsonObject().remove("bear_id");
        Assert.assertEquals(bear, item);
    }

    @Test()
    public void Step3Delete() {
        //Check id is got on creation step
        if("".equals(this.id))
            throw new SkipException("Skipping this exception");   
        System.out.println("CreateBearTest. Deleting the bear. DELETE to /bear: " + item.toString());
        // DELETE the created bear
        ValidatableResponse resp = given().spec(requestSpec).pathParams("id", id).delete(bears)
        .then().assertThat().statusCode(200);
        Assert.assertEquals(resp.extract().asString(),"OK");
    }

    @AfterTest
    public void AfterwardCheck() {
        System.out.println("CreateBearTest. Afterward checking" );
        given().spec(requestSpec).get("bear")
        .then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("empty_list.json"));
    }
    
    @DataProvider
    public static Iterator<Object[]> getBearItem() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader("src\\test\\java\\restapi\\datasets\\positive_bear_items.json"));
        JsonElement jelement = new JsonParser().parse(reader);
        List<Object[]> data = new ArrayList<>();
        for (JsonElement test : jelement.getAsJsonArray()){
            JsonObject  jobject = test.getAsJsonObject();
            data.add(new Object[] {jobject});
        }
        return data.iterator();
    }
    
}
