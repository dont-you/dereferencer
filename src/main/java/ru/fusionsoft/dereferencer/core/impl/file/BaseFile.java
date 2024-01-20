package ru.fusionsoft.dereferencer.core.impl.file;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;

import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceRuntimeException;
import static ru.fusionsoft.dereferencer.core.impl.file.FragmentIdentifier.IdentifierType.*;

public class BaseFile implements File, Comparable<BaseFile>, ReferenceListener{
    protected final URI baseURI;
    protected final FileRegister fileRegister;
    protected final JsonNode source;
    protected final JsonNode derefedSource;
    protected final Map<Reference, String> references;
    protected final Map<String, JsonNode> anchors;
    protected final Map<FragmentIdentifier, Reference> requests;
    private boolean canResponse;

    public BaseFile(FileRegister fileRegister, URI baseURI, JsonNode source) {
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
    final public void resolve(){
        beforeResolvingHook();
        exploreSource("", source);
        setCanResponseToTrue();
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
            processReference(pathToNode, nodeValue.asText());
        } else if(nodeValue.isArray()) {
            String currentPath = pathToNode.concat("/").concat(nodeKey);
            IntStream.range(0,nodeValue.size())
                    .forEach(i -> exploreSource(currentPath.concat("/").concat(String.valueOf(i)),nodeValue.get(i)));
        } else if(nodeValue.isObject()){
            exploreSource(pathToNode.concat("/").concat(nodeKey), nodeValue);
        }
    }

    private void processReference(String pathToReference, String referenceValue){
        try {
            Reference targetReference = Character.isDigit(referenceValue.charAt(0)) ?
                    processRelJsonPtrReference(pathToReference, referenceValue) :
                    processURIReference(referenceValue);

            references.put(targetReference,pathToReference);
            targetReference.subscribe(this);
        } catch (URISyntaxException | DereferenceException e) {
            System.err.println("could not resolve reference with value- " + referenceValue
                    + " \nin a file - " + baseURI
                    + " \nwith msg - " + e.getMessage()
            );
        }
    }

    private Reference processRelJsonPtrReference(String pathToReferencedValue, String referencedValue) throws DereferenceException {
        FragmentIdentifier relativePointer = new FragmentIdentifier(referencedValue);
        Reference reference = new Reference(relativePointer);
        reference.setFragment(FragmentIdentifier.evaluateRelativeJsonPointer(derefedSource, pathToReferencedValue, relativePointer.getIdentifier()));
        return reference;
    }

    private Reference processURIReference(String referenceValue) throws URISyntaxException, DereferenceException {
        URI targetURI = baseURI.resolve(new URI(referenceValue));
        URI absoluteURI = makeAbsoluteURI(targetURI);
        FragmentIdentifier fragmentIdentifier = new FragmentIdentifier(targetURI.getFragment());
        return getFileFromFileReg(absoluteURI).getFragment(fragmentIdentifier);
    }

    private void setCanResponseToTrue(){
        canResponse = true;
        requests.values().forEach(ref -> {
            if(!ref.isResolved())
                ref.setFragment(resolveFragment(ref.getFragmentIdentifier()));
        });
    }

    protected void afterResolvingHook(){}

    protected BaseFile getFileFromFileReg(URI targetUri) throws DereferenceException {
        if (targetUri.equals(baseURI))
            return this;
        else
            return (BaseFile) fileRegister.get(targetUri);
    }

    private URI makeAbsoluteURI(URI uri) throws URISyntaxException {
        return new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);
    }

    public Reference getFragment(FragmentIdentifier requestedPtr) {
        Reference targetReference = requests.get(requestedPtr);

        if (targetReference == null) {
            targetReference = new Reference(requestedPtr);
            requests.put(requestedPtr, targetReference);
            if(canResponse)
                targetReference.setFragment(resolveFragment(requestedPtr));
        }

        return targetReference;
    }

    private JsonNode resolveFragment(FragmentIdentifier ptrToFragment) {
        if (ptrToFragment.getType() == JSON_POINTER) {
            return derefedSource.at(ptrToFragment.getIdentifier());
        } else if (ptrToFragment.getType() == PLAIN_NAME){
            return anchors.get(ptrToFragment.getIdentifier());
        } else {
            throw new DereferenceRuntimeException("some unexpected error while resolving fragment: relative json pointer not evaluated");
        }
    }

    @Override
    public int compareTo(BaseFile baseFile) {
        return baseURI.compareTo(baseFile.baseURI);
    }

    @Override
    public void update(Reference reference) {
        setDereferencedValue(new FragmentIdentifier(references.get(reference)), reference.getFragment());
    }

    private void setDereferencedValue(FragmentIdentifier ptrToRef, JsonNode dereferencedValue){
        ((ObjectNode) derefedSource.at(ptrToRef.getIdentifier())).removeAll();
        JsonNode parentNode = derefedSource.at(FragmentIdentifier.getParentPointer(ptrToRef));

        if (parentNode.isObject())
            ((ObjectNode) parentNode).set(FragmentIdentifier.getPropertyName(ptrToRef), dereferencedValue);
        else if (parentNode.isArray())
            ((ArrayNode) parentNode).set(Integer.parseInt(FragmentIdentifier.getPropertyName(ptrToRef)), dereferencedValue);
    }
}
