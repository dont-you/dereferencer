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

    public JsonNode getFragment() {
        return fragment;
    }

    public void subscribe(BaseFile subscriber){
        requesters.add(subscriber);
    }

    public Map<String, BaseFile> getAnchors() {
        return anchors;
    }

    public Set<BaseFile> getRequesters() {
        return requesters;
    }

    public JsonPtr getJsonPtr() {
        return jsonPtr;
    }

    static public class ReferenceProxy{
        private Reference reference;

        public ReferenceProxy(BaseFile handler, JsonPtr jsonPtr){
            reference = new Reference(handler, jsonPtr);
        }

        public void setHandler(BaseFile handler){
            reference.handler = handler;
        }

        public void setFragment(JsonNode fragment) throws DereferenceException {
            reference.fragment = fragment;
            for(BaseFile requester: reference.requesters){
                requester.responseToRequest(reference, fragment);
            }
        }

        public void addAllAnchors(Map<String, BaseFile> anchors) throws DereferenceException{
            Map<String, BaseFile> notPresent = getNotPresentAnchors(anchors);
            if(!notPresent.isEmpty()){
                reference.anchors.putAll(notPresent);
                for(BaseFile requester: reference.requesters){
                    requester.updateAnchorsFromRequest(reference, anchors);
                }
            }
        }

        private Map<String, BaseFile> getNotPresentAnchors(Map<String, BaseFile> anchors){
            Map<String, BaseFile> notPresent = new HashMap<>();
            for(Map.Entry<String, BaseFile> anchor: anchors.entrySet()){
                if(!reference.anchors.containsKey(anchor.getKey())){
                    notPresent.put(anchor.getKey(), anchor.getValue());
                }
            }
            return notPresent;
        }

        public void addRequester(BaseFile requester) {
            reference.requesters.add(requester);
        }

        public void setJsonPtr(JsonPtr jsonPtr) {
            reference.jsonPtr = jsonPtr;
        }

        public Reference getReference(){
            return reference;
        }

        public JsonPtr getJsonPtr(){
            return reference.getJsonPtr();
        }
    }
}
