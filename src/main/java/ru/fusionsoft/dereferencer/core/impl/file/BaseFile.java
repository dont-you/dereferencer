package ru.fusionsoft.dereferencer.core.impl.file;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.JsonPointer;
import ru.fusionsoft.dereferencer.core.Reference;

import java.net.URI;
import java.util.Map;
import java.util.TreeMap;

public class BaseFile implements File {
    private URI baseURI;
    private FileRegister fileRegister;
    private JsonNode source;
    private JsonNode derefedSource;
    private Map<Reference, JsonPointer> references;
    private ResponseManager responseManager;

    public BaseFile(FileRegister fileRegister, URI baseURI, JsonNode source){
        this.baseURI = baseURI;
        this.fileRegister = fileRegister;
        this.source = source;
        this.derefedSource = null;
        this.references = new TreeMap<>();
        this.responseManager = new ResponseManager();
    }

    @Override
    public JsonNode getDerefedJson() {
        return derefedSource;
    }

    @Override
    public JsonNode getSourceNode() {
        return source;
    }

    @Override
    public Reference[] getReferences() {
        return (Reference[]) references.keySet().toArray();
    }

    @Override
    public void dereference() {

    }

    @Override
    public Reference getFragment(JsonPointer jsonPointer) {
        return null;
    }

    @Override
    public void redirectReference(Reference.ReferenceProxy referenceProxy) {

    }

    @Override
    public void updateReferenceInfo(Reference reference) {

    }

    protected void resolveNode(String path, JsonNode node){
        // TODO
    }

    class ResponseManager{

    }
}
