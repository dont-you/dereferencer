package ru.fusionsoft.dereferencer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DereferencerIT {

    ObjectMapper jsonMapper = new ObjectMapper();

    @Test
    public void Test_simple_schema_With_cycle() throws DereferenceException {
        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        dereferencer.dereference(URI.create("./src/integration-test/resources/test-schemas/schemas/cycle-schema/cycle_schema_A.json"));
        assertTrue(true);
   }
   @Test
   public void delme()
           throws URISyntaxException {
        URI uri = new URI("file:/home/who/Work/Projects");
        System.out.println(new URI("Work/Projects/src").relativize(uri));
   }

   @Test
   public void Test_simple_schema_With_plain_name_fragment_reference()
           throws IOException, DereferenceException {
        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        JsonNode actual = dereferencer.dereference(URI.create("./src/integration-test/resources/test-schemas/schemas/basic-schemas/simple_anchor_schema.json"));
        JsonNode expected = jsonMapper.readTree(Paths
                .get("./src/integration-test/resources/test-schemas/expected-result/dereferenced_simple_anchor_schema.json").toFile());

        assertEquals(expected, actual);
    }

   @Test
   public void Test_schema_With_relative_json_pointers()
           throws IOException, DereferenceException {
        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        JsonNode actual = dereferencer.dereference(URI.create("./src/integration-test/resources/test-schemas/schemas/basic-schemas/relative_json_pointers.json"));
        JsonNode expected = jsonMapper.readTree(Paths
                .get("./src/integration-test/resources/test-schemas/expected-result/dereferenced_relative_json_pointers.json").toFile());

        assertEquals(expected, actual);
    }

   @Test
   public void Test_schema_With_urn_references()
           throws IOException, DereferenceException {
        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        JsonNode actual = dereferencer.dereference(URI.create("./src/integration-test/resources/test-schemas/schemas/urn-resolving/test_urn_resolving.yaml"));
        JsonNode expected = jsonMapper.readTree(Paths
                .get("./src/integration-test/resources/test-schemas/expected-result/dereferenced_test_urn_resolving.json").toFile());
        assertEquals(expected, actual);
    }

    @Test
    public void fuzTest() throws DereferenceException {
        Dereferencer dereferencer = DereferencerBuilder.builder().enableAllOfMerge().build();
        String HOME = System.getenv().get("HOME");

        JsonNode json1 = dereferencer.dereference(URI.create(HOME+"/Temp/schemes/fipc.yaml").normalize());
        System.out.println(json1);

        JsonNode json2 = dereferencer.dereference(URI.create(HOME+"/Temp/service/fipc-db-service.yaml").normalize());
        System.out.println(json2);

    }
    @Test
    public void Test_simple_merge_schema()
            throws DereferenceException, IOException {
        Dereferencer dereferencer = DereferencerBuilder.builder().enableAllOfMerge().build();
        JsonNode actual = dereferencer
                .dereference(URI.create("./src/integration-test/resources/test-schemas/schemas/basic-schemas/simple_merge_schema.json"));
        JsonNode expected = jsonMapper.readTree(
                Paths.get("./src/integration-test/resources/test-schemas/expected-result/dereferenced_simple_merge_schema.json")
                        .toFile());

        assertEquals(expected, actual);
    }

    @Test
    public void test()
            throws IOException, DereferenceException{
        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        JsonNode node = jsonMapper.readTree("{" +
                "\"$id\":\"anon_test.json\","+
                "\"type\":\"object\"," +
                "\"properties\":{" +
                "\"first_email\":{" +
                "\"type\":\"string\"," +
                "\"format\":\"email\"" +
                "}," +
                "\"second_email\":{" +
                "\"$ref\":\"1/first_email\"" +
                "}}}");

        JsonNode actual = dereferencer.anonymousDereference(node);
        JsonNode expected = jsonMapper.readTree(
                "{" +
                    "\"$id\":\"anon_test.json\"," +
                    "\"type\":\"object\"," +
                    "\"properties\":{" +
                        "\"first_email\":{" +
                            "\"type\":\"string\"," +
                            "\"format\":\"email\"" +
                        "}," +
                        "\"second_email\":{" +
                            "\"type\":\"string\"," +
                            "\"format\":\"email\"" +
                "}}}");

        assertEquals(expected,actual);
    }
}
