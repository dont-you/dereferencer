package ru.fusionsoft.dereferencer.core.impl.file;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
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
    private final JsonNode derefedSource;
    private final Map<FragmentIdentifier, Reference> references;
    private final Map<String, FragmentIdentifier> anchors;
    private final Map<FragmentIdentifier, ReferenceProxy> requests;
    private boolean canResponse;

    public BaseFile(FileRegister fileRegister, URI baseURI, JsonNode source){
        this.baseURI = baseURI;
        this.fileRegister = fileRegister;
        this.source = source;
        this.derefedSource = source;
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
        anchors.put(plainName, new FragmentIdentifier(pathToNode, plainName));
    }

    private void resolveRefNode(String pathToNode, JsonNode nodeValue) throws DereferenceException {
        try {
            URI targetUri = baseURI.resolve(new URI(nodeValue.asText()));
            Reference reference = getFileFromFileReg(targetUri).getFragment(new FragmentIdentifier(targetUri.getFragment()));
            references.put(new FragmentIdentifier(pathToNode),reference);
            reference.subscribe(this);
        } catch (URISyntaxException e) {
            throw new DereferenceException("could not parse ref - " + nodeValue);
        }
    }

    public void update(Reference reference, JsonNode jsonNode){
        for(Entry<FragmentIdentifier, Reference> refEntry: references.entrySet()){
            if(refEntry.getValue().equals(reference)){
                FragmentIdentifier ptrToRef = refEntry.getKey();
                ((ObjectNode) derefedSource.at(ptrToRef.getPointer())).removeAll();
                JsonNode parentNode = derefedSource.at(ptrToRef.getParentPtr().getPointer());

                if(parentNode.isObject())
                    ((ObjectNode)parentNode).set(ptrToRef.getPropertyName(), jsonNode);
                else if(parentNode.isArray())
                    ((ArrayNode)parentNode).set(Integer.parseInt(ptrToRef.getPropertyName()), jsonNode);
                else
                    System.out.println("error");
            }
        }
    }

    public Reference getFragment(FragmentIdentifier requestedPtr){
        ReferenceProxy targetRefProxy = requests.get(requestedPtr);

        if(targetRefProxy==null){
            targetRefProxy = Reference.getReferenceProxy(requestedPtr);
            requests.put(requestedPtr, targetRefProxy);
            responseTo(targetRefProxy);
        }

        return targetRefProxy.getReference();
    }

    private void setCanResponseTrue(){
        canResponse = true;
        requests.values().forEach(this::responseTo);
    }

    private void responseTo(ReferenceProxy refProxy){
        if(!canResponse)
            return;

        FragmentIdentifier ptrToFragment = refProxy.getFragmentIdentifier().isAnchorPointer()?
                anchors.get(refProxy.getFragmentIdentifier().getPlainName()):
                refProxy.getFragmentIdentifier();

        refProxy.setFragment(derefedSource.at(ptrToFragment.getPointer()));
    }

    @Override
    public int compareTo(BaseFile baseFile) {
        return baseURI.compareTo(baseFile.baseURI);
    }
}
