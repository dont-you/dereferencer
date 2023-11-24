package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

public interface File {
    JsonNode getDerefedJson() throws DereferenceException;
    JsonNode getSourceNode() throws DereferenceException;
    void dereference() throws DereferenceException;
}
