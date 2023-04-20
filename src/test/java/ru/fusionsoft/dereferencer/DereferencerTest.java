package ru.fusionsoft.dereferencer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import ru.fusionsoft.dereferencer.exception.ReferenceException;

public class DereferencerTest{
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void Test_simple_scheme_With_local_And_remote_references() throws StreamReadException, DatabindException, ReferenceException, IOException{
        JsonNode actual = Dereferencer.dereference("./src/test/resources/test-schemes/schemes/simple_scheme.json");
        JsonNode expected = mapper.readTree(Paths.get("./src/test/resources/test-schemes/expected-result/dereferenced_simple_scheme.json").toFile());

        assertEquals(expected, actual);
    }

    @Test
    public void Test_simple_scheme_With_url_reference() throws StreamReadException, DatabindException, ReferenceException, IOException, URISyntaxException{
        JsonNode actual = Dereferencer.dereference("./src/test/resources/test-schemes/schemes/simple_scheme_with_url_ref.json");
        JsonNode expected = mapper.readTree(Paths.get("./src/test/resources/test-schemes/expected-result/dereferenced_simple_scheme_with_url_ref.json").toFile());

        assertEquals(expected, actual);
    }
}