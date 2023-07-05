package ru.fusionsoft.dereferencer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

public class DereferencerIT {

    ObjectMapper jsonMapper = new ObjectMapper();

    @Test
    public void Test_simple_scheme_With_url_reference() throws LoadException, IOException {
        Dereferencer dereferencer = new Dereferencer(DereferenceConfiguration.builder().build());
        JsonNode actual = dereferencer
            .dereference(URI.create("./src/test/resources/test-schemes/schemes/simple_scheme_with_url_ref.json"));
        JsonNode expected = jsonMapper.readTree(Paths
                                            .get("./src/test/resources/test-schemes/expected-result/dereferenced_simple_scheme_with_url_ref.json")
                                            .toFile());

        assertEquals(expected, actual);
   }

   @Test
   public void delme() throws LoadException, IOException {
        JsonNode jsonNode = jsonMapper.readTree("{\"allOf\":{}}");
        if(!jsonNode.at("/allOf").isMissingNode()){
            System.out.println("allof schema node");
        }
   }
}
