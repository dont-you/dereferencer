package ru.fusionsoft.dereferencer.core;

import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.JsonNode;

public class Reference {
    private File handler;
    private JsonPointer jsonPointer;
    private JsonNode fragment;
    private Set<Anchor> anchors;
    private Set<File> requesters;

    private Reference(File handler){
        this.handler = handler;
        jsonPointer = null;
        fragment = null;
        anchors = new TreeSet<>();
        requesters = new TreeSet<>();
    }

    public static ReferenceProxy getReferenceProxy(File handler){
        return new ReferenceProxy(handler);
    }

    public File getHandler() {
        return handler;
    }

    public JsonPointer getJsonPointer() {
        return jsonPointer;
    }

    private void setJsonPointer(JsonPointer jsonPointer) {
        this.jsonPointer = jsonPointer;
    }

    public JsonNode getFragment() {
        return fragment;
    }

    private void setFragment(JsonNode fragment) {
        this.fragment = fragment;
    }

    public Set<Anchor> getAnchors() {
        return anchors;
    }

    private void addAllAnchors(Set<Anchor> anchors){
        this.anchors.addAll(anchors);
    }

    public Set<File> getRequesters() {
        return requesters;
    }

    private void addRequester(File requester) {
        requesters.add(requester);
    }

    static public class ReferenceProxy{
        private Reference reference;

        public ReferenceProxy(File handler){
            reference = new Reference(handler);
        }

        public void setJsonPointer(JsonPointer jsonPointer) {
            reference.setJsonPointer(jsonPointer);
        }

        public void setFragment(JsonNode fragment) {
            reference.setFragment(fragment);
        }

        public void addAllAnchors(Set<Anchor> anchors){
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
