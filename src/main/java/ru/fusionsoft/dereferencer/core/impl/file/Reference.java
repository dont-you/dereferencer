package ru.fusionsoft.dereferencer.core.impl.file;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

public class Reference {
    private BaseFile handler;
    private JsonPtr jsonPtr;
    private JsonNode fragment;
    private final Map<String, BaseFile> anchors;
    private final Set<BaseFile> requesters;

    private Reference(BaseFile handler, JsonPtr jsonPtr){
        this.handler = handler;
        this.jsonPtr = jsonPtr;
        fragment = null;
        anchors = new HashMap<>();
        requesters = new TreeSet<>();
    }

    public static ReferenceProxy getReferenceProxy(BaseFile handler, JsonPtr jsonPtr){
        return new ReferenceProxy(handler, jsonPtr);
    }

    public BaseFile getHandler() {
        return handler;
    }

    public JsonPtr getJsonPointer() {
        return jsonPtr;
    }

    public JsonNode getFragment() {
        return fragment;
    }

    private void setFragment(JsonNode fragment) throws DereferenceException {
        this.fragment = fragment;
        for(BaseFile requester: requesters){
            requester.responseToRequest(this, fragment);
        }
    }

    public void subscribe(BaseFile subscriber){
        requesters.add(subscriber);
    }

    public Map<String, BaseFile> getAnchors() {
        return anchors;
    }

    private void addAllAnchors(Map<String, BaseFile> anchors) throws DereferenceException{
        this.anchors.putAll(anchors);
        for(BaseFile requester: requesters){
            requester.updateAnchorsFromRequest(this, anchors);
        }
    }

    public Set<BaseFile> getRequesters() {
        return requesters;
    }

    private JsonPtr getJsonPtr() {
        return jsonPtr;
    }

    private void addRequester(BaseFile requester) {
        requesters.add(requester);
    }

    private void redirectReference(BaseFile handler, JsonPtr jsonPtr){
        this.handler = handler;
        if(!this.jsonPtr.isAnchorPointer())
            this.jsonPtr = this.jsonPtr.makeRedirectedPointer(jsonPtr);
    }

    static public class ReferenceProxy{
        private Reference reference;

        public ReferenceProxy(BaseFile handler, JsonPtr jsonPtr){
            reference = new Reference(handler, jsonPtr);
        }

        public void redirectReference(BaseFile handler, JsonPtr pathToHandler) throws DereferenceException{
            reference.redirectReference(handler, pathToHandler);
        }

        public void setFragment(JsonNode fragment) throws DereferenceException {
            reference.setFragment(fragment);
        }

        public void addAllAnchors(Map<String, BaseFile> anchors) throws DereferenceException{
            reference.addAllAnchors(anchors);
        }

        public void addRequester(BaseFile requester) {
            reference.addRequester(requester);
        }

        public Reference getReference(){
            return reference;
        }

        public JsonPtr getJsonPtr(){
            return reference.getJsonPtr();
        }
    }
}
