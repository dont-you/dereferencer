package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface File {
    public JsonNode getDerefedJson();
    public JsonNode getSourceNode();
    public Reference[] getReferences();
    public Map<String,JsonNode> getAnchors();
}
