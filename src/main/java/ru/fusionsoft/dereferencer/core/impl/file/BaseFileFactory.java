package ru.fusionsoft.dereferencer.core.impl.file;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.FileFactory;
import ru.fusionsoft.dereferencer.core.FileRegister;

import java.net.URI;

public class BaseFileFactory implements FileFactory {
    @Override
    public File makeFile(FileRegister fileRegister, URI baseURI, JsonNode source) {
        return new BaseFile(fileRegister,baseURI,source);
    }
}
