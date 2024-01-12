package ru.fusionsoft.dereferencer.allOf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceRuntimeException;
import ru.fusionsoft.dereferencer.core.impl.file.BaseFile;
import ru.fusionsoft.dereferencer.core.impl.file.FragmentIdentifier;

import java.net.URI;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.IntStream;

public class AllOfFile extends BaseFile {
    Stack<String> pathsToNotMergedAllOfs;
    JsonNode mergedJson;

    public AllOfFile(FileRegister fileRegister, URI baseURI, JsonNode source) {
        // TODO refactor
        super(fileRegister, baseURI, source);
        pathsToNotMergedAllOfs = new Stack<>();
        mergedJson = derefedSource;
    }

    @Override
    public JsonNode getDerefedJson() {
        return mergedJson;
    }

    @Override
    protected final void afterResolvingHook(){
         if (!pathsToNotMergedAllOfs.empty())
            mergeAllOfArrays();
    }

    private void mergeAllOfArrays() {
        for (String pathToAllOf : pathsToNotMergedAllOfs) {
            JsonNode merged = mergeAllOfArray((ArrayNode) derefedSource.at(pathToAllOf + "/" + "allOf"));
            ((ObjectNode) derefedSource.at(pathToAllOf))
                    .remove("allOf");
            ((ObjectNode) derefedSource.at(pathToAllOf))
                    .replace("allOf", merged);

            try{
                ((ObjectNode) derefedSource.at(FragmentIdentifier.getParentPointer(pathToAllOf)))
                        .set(FragmentIdentifier.getPropertyName(pathToAllOf), merged);
            } catch (DereferenceException e) {
                throw new DereferenceRuntimeException("file with base uri - " + getBaseURI() + " have reference at root level");
            }
        }
    }

    private JsonNode mergeAllOfArray(ArrayNode array) {
        JsonNode merged = array.get(0);
        for (int i = 1; i < array.size(); i++) {
            mergeNodes(merged, array.get(i));
        }
        return merged;
    }

    private void mergeNodes(JsonNode left, JsonNode right) {
        Stack<JsonNode> nodeStack = new Stack<>();
        Stack<String> pathStack = new Stack<>();
        nodeStack.push(right);
        pathStack.push("");

        while (!nodeStack.empty()) {
            JsonNode currentNode = nodeStack.pop();
            String currentPath = pathStack.pop();
            Iterator<Entry<String, JsonNode>> fields = currentNode.fields();

            while (fields.hasNext()) {
                Entry<String, JsonNode> field = fields.next();
                String fieldName = field.getKey();
                String pathToField = currentPath + "/" + fieldName;
                JsonNode leftNode = left.at(pathToField);
                JsonNode rightNode = field.getValue();

                if (leftNode.isObject() && rightNode.isObject()) {
                    nodeStack.push(rightNode);
                    pathStack.push(pathToField);
                } else if (leftNode.isArray() && rightNode.isArray()) {
                    ((ObjectNode) left.at(currentPath)).set(fieldName,
                            mergeArrayNode((ArrayNode) leftNode, (ArrayNode) rightNode));
                } else if (!leftNode.isObject()) {
                    ((ObjectNode) left.at(currentPath)).set(fieldName, rightNode);
                }
            }
        }
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
    protected void resolveNode(String pathToNode, String nodeKey, JsonNode nodeValue){
        if (nodeKey.equals("allOf")) {
            pathsToNotMergedAllOfs.push(pathToNode);
            String currentPath = pathToNode.concat("/").concat(nodeKey);
            IntStream.range(0,nodeValue.size())
                    .forEach(i -> exploreSource(currentPath.concat("/").concat(String.valueOf(i)),nodeValue.get(i)));
        } else {
            super.resolveNode(pathToNode, nodeKey, nodeValue);
        }
    }
}
