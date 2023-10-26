package ru.fusionsoft.dereferencer;

import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

public interface Dereferencer {
    public JsonNode dereference(URI uri) throws DereferenceException;

    public JsonNode anonymousDereference(JsonNode node) throws DereferenceException;
}
