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
        this.canResponse = true;
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
        JsonNode currentNode;
        String currentPath;
        Stack<JsonNode> nodeStack = new Stack<>();
        Stack<String> pathStack = new Stack<>();
        nodeStack.push(source);
        pathStack.push("");

        while(!nodeStack.empty()){
            currentNode = nodeStack.pop();
            currentPath = pathStack.pop();
            Iterator<Entry<String, JsonNode>> fields = currentNode.fields();
            while(fields.hasNext()){
                Entry<String, JsonNode> field = fields.next();
                String fieldKey = field.getKey();
                String fieldPath = currentPath + "/" + fieldKey;
                JsonNode fieldValue = field.getValue();

                fieldKey = decodeFieldKey(fieldKey);
                resolveNode(currentPath, fieldKey, fieldValue);

                if (fieldValue.isArray()) {
                    Iterator<JsonNode> elements = field.getValue().elements();
                    int i = 0;
                    while (elements.hasNext()) {
                        nodeStack.push(elements.next());
                        pathStack.push(fieldPath + "/" + i++);
                    }
                } else {
                    nodeStack.push(fieldValue);
                    pathStack.push(fieldPath);
                }

            }
        }
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
            String plainName = nodeValue.asText();
            anchors.put(plainName, new JsonPtr(pathToNode, plainName));
        } else if(nodeKey.equals("$ref")){
            URI targetUri;
            try {
                targetUri = baseURI.relativize(new URI(nodeValue.toString()));
            } catch (URISyntaxException e) {
                throw new DereferenceException("could not parse ref - " + nodeValue);
            }
            references.put(getFileFromFileReg(targetUri).getFragment(new JsonPtr(targetUri.getFragment())),
                    new JsonPtr(pathToNode));
        }
    }
    public void responseToRequest(Reference reference, JsonNode jsonNode) throws DereferenceException {
        JsonPtr ptrToRef = references.get(reference);
        ((ObjectNode) derefedSource.at(ptrToRef.getPointer())).removeAll();
        ((ObjectNode) derefedSource.at(ptrToRef.getParentPtr().getPointer())).set(ptrToRef.getPropertyName(), jsonNode);
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
        JsonNode fragmentNode = derefedSource.get(ptrToFragment.getPointer());
        if(fragmentNode.isMissingNode()){
            for(Entry<Reference, JsonPtr> refEntry: references.entrySet()){
                if(refEntry.getValue().isSupSetTo(ptrToFragment)){
                    delegateReference(refEntry.getKey().getHandler(), refProxy, refEntry.getValue());
                    break;
                }
            }
        } else {
            resolveReference(refProxy, ptrToFragment, fragmentNode);
        }
    }

    private void responseToAnchorRef(ReferenceProxy refProxy) throws DereferenceException{
        JsonPtr ptrToAnchor = anchors.get(refProxy.getJsonPtr().getPlainName());
        if(ptrToAnchor==null){
            for(Reference ref: references.keySet()){
                BaseFile anchorHost = ref.getAnchors().get(refProxy.getJsonPtr().getPlainName());
                if(anchorHost!=null){
                    delegateReference(anchorHost, refProxy, null);
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

    private void delegateReference(BaseFile delegate, ReferenceProxy refProxy, JsonPtr pointerToGateWay) throws DereferenceException{
        requests.replace(refProxy, true);
        refProxy.redirectReference(delegate, pointerToGateWay);
        delegate.redirectReference(refProxy);
    }

    public void redirectReference(ReferenceProxy referenceProxy) throws DereferenceException {
        requests.put(referenceProxy, false);
        responseTo(referenceProxy);
    }

    public void updateAnchorsFromRequest(Reference reference, Map<String, BaseFile> updatedAnchors) throws DereferenceException {
        if(references.containsKey(reference)){
            List<ReferenceProxy> notResolvedAnchorReqs =  requests.entrySet().stream()
                .filter(reqEntry -> !reqEntry.getValue() && reqEntry.getKey().getJsonPtr().isAnchorPointer())
                .map(Map.Entry::getKey)
                .toList();

            for(ReferenceProxy reqRefProxy: notResolvedAnchorReqs){
                BaseFile delegate = updatedAnchors.get(reqRefProxy.getJsonPtr().getPlainName());
                if(delegate!=null){
                    delegateReference(delegate, reqRefProxy, null);
                    requests.replace(reqRefProxy, true);
                }
            }

            List<ReferenceProxy> associatedReqs = requests.keySet().stream()
                .filter(reqRefProxy -> reqRefProxy.getJsonPtr().isSupSetTo(reference.getJsonPointer()))
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
