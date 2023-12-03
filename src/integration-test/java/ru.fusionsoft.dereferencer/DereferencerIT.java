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

//    @Test
//    public void Test_simple_scheme_With_url_reference() throws LoadException, IOException {
//        Dereferencer dereferencer = new Dereferencer(DereferenceConfiguration.builder().build());
//        JsonNode actual = dereferencer
//            .dereference(URI.create("./src/integration-test/resources/test-schemes/schemes/simple_scheme_with_url_ref.json"));
//        JsonNode expected = jsonMapper.readTree(Paths
//                                            .get("./src/integration-test/resources/test-schemes/expected-result/dereferenced_simple_scheme_with_url_ref.json")
//                                            .toFile());
//
//        assertEquals(expected, actual);
//   }

    @Test
    public void Test_simple_scheme_With_cycle() throws IOException, DereferenceException {
        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        JsonNode actual = dereferencer.dereference(URI.create("./src/integration-test/resources/test-schemes/schemes/cycle_schema_A.json"));

        assertTrue(true);
   }
   @Test
   public void Test_simple_scheme_With_local_And_remote_references()
           throws IOException, DereferenceException {
        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        JsonNode actual = dereferencer.dereference(URI.create("./src/integration-test/resources/test-schemes/schemes/simple_scheme.json"));
        JsonNode expected = jsonMapper.readTree(Paths
                .get("./src/integration-test/resources/test-schemes/expected-result/dereferenced_simple_scheme.json").toFile());

        assertEquals(expected, actual);
    }

   @Test
   public void delme()
           throws IOException, DereferenceException, URISyntaxException {
    }
//
//    @Test
//   public void Test_simple_scheme_With_plain_name_fragment_reference()
//           throws IOException, LoadException {
//        Dereferencer dereferencer = new Dereferencer(DereferenceConfiguration.builder().build());
//        JsonNode actual = dereferencer.dereference(URI.create("./src/integration-test/resources/test-schemes/schemes/simple_scheme.json"));
//        JsonNode expected = jsonMapper.readTree(Paths
//                .get("./src/integration-test/resources/test-schemes/expected-result/dereferenced_simple_scheme.json").toFile());
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void Test_schema_With_ref_To_schema_With_Anchor() throws LoadException, IOException {
//        Dereferencer dereferencer = new Dereferencer(DereferenceConfiguration.builder().build());
//        JsonNode actual = dereferencer.dereference(URI.create("./src/integration-test/resources/test-schemes/schemes/schema_with_ref_to_schema_with_anchor.json"));
//        JsonNode expected = jsonMapper.readTree(Paths
//                .get("./src/integration-test/resources/test-schemes/expected-result/dereferenced_schema_with_ref_to_schema_with_anchor.json").toFile());
//
//        assertEquals(expected, actual);
//    }
//
    @Test
    public void fuzTest() throws DereferenceException {
        Dereferencer dereferencer = DereferencerBuilder.builder().build();
        String HOME = System.getenv().get("HOME");

        JsonNode json1 = dereferencer.dereference(URI.create(HOME+"/Temp/schemes/fipc.yaml").normalize());
//        System.out.println(json1);

        JsonNode json2 = dereferencer.dereference(URI.create(HOME+"/Temp/service/fipc-db-service.yaml").normalize());
        System.out.println(json2);

    }
//    @Test
//    public void Test_simple_schema_With_urn_ref() throws URISyntaxException, LoadException, IOException {
//        InputStream inputStream = Dereferencer.class.getClassLoader().getResourceAsStream("config.properties");
//        Properties properties = new Properties();
//        properties.load(inputStream);
//        Dereferencer dereferencer = new Dereferencer(DereferenceConfiguration.builder().setTokens(new Tokens().setGitHubToken(properties.getProperty("github-token"))).build());
//        JsonNode actual = dereferencer.dereference(URI.create("https://github.com/dont-you/dereferencer/blob/master/src/integration-test/resources/test-schemes/schemes/urn-resolving/simple_schema_with_urn_ref.json"));
//        JsonNode expected = jsonMapper.readTree(Paths
//                .get("./src/integration-test/resources/test-schemes/expected-result/dereferenced_simple_schema_with_urn_ref.json").toFile());
//
//        assertEquals(expected, actual);
//    }
//
//
//    @Test
//    public void Test_simple_merge_scheme()
//            throws StreamReadException, DatabindException, IOException, URISyntaxException, LoadException {
//        Dereferencer dereferencer = new Dereferencer(DereferenceConfiguration.builder().setLoadingFlags(new LoadingFlag[]{LoadingFlag.MERGE_ALL_OF}).build());
//        JsonNode actual = dereferencer
//                .dereference(URI.create("./src/integration-test/resources/test-schemes/schemes/simple_merge_scheme.json"));
//        JsonNode expected = jsonMapper.readTree(
//                Paths.get("./src/integration-test/resources/test-schemes/expected-result/dereferenced_simple_merge_scheme.json")
//                        .toFile());
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void Test_simple_merge_scheme_With_refs_And_nesting()
//            throws StreamReadException, DatabindException, IOException, URISyntaxException, LoadException {
//
//        Dereferencer dereferencer = new Dereferencer(DereferenceConfiguration.builder().setLoadingFlags(new LoadingFlag[]{LoadingFlag.MERGE_ALL_OF}).build());
//        JsonNode actual = dereferencer.dereference(
//                URI.create("./src/integration-test/resources/test-schemes/schemes/simple_merge_scheme_with_refs_and_nesting.json"));
//        JsonNode expected = jsonMapper.readTree(Paths.get(
//                "./src/integration-test/resources/test-schemes/expected-result/dereferenced_simple_merge_scheme_with_refs_and_nesting.json")
//                .toFile());
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void test()
//            throws StreamReadException, DatabindException, IOException, URISyntaxException, LoadException, ExecutionException {
//        Dereferencer dereferencer = new Dereferencer(DereferenceConfiguration.builder().setLoadingFlags(new LoadingFlag[]{LoadingFlag.MERGE_ALL_OF}).build());
//        JsonNode node = jsonMapper.readTree("{" +
//                "\"$id\":\"https://example.com\","+
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
//                    "\"$id\":\"https://example.com\"," +
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
//    }

}
