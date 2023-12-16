package ru.fusionsoft.dereferencer.git;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class GitLabLoaderTest {

    private GitLabLoader gitLabLoader = new GitLabLoader();
    private ObjectMapper objectMapper = new ObjectMapper();
    @Test
    public void Test_load_json_file() throws DereferenceException, IOException {
        JsonNode node = objectMapper.readTree(gitLabLoader.loadSource(new URL("https://gitlab.com/dont-you/git-tests/-/blob/main/test.json")));
        assertEquals(node, objectMapper.readTree("\"test-complete\""));
    }
}
