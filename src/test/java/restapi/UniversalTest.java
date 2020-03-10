package restapi;

import com.google.gson.JsonObject;
import static io.restassured.RestAssured.given;
import io.restassured.mapper.ObjectMapperType;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import io.restassured.response.ValidatableResponse;

import org.testng.annotations.Test;


public class UniversalTest extends CommonClass{
    private final JsonObject variable_set = new JsonObject();
    
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
