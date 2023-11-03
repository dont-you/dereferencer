package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;

public interface FileFactory {
    public File makeFile(URI baseURI, JsonNode source);
}
