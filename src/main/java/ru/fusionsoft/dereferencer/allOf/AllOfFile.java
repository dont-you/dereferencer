package ru.fusionsoft.dereferencer.allOf;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.impl.file.BaseFile;

import java.net.URI;

public class AllOfFile extends BaseFile{
    public AllOfFile(FileRegister fileRegister, URI baseURI, JsonNode source) {
        super(fileRegister, baseURI, source);
    }
}
