package ru.fusionsoft.dereferencer.reference;

import com.fasterxml.jackson.databind.JsonNode;

public interface Reference{
    public ReferenceType getReferenceType();
    public JsonNode getSource();
}
