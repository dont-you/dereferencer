package ru.fusionsoft.dereferencer.core.schema.impl;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.fusionsoft.dereferencer.core.SchemaLoader;
import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;
import ru.fusionsoft.dereferencer.core.routing.Route;

import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public class AllOfSchema extends Schema {
    public AllOfSchema(SchemaLoader loader, Route schemaRoute, JsonNode sourceJson) {
        super(loader, schemaRoute, sourceJson);
    }

    @Override
    public JsonNode asJson() throws LoadException {
        super.asJson();
        if(!resolvedJson.at("/allOf").isMissingNode()){
            JsonNode result;
            try {
                result = new ObjectMapper().readTree("{}");
            } catch (JsonMappingException e) {
                throw new UnknownException(""); // todo
            } catch (JsonProcessingException e) {
                throw new UnknownException(""); // todo
            }

            for (int i = 0; i < resolvedJson.at("/allOf").size(); i++) {
                Stack<JsonNode> memory = new Stack<>();
                Stack<String> pathStack = new Stack<>();
                memory.push(resolvedJson.at("/allOf/" + i));
                pathStack.push("");

                while (!memory.empty()) {
                    JsonNode currentNode = memory.pop();
                    String currentPath = pathStack.pop();
                    Iterator<Map.Entry<String, JsonNode>> fields = currentNode.fields();
                    JsonNode resNodeByPath = result.at(currentPath);

                    if (resNodeByPath.isObject() && currentNode.isObject()) {
                        while (fields.hasNext()) {
                            Map.Entry<String, JsonNode> field = fields.next();

                            memory.push(field.getValue());
                            pathStack.push(currentPath + "/" + field.getKey());
                        }

                    } else if (resNodeByPath.isMissingNode() || !currentNode.isArray()) {
                        result = setNode(result, currentNode, currentPath);
                    } else {
                        Iterator<JsonNode> elements = currentNode.elements();
                        ArrayNode resArray = (ArrayNode) resNodeByPath;

                        while (elements.hasNext()) {
                            JsonNode value = elements.next();
                            if (!findInArrayNode(resArray, value))
                                ((ArrayNode) resArray).add(value);
                        }
                    }
                }
            }

            resolvedJson = result;
        }

        return resolvedJson;
    }

    public boolean findInArrayNode(ArrayNode arrayNode, JsonNode node) {
        for (int i = 0; i < arrayNode.size(); i++) {
            if (arrayNode.get(i).equals(node))
                return true;
        }

        return false;
    }

    public JsonNode setNode(JsonNode rootNode, JsonNode newNode, String ptr) {
        if (!ptr.equals("")) {
            JsonNode parentNode = rootNode.at(JsonPointer
                    .compile(ptr)
                    .head());
            if (parentNode.isArray()) {
                ((ArrayNode) parentNode).set(Integer.parseInt(ptr.substring(ptr.lastIndexOf("/") + 1)), newNode);
            } else {
                ((ObjectNode) parentNode).set(ptr.substring(ptr.lastIndexOf("/") + 1), newNode);
            }
        } else {
            rootNode = newNode;
        }

        return rootNode;
    }
}
