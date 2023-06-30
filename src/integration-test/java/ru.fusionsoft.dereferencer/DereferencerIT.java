package ru.fusionsoft.dereferencer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

public class DereferencerIT {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void Test_simple_scheme_With_url_reference() throws DereferenceException, IOException {
        Dereferencer dereferencer = new Dereferencer(DereferenceConfiguration.builder().build());
        JsonNode actual = dereferencer
            .dereference(URI.create("./src/test/resources/test-schemes/schemes/simple_scheme_with_url_ref.json"));
        JsonNode expected = mapper.readTree(Paths
                                            .get("./src/test/resources/test-schemes/expected-result/dereferenced_simple_scheme_with_url_ref.json")
                                            .toFile());

        assertEquals(expected, actual);
   }
}
