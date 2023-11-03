package ru.fusionsoft.dereferencer.core.impl.file;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.FileFactory;

import java.net.URI;

public class BaseFileFactory implements FileFactory {
    @Override
    public File makeFile(URI baseURI, JsonNode source) {
        // TODO
        return null;
    }
}
