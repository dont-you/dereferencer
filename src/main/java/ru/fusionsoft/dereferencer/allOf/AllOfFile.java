package ru.fusionsoft.dereferencer.allOf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.impl.file.BaseFile;
import ru.fusionsoft.dereferencer.core.impl.file.FragmentIdentifier;

import java.net.URI;
import java.util.*;
import java.util.stream.IntStream;

public class AllOfFile extends BaseFile {
    Stack<String> pathsToNotMergedAllOfs;
    JsonNode mergedJson;

    public AllOfFile(FileRegister fileRegister, URI baseURI, JsonNode source) {
        super(fileRegister, baseURI, source);
        pathsToNotMergedAllOfs = new Stack<>();
        mergedJson = derefedSource;
    }

    @Override
    public JsonNode getDerefedJson() {
        return mergedJson;
    }

    @Override
    public URI getBaseURI() {
        return baseURI;
    }

    @Override
    protected final void afterResolvingHook() {
        if (!pathsToNotMergedAllOfs.empty())
            mergeAllOfArrays();
    }

    private void mergeAllOfArrays() {
        for (String pathToAllOf : pathsToNotMergedAllOfs) {
            setMergedAllOf(
                    pathToAllOf,
                    mergeAllOf((ArrayNode) derefedSource.at(pathToAllOf + "/" + "allOf"))
            );
        }
    }

    private JsonNode mergeAllOf(ArrayNode allOf) {
        JsonNode merged = MissingNode.getInstance();

        for (int i = 0; i < allOf.size(); i++) {
            merged = mergeJsonNodes(merged, allOf.get(i));
        }

        return merged;
    }

    private void setMergedAllOf(String pathToAllOf, JsonNode mergedAllOf) {
        ((ObjectNode) derefedSource.at(pathToAllOf))
                .remove("allOf");
        ((ObjectNode) derefedSource.at(pathToAllOf))
                .replace("allOf", mergedAllOf);
        ((ObjectNode) derefedSource.at(FragmentIdentifier.getParentPointer(pathToAllOf)))
                .set(FragmentIdentifier.getPropertyName(pathToAllOf), mergedAllOf);
    }

    private JsonNode mergeJsonNodes(JsonNode leftNode, JsonNode rightNode) {
        if (leftNode.isObject() && rightNode.isObject())
            return mergeObjectNodes((ObjectNode) leftNode, (ObjectNode) rightNode);
        else if (leftNode.isArray() && rightNode.isArray())
            return mergeArrayNodes((ArrayNode) leftNode, (ArrayNode) rightNode);
        else
            return rightNode;
    }

    private ObjectNode mergeObjectNodes(ObjectNode leftNode, ObjectNode rightNode) {
        ObjectNode mergedNode = rightNode.deepCopy();
        leftNode.fields().forEachRemaining(nodeEntry -> {
            String propertyName = nodeEntry.getKey();
            if (rightNode.has(propertyName))
                mergedNode.replace(propertyName, mergeJsonNodes(nodeEntry.getValue(), rightNode.get(propertyName)));
            else
                mergedNode.set(propertyName, nodeEntry.getValue());
        });
        return mergedNode;
    }

    private ArrayNode mergeArrayNodes(ArrayNode leftNode, ArrayNode rightNode) {
        return mergeArrayNode(leftNode, rightNode);
    }

    private ArrayNode mergeArrayNode(ArrayNode left, ArrayNode right) {
        Set<JsonNode> arrayNodes = new HashSet<>();
        left.forEach(arrayNodes::add);
        right.forEach(arrayNodes::add);
        left.removeAll();
        arrayNodes.forEach(left::add);
        return left;
    }

    @Override
    protected void resolveNode(String pathToNode, String nodeKey, JsonNode nodeValue) {
        if (nodeKey.equals("allOf")) {
            pathsToNotMergedAllOfs.push(pathToNode);
            String currentPath = pathToNode.concat("/").concat(nodeKey);
            IntStream.range(0, nodeValue.size())
                    .forEach(i -> exploreSource(currentPath.concat("/").concat(String.valueOf(i)), nodeValue.get(i)));
        } else {
            super.resolveNode(pathToNode, nodeKey, nodeValue);
        }
    }
}
