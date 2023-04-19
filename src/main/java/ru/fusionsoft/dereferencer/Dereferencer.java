package ru.fusionsoft.dereferencer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.exception.ReferenceException;
import ru.fusionsoft.dereferencer.reference.Reference;
import ru.fusionsoft.dereferencer.reference.ReferenceFactory;

public class Dereferencer{
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
