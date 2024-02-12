package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;

public interface FileFactory {
    File makeFile(FileRegister fileRegister, URI retrievalURI, JsonNode source) throws DereferenceException;
}
