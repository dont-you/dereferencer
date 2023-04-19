package ru.fusionsoft.dereferencer.reference.impl;

import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.fusionsoft.dereferencer.reference.Reference;
import ru.fusionsoft.dereferencer.reference.ReferenceType;

public class URLReference implements Reference{
    private URI uri;

    public URLReference(URI uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public ReferenceType getReferenceType() {
        return null;
    }

    @Override
    public JsonNode getSource() {
        return null;
    }
}
