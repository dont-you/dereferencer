package ru.fusionsoft.dereferencer.allOf;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.FileFactory;

import java.net.URI;

public class AllOfFileFactory implements FileFactory {
    @Override
    public File makeFile(URI baseURI, JsonNode source) {
        // TODO
        return null;
    }
}
