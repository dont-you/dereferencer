package ru.fusionsoft.dereferencer.core;

import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

public class BaseDereferencer implements Dereferencer{

    @Override
    public JsonNode anonymousDereference(JsonNode node) throws DereferenceException {
        // TODO
        return null;
    }

    @Override
    public JsonNode dereference(URI uri) throws DereferenceException {
        // TODO
        return null;
    }

}
