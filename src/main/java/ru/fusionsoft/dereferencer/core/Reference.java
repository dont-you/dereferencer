package ru.fusionsoft.dereferencer.core;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

public class Reference {
    private File handler;
    private JsonPtr jsonPtr;
    private JsonNode fragment;
    private Map<String,File> anchors;
    private Set<File> requesters;

    private Reference(File handler, JsonPtr jsonPtr){
        this.handler = handler;
        this.jsonPtr = jsonPtr;
        fragment = null;
        anchors = new TreeMap<>();
        requesters = new TreeSet<>();
    }

    public static ReferenceProxy getReferenceProxy(File handler, JsonPtr jsonPtr){
        return new ReferenceProxy(handler, jsonPtr);
    }

    public File getHandler() {
        return handler;
    }

    public JsonPtr getJsonPointer() {
        return jsonPtr;
    }

    public JsonNode getFragment() {
        return fragment;
    }

    private void setFragment(JsonNode fragment) {
        this.fragment = fragment;
        requesters.forEach(file -> {
            try {
                file.responseToRequest(this,fragment);
            } catch (DereferenceException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Map<String, File> getAnchors() {
        return anchors;
    }

    private void addAllAnchors(Map<String, File> anchors){
        this.anchors.putAll(anchors);
        requesters.forEach(file -> {
            try {
                file.updateAnchorsFromRequest(this,anchors);
            } catch (DereferenceException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Set<File> getRequesters() {
        return requesters;
    }

    private void addRequester(File requester) {
        requesters.add(requester);
    }

    private void redirectReference(File handler, JsonPtr jsonPtr){
        this.handler = handler;
        this.jsonPtr = this.jsonPtr.makeRedirectedPointer(jsonPtr);
    }

    static public class ReferenceProxy{
        private Reference reference;

        public ReferenceProxy(File handler, JsonPtr jsonPtr){
            reference = new Reference(handler, jsonPtr);
        }

        public void redirectReference(File handler, JsonPtr pathToHandler){
            reference.redirectReference(handler, pathToHandler);
        }

        public void setFragment(JsonNode fragment) {
            reference.setFragment(fragment);
        }

        public void addAllAnchors(Map<String, File> anchors){
            reference.addAllAnchors(anchors);
        }

        public void addRequester(File requester) {
            reference.addRequester(requester);
        }

        public Reference getReference(){
            return reference;
        }
    }
}
