package ru.fusionsoft.dereferencer.allOf;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.FileFactory;
import ru.fusionsoft.dereferencer.core.FileRegister;

import java.net.URI;

public class AllOfFileFactory implements FileFactory {
    @Override
    public File makeFile(FileRegister fileRegister, URI baseURI, JsonNode source) {
        return new AllOfFile(fileRegister, baseURI, source);
    }
}
