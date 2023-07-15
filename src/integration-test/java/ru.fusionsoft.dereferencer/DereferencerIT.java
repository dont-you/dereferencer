package ru.fusionsoft.dereferencer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.tika.Tika;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DereferencerIT {

    ObjectMapper jsonMapper = new ObjectMapper();

    @Test
    public void Test_simple_scheme_With_url_reference() throws LoadException, IOException {
        Dereferencer dereferencer = new Dereferencer(DereferenceConfiguration.builder().build());
        JsonNode actual = dereferencer
            .dereference(URI.create("./src/integration-test/resources/test-schemes/schemes/simple_scheme_with_url_ref.json"));
        JsonNode expected = jsonMapper.readTree(Paths
                                            .get("./src/integration-test/resources/test-schemes/expected-result/dereferenced_simple_scheme_with_url_ref.json")
                                            .toFile());

        assertEquals(expected, actual);
   }
   @Test
   public void Test_simple_scheme_With_local_And_remote_references()
           throws IOException, LoadException {
        Dereferencer dereferencer = new Dereferencer(DereferenceConfiguration.builder().build());
        JsonNode actual = dereferencer.dereference(URI.create("./src/integration-test/resources/test-schemes/schemes/simple_scheme.json"));
        JsonNode expected = jsonMapper.readTree(Paths
                .get("./src/integration-test/resources/test-schemes/expected-result/dereferenced_simple_scheme.json").toFile());

        assertEquals(expected, actual);
    }

    @Test
   public void Test_simple_scheme_With_plain_name_fragment_reference()
           throws IOException, LoadException {
        Dereferencer dereferencer = new Dereferencer(DereferenceConfiguration.builder().build());
        JsonNode actual = dereferencer.dereference(URI.create("./src/integration-test/resources/test-schemes/schemes/simple_scheme.json"));
        JsonNode expected = jsonMapper.readTree(Paths
                .get("./src/integration-test/resources/test-schemes/expected-result/dereferenced_simple_scheme.json").toFile());

        assertEquals(expected, actual);
    }

    @Test
    public void Test_schema_With_ref_To_schema_With_Ancor() throws LoadException, IOException {
        Dereferencer dereferencer = new Dereferencer(DereferenceConfiguration.builder().build());
        JsonNode actual = dereferencer.dereference(URI.create("./src/integration-test/resources/test-schemes/schemes/schema_with_ref_to_schema_with_anchor.json"));
        JsonNode expected = jsonMapper.readTree(Paths
                .get("./src/integration-test/resources/test-schemes/expected-result/dereferenced_schema_with_ref_to_schema_with_anchor.json").toFile());

//        assertEquals(expected, actual);
        System.out.println(actual);
    }

    @Test
    public void delme() throws LoadException, IOException {
        Dereferencer dereferencer = new Dereferencer(DereferenceConfiguration.builder().build());
        String HOME = System.getenv().get("HOME");

        JsonNode json1 = dereferencer.dereference(URI.create(HOME+"/Temp/schemes/fipc.yaml").normalize());
        System.out.println(json1);

        JsonNode json2 = dereferencer.dereference(URI.create(HOME+"/Temp/service/fipc-db-service.yaml").normalize());
        System.out.println(json2);
    }


}
