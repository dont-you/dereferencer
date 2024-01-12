package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;

public interface File {
    JsonNode getDerefedJson();

    JsonNode getSourceNode();

    void resolve();
}
