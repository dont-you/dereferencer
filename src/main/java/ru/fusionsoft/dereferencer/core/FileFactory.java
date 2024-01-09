package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;

public interface FileFactory {
    File makeFile(FileRegister fileRegister, URI baseURI, JsonNode source);
}
