package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

public interface File {
    JsonNode getDerefedJson();

    JsonNode getSourceNode();

    void dereference();
}
