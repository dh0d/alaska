package restapi;

import org.testng.annotations.DataProvider;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader; 
import java.io.BufferedReader;
import java.io.FileNotFoundException;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataProviderSource {

    @DataProvider(name="BearProvider") 
    private static Iterator<Object[]> getBearItem(final Method testMethod) throws FileNotFoundException {
        TestDataProviderParameters TestFileParameters = testMethod.getAnnotation(TestDataProviderParameters.class);
        BufferedReader reader = new BufferedReader(new FileReader(TestFileParameters.path()));
        JsonElement jelement = new JsonParser().parse(reader);
        List<Object[]> data = new ArrayList<>();
        for (JsonElement test : jelement.getAsJsonArray()){
            JsonObject  jobject = test.getAsJsonObject();
            data.add(new Object[] {jobject});
        }
        return data.iterator();
    };   

}