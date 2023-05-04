package ru.fusionsoft.dereferencer.core.builders;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Stack;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.reference.Reference;
import ru.fusionsoft.dereferencer.exception.ReferenceException;

public class SchemeResolver{
    private Reference reference;

    public SchemeResolver(Reference reference){
        this.reference = reference;
    }

    public JsonNode dereferenceResolve(JsonNode node) throws JsonMappingException, JsonProcessingException, ReferenceException {
        JsonNode currentNode;
        Stack<JsonNode> memory = new Stack<>();
        Stack<String> pathStack = new Stack<>();
        memory.push(node);
        pathStack.push("");

        while (!memory.empty()) {
            currentNode = memory.pop();
            String currentPath = pathStack.pop();
            Iterator<Entry<String, JsonNode>> fields = currentNode.fields();

            while(fields.hasNext()){
                Entry<String, JsonNode> field = fields.next();
                String fieldKey = field.getKey();
                JsonNode fieldValue = field.getValue();

                if(fieldKey.equals("$ref")){
                    JsonNode resolvedNode = Linker.combine(reference.createNewReference(fieldValue.asText()));
                    node = setNode(node, resolvedNode, currentPath);
                    continue;
                } else if (fieldKey.equals("allOf")){
                    JsonNode resolvedNode = mergeResolve(field.getValue());
                    node = setNode(node, resolvedNode, currentPath);
                    continue;
                }

                if(fieldValue.isArray()){
                    Iterator<JsonNode> elements = field.getValue().elements();

                    int i=0;
                    while(elements.hasNext()){
                        memory.push(elements.next());
                        pathStack.push(currentPath + "/" + field.getKey() + "/" + i++);
                    }
                } else {
                    memory.push(field.getValue());
                    pathStack.push(currentPath+ "/" + field.getKey()  );
                }
            }
        }
        return node;
    }


    public JsonNode mergeResolve(JsonNode node) throws JsonMappingException, JsonProcessingException, ReferenceException {
        JsonNode result = null;
        try {
            result = Dereferencer.objectMapper.readTree("{}");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        for (int i = 0; i < node.size(); i++) {
            JsonNode dereferencedNode = dereferenceResolve(node.at("/"+i));
            Stack<JsonNode> memory = new Stack<>();
            Stack<String> pathStack = new Stack<>();
            memory.push(dereferencedNode);
            pathStack.push("");

            while (!memory.empty()) {
                JsonNode currentNode = memory.pop();
                String currentPath = pathStack.pop();
                Iterator<Entry<String, JsonNode>> fields = currentNode.fields();
                JsonNode resNodeByPath = result.at(currentPath);

                if(resNodeByPath.isObject() && currentNode.isObject()){
                    while (fields.hasNext()) {
                        Entry<String, JsonNode> field = fields.next();

                        memory.push(field.getValue());
                        pathStack.push(currentPath + "/" + field.getKey());
                    }

                } else if(resNodeByPath.isMissingNode() || !currentNode.isArray()){
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
        return result;
    }

    public boolean findInArrayNode(ArrayNode arrayNode, JsonNode node) {
        for (int i = 0; i < arrayNode.size(); i++) {
            if (arrayNode.get(i).equals(node))
                return true;
        }

        return false;
    }

    public JsonNode setNode(JsonNode rootNode, JsonNode newNode, String ptr){
        if(!ptr.equals("")){
            JsonNode parentNode = rootNode.at(JsonPointer
                                              .compile(ptr)
                                              .head());
            if(parentNode.getNodeType()==JsonNodeType.ARRAY){
                ((ArrayNode) parentNode).set(Integer.parseInt(ptr.substring(ptr.lastIndexOf("/")+1)), newNode);
            } else {
                ((ObjectNode) parentNode).set(ptr.substring(ptr.lastIndexOf("/")+1), newNode);
            }
        } else {
            rootNode = newNode;
        }

        return  rootNode;
    }

}
