package ru.fusionsoft.dereferencer.allof;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.FileImpl;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class MergedFile extends FileImpl {
    private Merger merger;
    private Set<String> notMerged;
    public MergedFile(URI baseURI, JsonNode source) {
        super(baseURI, source);
    }

    @Override
    protected void init(){
        merger = new Merger();
        notMerged = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @Override
    protected void resolveNode(String pathToNode, String nodeKey, JsonNode nodeValue) {
        if (nodeKey.equals("allOf"))
            notMerged.add(pathToNode);

        super.resolveNode(pathToNode, nodeKey, nodeValue);
    }

    @Override
    public JsonNode getFragment(String path, Dereferencer dereferencer) {
        path = resolveAnchorToPath(path);
        JsonNode response = super.getFragment(path, dereferencer);
        mergeFromPath(path);
        return response;
    }

    @Override
    public JsonNode getFragmentImmediately(String path, Dereferencer dereferencer){
        path = resolveAnchorToPath(path);
        JsonNode response = super.getFragmentImmediately(path, dereferencer);
        mergeFromPath(path);
        return response;
    }

    private void mergeFromPath(String path) {
        List<String> combined = new ArrayList<>(notMerged.stream().filter(notMergedArray -> notMergedArray.startsWith(path)).toList());
        combined.sort(Comparator.reverseOrder());

        for(String pathToNotMerged: combined){
            JsonNode merged = merger.merge(derefedSource.at(pathToNotMerged + "/allOf").deepCopy());
            synchronized (this){
                if(notMerged.contains(pathToNotMerged)){
                    notMerged.remove(pathToNotMerged);
                    setMergedAllOf(pathToNotMerged, merged);
                }
            }
        }
    }

    private void setMergedAllOf(String pathToAllOf, JsonNode mergedAllOf) {
        ((ObjectNode) derefedSource.at(pathToAllOf))
                .remove("allOf");
        ((ObjectNode) derefedSource.at(pathToAllOf))
                .replace("allOf", mergedAllOf);
        ((ObjectNode) derefedSource.at(getParentPointer(pathToAllOf)))
                .set(getPropertyName(pathToAllOf), mergedAllOf);
    }
}