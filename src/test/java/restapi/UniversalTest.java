package restapi;

import com.google.gson.JsonObject;
import static io.restassured.RestAssured.given;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


public class UniversalTest {
    private RequestSpecification requestSpec;
    private String ServerAddress = "http://localhost";
    private final JsonObject variable_set = new JsonObject();
    
    @BeforeClass
    @Parameters ({"server"})
    public void CheckConditions(@Optional String server) {
        System.out.println("CheckConditions" );
        if (server != null) {
            ServerAddress = server;
        }
        requestSpec = new RequestSpecBuilder().setBaseUri(ServerAddress).setPort(8091)
        .setContentType(ContentType.JSON).build();     
        given().spec(requestSpec).get("bear")
        .then().assertThat().statusCode(200).body(matchesJsonSchemaInClasspath("empty_list.json"));
    }
    
    @Test(dataProvider = "BearProvider", dataProviderClass = DataProviderSource.class)
    @TestDataProviderParameters(path = "src\\test\\java\\restapi\\datasets\\universal_template.json")
    public void Universal(JsonObject item) throws IllegalAccessException, InterruptedException {
        String url = item.get("header").getAsJsonObject().getAsJsonPrimitive("url").getAsString();
        String method = item.get("header").getAsJsonObject().getAsJsonPrimitive("method").getAsString();
        Integer code = item.get("header").getAsJsonObject().getAsJsonPrimitive("code").getAsInt();
        String schema = item.get("schema").toString();
        String variable = item.get("variable").getAsString();
        boolean test_enabled = item.get("header").getAsJsonObject().getAsJsonPrimitive("enable").getAsBoolean();
        
        if (!test_enabled){
            return;
        }
        Object res = new Object();

        switch (method){
            case "post":
                // POST a new bear and check the response code and schema
                res = given().spec(requestSpec).body(item.get("body"),ObjectMapperType.GSON)
                    .when().post(url)
                    .then().statusCode(code).body(matchesJsonSchema(schema));
                break;
            case "get":
                // GET a bear info and check the response code and schema
                res = given().spec(requestSpec).pathParams(variable, variable_set.get(variable).getAsString())
                    .when().get(url)
                    .then().statusCode(code).body(matchesJsonSchema(schema));
                break;
            case "delete":
                // DELETE a bear item and check the response code
                res = given().spec(requestSpec).pathParams(variable, variable_set.get(variable).getAsString())
                    .when().delete(url)
                    .then().statusCode(code);
                break;
            case "put":
                // DELETE a bear item and check the response code
                res = given().spec(requestSpec).pathParams(variable, variable_set.get(variable).getAsString()).body(item.get("body"),ObjectMapperType.GSON)
                    .when().put(url)
                    .then().statusCode(code);
                break;
        }
        
        //Save response into variable if required
        if (!item.get("save response").getAsString().isEmpty()){
            ValidatableResponse response = (ValidatableResponse) res;
            variable_set.addProperty(item.get("save response").getAsString(), response.extract().body().asString());
        }
    }
    
}
