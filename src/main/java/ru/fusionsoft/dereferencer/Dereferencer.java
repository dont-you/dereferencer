package ru.fusionsoft.dereferencer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.fusionsoft.dereferencer.core.builders.Linker;
import ru.fusionsoft.dereferencer.core.reference.Reference;
import ru.fusionsoft.dereferencer.core.reference.factories.ReferenceFactory;
import ru.fusionsoft.dereferencer.exception.ReferenceException;

public class Dereferencer {
    public static final ObjectMapper objectMapper = new ObjectMapper();
    private static String gitHubToken = null;
    private static Properties properties = null;
    private static Logger logger = Logger.getGlobal();

    public static JsonNode dereference(String uri)
            throws ReferenceException, StreamReadException, DatabindException, IOException {
        logger.info("start document dereferencing at uri - '" + uri + "'");
        if(properties==null){
            properties = new Properties();
            InputStream inputStream = Dereferencer.class.getClassLoader().getResourceAsStream("config.properties");
            properties.load(inputStream);
        }
        Reference reference = ReferenceFactory.create(uri);
        JsonNode jsonNode = Linker.combine(reference);
        logger.info("end document dereferencing at uri - '" + uri + "'");
        return jsonNode;
    }

    public static void setGitHubToken(String gitHubToken) {
        Dereferencer.gitHubToken = gitHubToken;
    }

    public static String getGitHubToken() {
        return gitHubToken;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        Dereferencer.logger = logger;
    }
}
