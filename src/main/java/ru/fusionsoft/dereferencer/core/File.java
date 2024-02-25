package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;

public interface File {
    JsonNode getDerefedJson();

    JsonNode getSourceNode();

    URI getBaseURI();

    void resolve();
}
