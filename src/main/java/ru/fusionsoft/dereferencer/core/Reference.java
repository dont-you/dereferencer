package ru.fusionsoft.dereferencer.core;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.JsonNode;

public class Reference {
    private File handler;
    private JsonPointer jsonPointer;
    private JsonNode fragment;
    private Map<String,File> anchors;
    private Set<File> requesters;

    private Reference(File handler, JsonPointer jsonPointer){
        this.handler = handler;
        this.jsonPointer = jsonPointer;
        fragment = null;
        anchors = new TreeMap<>();
        requesters = new TreeSet<>();
    }

    public static ReferenceProxy getReferenceProxy(File handler, JsonPointer jsonPointer){
        return new ReferenceProxy(handler, jsonPointer);
    }

    public File getHandler() {
        return handler;
    }

    public JsonPointer getJsonPointer() {
        return jsonPointer;
    }

    public JsonNode getFragment() {
        return fragment;
    }

    private void setFragment(JsonNode fragment) {
        this.fragment = fragment;
    }

    public Map<String, File> getAnchors() {
        return anchors;
    }

    private void addAllAnchors(Map<String, File> anchors){
        this.anchors.putAll(anchors);
    }

    public Set<File> getRequesters() {
        return requesters;
    }

    private void addRequester(File requester) {
        requesters.add(requester);
    }

    private void redirectReference(File handler, JsonPointer jsonPointer){
        this.handler = handler;
        this.jsonPointer = this.jsonPointer.makeRedirectedPointer(jsonPointer);
    }

    static public class ReferenceProxy{
        private Reference reference;

        public ReferenceProxy(File handler, JsonPointer jsonPointer){
            reference = new Reference(handler, jsonPointer);
        }

        public void redirectReference(File handler, JsonPointer pathToHandler){
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
