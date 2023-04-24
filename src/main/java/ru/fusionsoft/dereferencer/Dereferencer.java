package ru.fusionsoft.dereferencer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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

    public static JsonNode dereference(String uri) throws ReferenceException, StreamReadException, DatabindException, IOException{
        try {
            Reference reference = ReferenceFactory.create(new URI(uri));
            Linker linker = new Linker();
            JsonNode jsonNode = linker.combine(reference);
            return jsonNode;
        } catch (URISyntaxException e) {
            throw new ReferenceException(e.getMessage());
        }

    }
}
