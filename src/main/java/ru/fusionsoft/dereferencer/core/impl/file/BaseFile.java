package ru.fusionsoft.dereferencer.core.impl.file;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.Reference;

import java.net.URI;
import java.util.Map;

public class BaseFile implements File {

    public BaseFile(FileRegister fileRegister, URI baseURI, JsonNode source){
        // TODO
    }

    @Override
    public JsonNode getDerefedJson() {
        // TODO
        return null;
    }

    @Override
    public JsonNode getSourceNode() {
        // TODO
        return null;
    }

    @Override
    public Reference[] getReferences() {
        // TODO
        return new Reference[0];
    }

    @Override
    public Map<String, JsonNode> getAnchors() {
        // TODO
        return null;
    }

    @Override
    public void dereference() {
        // TODO
    }

    protected void resolveNode(String path, JsonNode node){
        // TODO
    }
}
