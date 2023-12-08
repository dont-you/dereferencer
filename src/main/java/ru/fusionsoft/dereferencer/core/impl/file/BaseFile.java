package ru.fusionsoft.dereferencer.core.impl.file;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;

import org.apache.commons.lang3.StringUtils;
import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

public class BaseFile implements File, Comparable<BaseFile>{
    private final URI baseURI;
    private final FileRegister fileRegister;
    private final JsonNode source;
    protected final JsonNode derefedSource;
    private final Map<FragmentIdentifier, String> references;
    private final Map<String, JsonNode> anchors;
    private final Map<FragmentIdentifier, Reference> requests;
    //

    public BaseFile(FileRegister fileRegister, URI baseURI, JsonNode source){
        this.baseURI = baseURI;
        this.fileRegister = fileRegister;
        this.source = source;
        this.derefedSource = source;
        this.references = new HashMap<>();
        this.anchors = new HashMap<>();
        this.requests = new HashMap<>();
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
        resolveReferences();
    }

    protected void exploreSourceJson() throws DereferenceException{
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

    protected BaseFile getFileFromFileReg(URI targetUri) throws DereferenceException{
        if(targetUri.equals(baseURI))
            return this;
        else
            return (BaseFile) fileRegister.get(targetUri);
    }

    protected boolean resolveNode(String pathToNode, String nodeKey, JsonNode nodeValue) throws DereferenceException{
        boolean isPayLoadNode = false;
        if(nodeKey.equals("$anchor")){
            anchors.put(nodeValue.asText(),derefedSource.at(pathToNode));
            isPayLoadNode = true;
        } else if(nodeKey.equals("$ref")){
            references.put(new FragmentIdentifier(pathToNode), nodeValue.asText());
            isPayLoadNode = true;
        }

        return isPayLoadNode;
    }

    private void resolveReferences() throws DereferenceException {
        for(Entry<FragmentIdentifier, String> refEntry: references.entrySet()) {
            JsonNode dereferencedValue = MissingNode.getInstance();
            if(FragmentIdentifier.isRelativePointer(refEntry.getValue())){
                FragmentIdentifier resolvedRelative = FragmentIdentifier.resolveRelativePtr(refEntry.getKey().getPointer(), refEntry.getValue());
                if(resolvedRelative.endsWithHash()){
                    String propName = resolvedRelative.getPropertyName();
                    dereferencedValue =  StringUtils.isNumeric(propName)? IntNode.valueOf(Integer.parseInt(propName)): TextNode.valueOf(propName);
                } else {
                    Reference reference = getFragment(resolvedRelative);
                    dereferencedValue = reference.getFragment();
                }
            } else {
                try {
                    URI targetUri = baseURI.resolve(new URI(refEntry.getValue()));
                    URI absoluteUri = makeAbsoluteURI(targetUri);
                    Reference reference = getFileFromFileReg(absoluteUri).getFragment(new FragmentIdentifier(targetUri.getFragment()));
                    dereferencedValue = reference.getFragment();
                } catch (URISyntaxException e) {
                    throw new DereferenceException("ref have errors - " + refEntry.getValue());
                }

            }
            dereferenceRef(refEntry.getKey(), dereferencedValue);
        }
    }

    private void dereferenceRef(FragmentIdentifier ptrToRef, JsonNode dereferencedValue){
        ((ObjectNode) derefedSource.at(ptrToRef.getPointer())).removeAll();
        JsonNode parentNode = derefedSource.at(ptrToRef.getParentPtr().getPointer());

        if(parentNode.isObject())
            ((ObjectNode)parentNode).set(ptrToRef.getPropertyName(), dereferencedValue);
        else if(parentNode.isArray())
            ((ArrayNode)parentNode).set(Integer.parseInt(ptrToRef.getPropertyName()), dereferencedValue);
    }

    private URI makeAbsoluteURI(URI uri) throws URISyntaxException {
        return new URI(uri.getScheme(),uri.getSchemeSpecificPart(),null);
    }

    public Reference getFragment(FragmentIdentifier requestedPtr){
        Reference targetReference = requests.get(requestedPtr);

        if(targetReference==null){
            targetReference = new Reference(requestedPtr);
            requests.put(requestedPtr, targetReference);
            targetReference.setFragment(getJsonNodeByFragmentIdentifier(requestedPtr));
        }

        return targetReference;
    }

    private JsonNode getJsonNodeByFragmentIdentifier(FragmentIdentifier ptrToFragment){
        if(ptrToFragment.isAnchorPointer()){
            return anchors.get(ptrToFragment.getPlainName());
        } else {
            return derefedSource.at(ptrToFragment.getPointer());
        }
    }

    @Override
    public int compareTo(BaseFile baseFile) {
        return baseURI.compareTo(baseFile.baseURI);
    }
}
