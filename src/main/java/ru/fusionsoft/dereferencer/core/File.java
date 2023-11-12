package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface File {
    public JsonNode getDerefedJson();
    public JsonNode getSourceNode();
    public Reference[] getReferences();
    void dereference();
    Reference getFragment(JsonPointer jsonPointer);
    void redirectReference(Reference.ReferenceProxy referenceProxy);
    void updateReferenceInfo(Reference reference);
}
