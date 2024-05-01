package ru.fusionsoft.dereferencer.allof;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashSet;
import java.util.Set;

public class Merger {
    public JsonNode merge(ArrayNode allOf) {
        JsonNode merged = MissingNode.getInstance();

        for (int i = 0; i < allOf.size(); i++) {
            merged = mergeJsonNodes(merged, allOf.get(i));
        }

        return merged;
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
}