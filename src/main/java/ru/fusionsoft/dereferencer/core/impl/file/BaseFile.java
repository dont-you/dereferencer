package ru.fusionsoft.dereferencer.core.impl.file;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.impl.file.Reference.ReferenceProxy;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

public class BaseFile implements File, Comparable<BaseFile>{
    private final URI baseURI;
    private final FileRegister fileRegister;
    private final JsonNode source;
    private JsonNode derefedSource;
    private final Map<Reference, JsonPtr> references;
    private final Map<String, JsonPtr> anchors;
    private final Map<ReferenceProxy, Boolean> requests;
    private boolean canResponse;

    public BaseFile(FileRegister fileRegister, URI baseURI, JsonNode source){
        this.baseURI = baseURI;
        this.fileRegister = fileRegister;
        this.source = source;
        this.derefedSource = null;
        this.references = new HashMap<>();
        this.anchors = new HashMap<>();
        this.requests = new HashMap<>();
        this.canResponse = false;
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
    public void dereference() throws DereferenceException {
        derefedSource = source;
        exploreSourceJson();
        setCanResponseTrue();
    }

    private void exploreSourceJson() throws DereferenceException{
        Stack<JsonNode> nodeStack = new Stack<>();
        Stack<String> pathStack = new Stack<>();
        nodeStack.push(source);
        pathStack.push("");

        while(!nodeStack.empty()){
            exploreJsonNodeFields(nodeStack.pop().fields(), pathStack.pop()).forEach((k,v) -> {
                pathStack.push(k);
                nodeStack.push(v);
            });
        }
    }

    private Map<String, JsonNode> exploreJsonNodeFields(Iterator<Entry<String, JsonNode>> fields, String currentPath) throws DereferenceException {
        Map<String, JsonNode> stackMap = new HashMap<>();
        while(fields.hasNext()){
            Entry<String, JsonNode> field = fields.next();
            String fieldKey = decodeFieldKey(field.getKey());
            String fieldPath = currentPath + "/" + fieldKey;
            JsonNode fieldValue = field.getValue();
            resolveNode(currentPath, fieldKey, fieldValue);
            stackMap.putAll(getNextFields(fieldPath, fieldValue));
        }
        return stackMap;
    }

    private Map<String, JsonNode> getNextFields(String fieldPath, JsonNode fieldValue){
        Map<String, JsonNode> stackMap = new HashMap<>();
        if (fieldValue.isArray()) {
            Iterator<JsonNode> elements = fieldValue.elements();
            int i = 0;
            while (elements.hasNext()) {
                stackMap.put(fieldPath + "/" + i++,elements.next());
            }
        } else {
            stackMap.put(fieldPath,fieldValue);
        }
        return stackMap;
    }

    private String decodeFieldKey(String fieldKey){
        if (fieldKey.contains("/"))
            return fieldKey.replaceAll("/", "~1");
        else if (fieldKey.contains("~"))
            return fieldKey.replaceAll("~", "~0");
        else
            return fieldKey;
    }

    private BaseFile getFileFromFileReg(URI targetUri) throws DereferenceException{
        return (BaseFile) fileRegister.get(targetUri);
    }

    protected void resolveNode(String pathToNode, String nodeKey, JsonNode nodeValue) throws DereferenceException{
        if(nodeKey.equals("$anchor")){
            resolveAnchorNode(pathToNode, nodeValue);
        } else if(nodeKey.equals("$ref")){
            resolveRefNode(pathToNode, nodeValue);
        }
    }

    private void resolveAnchorNode(String pathToNode, JsonNode nodeValue){
        String plainName = nodeValue.asText();
        anchors.put(plainName, new JsonPtr(pathToNode, plainName));
    }

    private void resolveRefNode(String pathToNode, JsonNode nodeValue) throws DereferenceException {
        try {
            URI targetUri = baseURI.resolve(new URI(nodeValue.asText()));
            Reference reference = getFileFromFileReg(targetUri).getFragment(new JsonPtr(targetUri.getFragment()));
            references.put(reference, new JsonPtr(pathToNode));
            reference.subscribe(this);
        } catch (URISyntaxException e) {
            throw new DereferenceException("could not parse ref - " + nodeValue);
        }
    }

    public void update(Reference reference, JsonNode jsonNode) throws DereferenceException {
        JsonPtr ptrToRef = references.get(reference);
        ((ObjectNode) derefedSource.at(ptrToRef.getPointer())).removeAll();
        JsonNode parentNode = derefedSource.at(ptrToRef.getParentPtr().getPointer());
        if(parentNode.isObject())
            ((ObjectNode)parentNode).set(ptrToRef.getPropertyName(), jsonNode);
        else if(parentNode.isArray())
            ((ArrayNode)parentNode).set(Integer.parseInt(ptrToRef.getPropertyName()), jsonNode);
    }

    public Reference getFragment(JsonPtr requestedPtr) throws DereferenceException {
        ReferenceProxy targetRefProxy = requests.keySet().stream()
                .filter(requestRefProxy -> requestRefProxy.getJsonPtr().equals(requestedPtr)).findAny().orElse(null);

        if(targetRefProxy==null){
            targetRefProxy = new ReferenceProxy(this, requestedPtr);
            requests.put(targetRefProxy, false);
            responseTo(targetRefProxy);
        }

        return targetRefProxy.getReference();
    }

    private void setCanResponseTrue() throws DereferenceException{
        canResponse = true;
        for(Entry<ReferenceProxy, Boolean> reqEntry: requests.entrySet()){
            if(!reqEntry.getValue())
                responseTo(reqEntry.getKey());
        }
    }

    private void responseTo(ReferenceProxy refProxy) throws DereferenceException{
        if(!canResponse)
            return;

        if(refProxy.getJsonPtr().isAnchorPointer()){
            responseToAnchorRef(refProxy);
        } else {
            responseToPointerRef(refProxy);
        }
    }

    private void responseToPointerRef(ReferenceProxy refProxy) throws DereferenceException{
        JsonPtr ptrToFragment = refProxy.getJsonPtr();
        for(Entry<Reference, JsonPtr> refEntry: references.entrySet()){
            if(refEntry.getValue().isSupSetTo(ptrToFragment)){
                redirectReference(refProxy, refEntry.getKey());
                return;
            }
        }

        JsonNode fragmentNode = derefedSource.at(ptrToFragment.getPointer());
        resolveReference(refProxy, ptrToFragment, fragmentNode);
    }

    private void responseToAnchorRef(ReferenceProxy refProxy) throws DereferenceException{
        JsonPtr ptrToAnchor = anchors.get(refProxy.getJsonPtr().getPlainName());
        if(ptrToAnchor==null){
            for(Reference supposedGatewayRef: references.keySet()){
                if(supposedGatewayRef.getAnchors().containsKey(refProxy.getJsonPtr().getPlainName())){
                    redirectReference(refProxy, supposedGatewayRef);
                    break;
                }
            }
        } else {
            resolveReference(refProxy, ptrToAnchor, derefedSource.get(ptrToAnchor.getPointer()));
        }
    }

    private void resolveReference(ReferenceProxy refProxy, JsonPtr ptrToFragment, JsonNode fragmentNode) throws DereferenceException{
        requests.replace(refProxy, true);
        refProxy.addAllAnchors(getAssociatedAnchors(ptrToFragment));
        refProxy.setFragment(fragmentNode);
    }
    private void redirectReference(ReferenceProxy targetRef, Reference gatewayRef) throws DereferenceException{
        BaseFile gatewayHost=gatewayRef.getHandler();
        JsonPtr targetPtr=targetRef.getJsonPtr(), gatewayPtr=gatewayRef.getJsonPtr();

        if(isCantRedirectNow(targetPtr, gatewayPtr))
            return;

        if(!targetPtr.isAnchorPointer())
            targetPtr = JsonPtr.makeRedirectedPointer(targetPtr, references.get(gatewayRef), gatewayPtr);

        targetRef.setHandler(gatewayHost);
        targetRef.setJsonPtr(targetPtr);
        gatewayHost.requests.put(targetRef, false);
        gatewayHost.responseTo(targetRef);
    }

    private boolean isCantRedirectNow(JsonPtr targetPtr, JsonPtr gatewayPtr){
        return !targetPtr.isAnchorPointer() && gatewayPtr.isAnchorPointer() && gatewayPtr.getPointer() == null;
    }

    public void update(Reference reference, Map<String, BaseFile> updatedAnchors) throws DereferenceException {
        if(references.containsKey(reference)){
            List<ReferenceProxy> notResolvedAnchorReqs =  requests.entrySet().stream()
                .filter(reqEntry -> !reqEntry.getValue() && reqEntry.getKey().getJsonPtr().isAnchorPointer())
                .map(Map.Entry::getKey)
                .toList();

            for(ReferenceProxy reqRefProxy: notResolvedAnchorReqs){
                if(updatedAnchors.containsKey(reqRefProxy.getJsonPtr().getPlainName())){
                    redirectReference(reqRefProxy, reference);
                    requests.replace(reqRefProxy, true);
                }
            }

            List<ReferenceProxy> associatedReqs = requests.keySet().stream()
                .filter(reqRefProxy -> reqRefProxy.getJsonPtr().isSupSetTo(reference.getJsonPtr()))
                .toList();

            for(ReferenceProxy reqRefProxy: associatedReqs){
                reqRefProxy.addAllAnchors(updatedAnchors);
            }
        } else {
            throw new DereferenceException("internal error while constructing schema graph");
        }
    }

    private Map<String, BaseFile> getAssociatedAnchors(JsonPtr targetPointer){
        Map<String, BaseFile> result = new HashMap<>();
        anchors.forEach((plainName, pointerToAnchor) -> {
                if(targetPointer.isSupSetTo(pointerToAnchor))
                    result.put(plainName, this);
            });

        getAssociatedReferences(targetPointer).forEach(reference-> result.putAll(reference.getAnchors()));
        return result;
    }

    private List<Reference> getAssociatedReferences(JsonPtr targetPointer){
        List<Reference> result = new ArrayList<>();
        references.forEach((reference, pointerToReference) -> {
                if(targetPointer.isSupSetTo(pointerToReference))
                    result.add(reference);
            });

        return result;
    }

    @Override
    public int compareTo(BaseFile baseFile) {
        return this.baseURI.compareTo(baseFile.baseURI);
    }
}
