package ru.fusionsoft.dereferencer;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Base64;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;

import ru.fusionsoft.dereferencer.core.reference.factories.ReferenceFactory;
import ru.fusionsoft.dereferencer.core.reference.impl.external.GitHubReference;
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
//
    @Test
    public void delme() throws StreamReadException, DatabindException, ReferenceException, IOException, URISyntaxException{
    }

    @Test
    public void Test_complex_scheme_With_nesting() throws StreamReadException, DatabindException, ReferenceException, IOException{
        JsonNode actual = Dereferencer.dereference("./src/test/resources/test-schemes/schemes/complex-scheme/layer_1_scheme_1.json");
        JsonNode expected = mapper.readTree(Paths.get("./src/test/resources/test-schemes/expected-result/dereferenced_complex_scheme.json").toFile());

        assertEquals(expected, actual);
    }
}
