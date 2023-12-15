package ru.fusionsoft.dereferencer.core.impl.load;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class FileLoaderTest {

    private FileLoader fileLoader = new FileLoader();
    private ObjectMapper objectMapper = new ObjectMapper();
//    @Test
//    public void Test_load_json_file() throws DereferenceException, JsonProcessingException {
//        JsonNode node = fileLoader.loadSource(Paths.get("./src/test/resources/test_load_json.json").normalize().toAbsolutePath().toUri());
//        assertEquals(node, objectMapper.readTree("\"json\""));
//    }
//
//    @Test
//    public void Test_load_yaml_file() throws DereferenceException, JsonProcessingException {
//        JsonNode node = fileLoader.loadSource(Paths.get("./src/test/resources/test_load_yaml.yaml").normalize().toAbsolutePath().toUri());
//        assertEquals(node, objectMapper.readTree("\"yaml\""));
//    }
}
