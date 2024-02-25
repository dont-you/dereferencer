package ru.fusionsoft.dereferencer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DereferencerIT {

    ObjectMapper jsonMapper = new ObjectMapper();

    @Test
    public void Test_simple_schema_With_cycle() throws DereferenceException {
        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        dereferencer.dereference(URI.create("./src/test/resources/test-schemas/schemas/cycle-schema/cycle_schema_A.json"));
        assertTrue(true);
   }
   @Test
   public void delme()
           throws URISyntaxException, IOException {
        URI uri = new URI("file:/home/who/Work/Projects/Dereferencer/src/test/resources/test-schemas/schemas/cycle-schema/cycle_schema_A.json");
       URLConnection urlConnection = uri.toURL().openConnection();
       urlConnection.connect();
       System.out.println(jsonMapper.readTree(urlConnection.getInputStream()));
       System.out.println(urlConnection.getURL());
       System.out.println(urlConnection.getContentType());
   }

   @Test
   public void debug_cases(){
        try{
            URI uriToDebugCase = URI.create(System.getenv().get("HOME")+"/Temp/debug.yaml");
            Dereferencer dereferencer = DereferencerBuilder.builder().build();
            JsonNode node = dereferencer.dereference(uriToDebugCase);
            System.out.println(node);
        } catch (Exception e){
            e.printStackTrace();
        }
   }

   @Test
   public void Test_simple_schema_With_plain_name_fragment_reference()
           throws IOException, DereferenceException {
        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        JsonNode actual = dereferencer.dereference(URI.create("./src/test/resources/test-schemas/schemas/basic-schemas/simple_anchor_schema.json"));
        JsonNode expected = jsonMapper.readTree(Paths
                .get("./src/test/resources/test-schemas/expected-result/dereferenced_simple_anchor_schema.json").toFile());

        assertEquals(expected, actual);
    }

   @Test
   public void Test_schema_With_relative_json_pointers()
           throws IOException, DereferenceException {
        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        JsonNode actual = dereferencer.dereference(URI.create("./src/test/resources/test-schemas/schemas/basic-schemas/relative_json_pointers.json"));
        JsonNode expected = jsonMapper.readTree(Paths
                .get("./src/test/resources/test-schemas/expected-result/dereferenced_relative_json_pointers.json").toFile());

        assertEquals(expected, actual);
    }

   @Test
   public void Test_schema_With_urn_references()
           throws IOException, DereferenceException {
        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        JsonNode actual = dereferencer.dereference(URI.create("./src/test/resources/test-schemas/schemas/urn-resolving/test_urn_resolving.yaml"));
        JsonNode expected = jsonMapper.readTree(Paths
                .get("./src/test/resources/test-schemas/expected-result/dereferenced_test_urn_resolving.json").toFile());
        assertEquals(expected, actual);
    }

    @Test
    public void fuzTest() throws DereferenceException, IOException {
        Dereferencer dereferencer = DereferencerBuilder.builder().enableAllOfMerge().build();
//        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        String HOME = System.getenv().get("HOME");

        JsonNode json1 = dereferencer.dereference(URI.create(HOME+"/Work/schemes/fipc.yaml").normalize());
//        System.out.println(json1);

        JsonNode json2 = dereferencer.dereference(URI.create(HOME+"/Work/service/fipc-db-service.yaml").normalize());
        System.out.println(json2);

        assertEquals(jsonMapper.readTree(Paths.get(URI.create("file://" + HOME + "/Work/fipc-it-with-merge.json").normalize()).toFile()), json2);
//        assertEquals(jsonMapper.readTree(Paths.get(URI.create("file://" + HOME + "/Work/fipc-it.json").normalize()).toFile()), json2);
    }

    @Test
    public void Test_simple_merge_schema()
            throws DereferenceException, IOException {
        Dereferencer dereferencer = DereferencerBuilder.builder().enableAllOfMerge().build();
        JsonNode actual = dereferencer
                .dereference(URI.create("./src/test/resources/test-schemas/schemas/basic-schemas/simple_merge_schema.json"));
        JsonNode expected = jsonMapper.readTree(
                Paths.get("./src/test/resources/test-schemas/expected-result/dereferenced_simple_merge_schema.json")
                        .toFile());

        assertEquals(expected, actual);
    }

    @Test
    public void test()
            throws IOException, DereferenceException{
//        Dereferencer dereferencer = DereferencerBuilder.builder().build();
//        JsonNode node = jsonMapper.readTree("{" +
//                "\"$id\":\"anon_test.json\","+
//                "\"type\":\"object\"," +
//                "\"properties\":{" +
//                "\"first_email\":{" +
//                "\"type\":\"string\"," +
//                "\"format\":\"email\"" +
//                "}," +
//                "\"second_email\":{" +
//                "\"$ref\":\"1/first_email\"" +
//                "}}}");
//
//        JsonNode actual = dereferencer.anonymousDereference(node);
//        JsonNode expected = jsonMapper.readTree(
//                "{" +
//                    "\"$id\":\"anon_test.json\"," +
//                    "\"type\":\"object\"," +
//                    "\"properties\":{" +
//                        "\"first_email\":{" +
//                            "\"type\":\"string\"," +
//                            "\"format\":\"email\"" +
//                        "}," +
//                        "\"second_email\":{" +
//                            "\"type\":\"string\"," +
//                            "\"format\":\"email\"" +
//                "}}}");
//
//        assertEquals(expected,actual);
    }
}
