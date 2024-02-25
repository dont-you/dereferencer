package ru.fusionsoft.dereferencer.allOf;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.file.BaseFileFactory;

import java.net.URI;

public class AllOfFileFactory extends BaseFileFactory {
    @Override
    protected File makeFileInstance(FileRegister fileRegister, URI baseURI, JsonNode source) {
        return new AllOfFile(fileRegister, baseURI, source);
    }
}
