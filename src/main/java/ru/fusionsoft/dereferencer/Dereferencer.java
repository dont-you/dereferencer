package ru.fusionsoft.dereferencer;

import java.io.IOException;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.fusionsoft.dereferencer.core.builders.Linker;
import ru.fusionsoft.dereferencer.core.reference.Reference;
import ru.fusionsoft.dereferencer.core.reference.factories.ReferenceFactory;
import ru.fusionsoft.dereferencer.exception.ReferenceException;

public class Dereferencer{
    public static final ObjectMapper objectMapper = new ObjectMapper();
    private static String gitHubToken = null;

    public static JsonNode dereference(String uri) throws ReferenceException, StreamReadException, DatabindException, IOException{
        Reference reference = ReferenceFactory.create(uri);
        JsonNode jsonNode = Linker.combine(reference);
        return jsonNode;
    }

    public static void setGitHubToken(String gitHubToken) {
        Dereferencer.gitHubToken = gitHubToken;
    }

    public static String getGitHubToken() {
        return gitHubToken;
    }
}
