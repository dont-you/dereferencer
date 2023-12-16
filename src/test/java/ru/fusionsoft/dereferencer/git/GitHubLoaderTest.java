package ru.fusionsoft.dereferencer.git;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.*;

public class GitHubLoaderTest {

    private GitHubLoader gitHubLoader = new GitHubLoader();
    private ObjectMapper objectMapper = new ObjectMapper();

    public GitHubLoaderTest() throws IOException {
    }

    @Test
    public void Test_load_json_file() throws DereferenceException, IOException {
        JsonNode node = objectMapper.readTree(gitHubLoader.loadSource(new URL("https://github.com/dont-you/tests/blob/main/test.json")));
        assertEquals(node, objectMapper.readTree("\"test-complete\""));
    }
}
