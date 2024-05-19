package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.NotNull;
import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public class FileImpl implements DereferencedFile {
    protected final URI baseURI;
    protected final JsonNode derefedSource;
    private final Map<String, String> notDerefed;
    private final Map<String, String> processing;
    private final Map<String, String> anchors;
    private final int hash;

    public FileImpl(URI baseURI, JsonNode source) {
        this.baseURI = baseURI;
        this.derefedSource = source;
        this.notDerefed = new ConcurrentHashMap<>();
        this.processing = new ConcurrentHashMap<>();
        this.anchors = new ConcurrentHashMap<>();
        hash = Objects.hash(baseURI);
        traverseSourceTree();
    }

    private void traverseSourceTree() {
        init();
        exploreSource("", derefedSource);
        afterDereferencingHook();
    }

    protected void init(){

    }

    protected void afterDereferencingHook() {
    }

    final protected void exploreSource(String pathToSource, JsonNode source) {
        if (source.isArray())
            IntStream.range(0, source.size()).forEach(i -> exploreSource(pathToSource.concat("/").concat(String.valueOf(i)), source.get(i)));
        else
            source.fields().forEachRemaining(field -> resolveNode(pathToSource, decodeFieldKey(field.getKey()), field.getValue()));
    }

    private String decodeFieldKey(String fieldKey) {
        if (fieldKey.contains("/"))
            return fieldKey.replaceAll("/", "~1");
        else if (fieldKey.contains("~"))
            return fieldKey.replaceAll("~", "~0");
        else
            return fieldKey;
    }

    protected void resolveNode(String pathToNode, String nodeKey, JsonNode nodeValue) {
        if (nodeKey.equals("$anchor"))
            anchors.put(nodeValue.asText(), pathToNode);
        else if (nodeKey.equals("$ref"))
            notDerefed.put(pathToNode, nodeValue.asText());
        else if (nodeValue.isArray() || nodeValue.isObject())
            exploreSource(pathToNode.concat("/").concat(nodeKey), nodeValue);
    }

    @Override
    public JsonNode getFragment(String path, Dereferencer dereferencer) {
        path = resolveAnchorToPath(path);
        dereference(combineItems(path, notDerefed), dereferencer);

        return derefedSource.at(path);
    }

    private void dereference(Map<String,String> refMap, Dereferencer dereferencer){
        if (!refMap.isEmpty()) {
            processing.putAll(refMap);

            for (Map.Entry<String, JsonNode> ref : dereferencer.dereference(baseURI, refMap).entrySet()) {
                setDereferencedValue(ref.getKey(), ref.getValue());
                processing.remove(ref.getKey());
                notDerefed.remove(ref.getKey());
            }
        }
    }


    @Override
    public JsonNode getFragmentImmediately(String path, Dereferencer dereferencer){
        path = resolveAnchorToPath(path);
        dereferenceImmediately(combineItems(path, notDerefed), combineItems(path, processing), dereferencer);

        return derefedSource.at(path);
    }

    private void dereferenceImmediately(Map<String,String> combinedNotDerefed, Map<String,String> combinedProcessing, Dereferencer dereferencer){
        Map<String, String> combined = new HashMap<>();

        for(Map.Entry<String, String> target: combinedNotDerefed.entrySet()) {
            if(!combinedProcessing.containsKey(target.getKey())){
                combined.put(target.getKey(), target.getValue());
            }
        }

        dereference(combined, dereferencer);
    }

    private synchronized void setDereferencedValue(String path, JsonNode dereferencedValue) {
        if(!derefedSource.at(path).has("$ref"))
            return;

        setValue(path, dereferencedValue);
    }

    protected void setValue(String path, JsonNode value){
        ((ObjectNode) derefedSource.at(path)).removeAll();
        JsonNode parentNode = derefedSource.at(getParentPointer(path));

        if (parentNode.isObject())
            ((ObjectNode) parentNode).set(getPropertyName(path), value);
        else if (parentNode.isArray())
            ((ArrayNode) parentNode).set(Integer.parseInt(getPropertyName(path)), value);
    }

    protected String getParentPointer(String identifier) {
        return identifier.substring(0, identifier.lastIndexOf("/"));
    }

    protected String getPropertyName(@NotNull String identifier) {
        return identifier.substring(identifier.lastIndexOf("/") + 1);
    }

    protected final Map<String, String> combineItems(String pathToFragment, Map<String, String> itemMap) {
        Map<String, String> combined = new HashMap<>();
        pathToFragment = pathToFragment + "/";

        for (Map.Entry<String, String> ref : itemMap.entrySet()) {
            if (ref.getKey().startsWith(pathToFragment))
                combined.put(ref.getKey(), ref.getValue());
        }

        return combined;
    }

    protected final String resolveAnchorToPath(String supposedAnchor) {
        return supposedAnchor.startsWith("/") || supposedAnchor.isEmpty() ? supposedAnchor : anchors.get(supposedAnchor);
    }


    @Override
    public URI getBaseURI() {
        return baseURI;
    }


    @Override
    public int hashCode(){
        return hash;
    }

    @Override
    public boolean equals(Object o){
        return this.hash == o.hashCode();
    }
}
