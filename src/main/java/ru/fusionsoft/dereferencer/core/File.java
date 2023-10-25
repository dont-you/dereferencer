package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.ref.ReferenceManager;

public class File {
    protected File(AbsoluteURI absoluteURI, JsonNode sourceNode, ReferenceManager referenceManager){
        // TODO
    }

    protected void resolveNode(String path, JsonNode node){
        // TODO
    }

    public JsonNode getDerefedJson(){
        // TODO
        return null;
    }
}
