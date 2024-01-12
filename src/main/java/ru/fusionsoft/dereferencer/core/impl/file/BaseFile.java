package ru.fusionsoft.dereferencer.core.impl.file;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;

import org.apache.commons.lang3.StringUtils;
import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceRuntimeException;

public class BaseFile implements File, Comparable<BaseFile> {
    protected final URI baseURI;
    protected final FileRegister fileRegister;
    protected final JsonNode source;
    protected final JsonNode derefedSource;
    protected final Map<FragmentIdentifier, String> references;
    protected final Map<String, JsonNode> anchors;
    protected final Map<FragmentIdentifier, Reference> requests;

    public BaseFile(FileRegister fileRegister, URI baseURI, JsonNode source) {
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
    final public void resolve(){
        beforeResolvingHook();
        exploreSource("", source);
        dereference();
        afterResolvingHook();
    }

    protected void beforeResolvingHook(){}

    final protected void exploreSource(String pathToSource, JsonNode source){
        source.fields().forEachRemaining(field -> resolveNode(pathToSource, decodeFieldKey(field.getKey()),field.getValue()));
    }

    private String decodeFieldKey(String fieldKey) {
        if (fieldKey.contains("/"))
            return fieldKey.replaceAll("/", "~1");
        else if (fieldKey.contains("~"))
            return fieldKey.replaceAll("~", "~0");
        else
            return fieldKey;
    }
    protected void resolveNode(String pathToNode, String nodeKey, JsonNode nodeValue){
        if (nodeKey.equals("$anchor")) {
            anchors.put(nodeValue.asText(), derefedSource.at(pathToNode));
        } else if (nodeKey.equals("$ref")) {
            references.put(new FragmentIdentifier(pathToNode), nodeValue.asText());
        } else if(nodeValue.isArray()) {
            String currentPath = pathToNode.concat("/").concat(nodeKey);
            IntStream.range(0,nodeValue.size())
                    .forEach(i -> exploreSource(currentPath.concat("/").concat(String.valueOf(i)),nodeValue.get(i)));
        } else if(nodeValue.isObject()){
            exploreSource(pathToNode.concat("/").concat(nodeKey), nodeValue);
        }
    }

    final protected void dereference(){
        // TODO refactor
        resolveReferences();
    }

    protected void afterResolvingHook(){}

    protected BaseFile getFileFromFileReg(URI targetUri) throws DereferenceException {
        if (targetUri.equals(baseURI))
            return this;
        else
            return (BaseFile) fileRegister.get(targetUri);
    }

    private void resolveReferences(){
        for (Entry<FragmentIdentifier, String> refEntry : references.entrySet()) {
            JsonNode dereferencedValue;
            try{
                if (FragmentIdentifier.isRelativePointer(refEntry.getValue())) {
                    FragmentIdentifier resolvedRelative = FragmentIdentifier
                        .resolveRelativePtr(refEntry.getKey().getPointer(), refEntry.getValue());
                    if (resolvedRelative.endsWithHash()) {
                        String propName = resolvedRelative.getPropertyName();
                        dereferencedValue = StringUtils.isNumeric(propName) ? IntNode.valueOf(Integer.parseInt(propName))
                            : TextNode.valueOf(propName);
                    } else {
                        Reference reference = getFragment(resolvedRelative);
                        dereferencedValue = reference.getFragment();
                    }
                } else {
                    URI targetUri = baseURI.resolve(new URI(refEntry.getValue()));
                    URI absoluteUri = makeAbsoluteURI(targetUri);
                    Reference reference = getFileFromFileReg(absoluteUri)
                            .getFragment(new FragmentIdentifier(targetUri.getFragment()));
                    dereferencedValue = reference.getFragment();
                }
            } catch (Exception e){
                // TODO add log
                System.err.println("could not resolve reference with - " + refEntry.getKey()
                        + " \nin a file - " + baseURI
                        + " \nwith msg - " + e.getMessage()
                );
                continue;
            }
            dereferenceRef(refEntry.getKey(), dereferencedValue);
        }
    }

    private void dereferenceRef(FragmentIdentifier ptrToRef, JsonNode dereferencedValue) {
        try{
            ((ObjectNode) derefedSource.at(ptrToRef.getPointer())).removeAll();
            JsonNode parentNode = derefedSource.at(ptrToRef.getParentPtr().getPointer());

            if (parentNode.isObject())
                ((ObjectNode) parentNode).set(ptrToRef.getPropertyName(), dereferencedValue);
            else if (parentNode.isArray())
                ((ArrayNode) parentNode).set(Integer.parseInt(ptrToRef.getPropertyName()), dereferencedValue);

        } catch (DereferenceException e) {
            throw new DereferenceRuntimeException("file with base uri - " + baseURI + " have reference at root level");
        }
    }

    private URI makeAbsoluteURI(URI uri) throws URISyntaxException {
        return new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);
    }

    public Reference getFragment(FragmentIdentifier requestedPtr) {
        Reference targetReference = requests.get(requestedPtr);

        if (targetReference == null) {
            targetReference = new Reference(requestedPtr);
            requests.put(requestedPtr, targetReference);
            targetReference.setFragment(getJsonNodeByFragmentIdentifier(requestedPtr));
        }

        return targetReference;
    }

    private JsonNode getJsonNodeByFragmentIdentifier(FragmentIdentifier ptrToFragment) {
        if (ptrToFragment.isAnchorPointer()) {
            return anchors.get(ptrToFragment.getPlainName());
        } else {
            return derefedSource.at(ptrToFragment.getPointer());
        }
    }

    public URI getBaseURI() {
        return baseURI;
    }

    @Override
    public int compareTo(BaseFile baseFile) {
        return baseURI.compareTo(baseFile.baseURI);
    }
}
