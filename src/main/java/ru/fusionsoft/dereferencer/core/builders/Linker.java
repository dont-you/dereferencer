package ru.fusionsoft.dereferencer.core.builders;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.builders.paths.PathToNode;
import ru.fusionsoft.dereferencer.core.reference.Reference;
import ru.fusionsoft.dereferencer.exception.ReferenceException;

public class Linker{
    public JsonNode combine(Reference reference) throws ReferenceException, JsonMappingException, JsonProcessingException{
        JsonNode currentNode= Dereferencer.objectMapper.readTree("{}");
        ((ObjectNode) currentNode).set("resultOfDereference", reference.getSource());
        SchemeDescriber schemeItems = SchemeDescriber.describe(currentNode);
        deref(reference,schemeItems, currentNode);
        return reference.setToSource(currentNode.get("resultOfDereference"));
    }

    public void deref(Reference currentRef, SchemeDescriber schemeItems, JsonNode rootNode) throws ReferenceException, JsonMappingException, JsonProcessingException{
        for(PathToNode remoteRef: schemeItems.getRemoteRefs()){
            String path = remoteRef.getPathToRef();
            String refValue = remoteRef.getNode().asText();
            derefSpecifiedRef(currentRef, refValue, rootNode, path);
        }

        int rounds = schemeItems.getLocalRefs().size();

        for (int i = 0; i < rounds;i++){
            Set<PathToNode> dereferenced = new HashSet<>();
            Stack<PathToNode> paths = new Stack<>();
            for(PathToNode localRef: schemeItems.getLocalRefs()){
                paths.add(localRef);
            }

            while(!paths.empty()){
                PathToNode localRef = paths.pop();
                String path = localRef.getPathToRef();
                String refValue = localRef.getNode().asText();
                boolean isDereferenced = derefSpecifiedRef(currentRef, refValue, rootNode, path);
                if(isDereferenced)
                    dereferenced.add(localRef);
            }


            for(PathToNode toRemove: dereferenced){
                schemeItems.getLocalRefs().remove(toRemove);
            }
        }

        List<PathToNode> allOfs = schemeItems.getAllOfs().
                stream().sorted(Comparator.comparing(PathToNode::getPathToRef, (p1, p2) -> {
                    return p2.length() - p1.length();}
                        )).collect(Collectors.toList());

        for(PathToNode allOfNode: allOfs){
            JsonNode resultNode = mergeAllOf(allOfNode.getNode());
            setNode(rootNode, resultNode, allOfNode.getPathToRef());
        }
    }

    public boolean derefSpecifiedRef(Reference currentReference,String ref, JsonNode rootNode, String ptr) throws ReferenceException, JsonMappingException, JsonProcessingException{
        JsonNode newNode = combine(currentReference.createNewReference(ref));
        if(newNode.getNodeType() == JsonNodeType.MISSING)
            return false;
        setNode(rootNode, newNode, ptr);
        return true;
    }

    public JsonNode mergeAllOf(JsonNode allOfNode) {
        JsonNode result = null;
        try {
            result = Dereferencer.objectMapper.readTree("{}");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        for (int i = 0; i < allOfNode.size(); i++) {
            Stack<JsonNode> memory = new Stack<>();
            Stack<String> pathStack = new Stack<>();
            memory.push(allOfNode.at("/"+i));
            pathStack.push("");

            while (!memory.empty()) {
                JsonNode currentNode = memory.pop();
                String currentPath = pathStack.pop();
                Iterator<Entry<String, JsonNode>> fields = currentNode.fields();

                if (result.at(currentPath).isMissingNode()) {
                    ObjectNode parent = (ObjectNode) result.at(currentPath.substring(0, currentPath.lastIndexOf("/")));
                    parent.set(currentPath.substring(currentPath.lastIndexOf("/") + 1), currentNode);
                    if (currentNode.isObject())
                        continue;
                } else if (currentNode.isObject()) {
                    if (!result.at(currentPath).isObject()) {
                        ObjectNode parent = (ObjectNode) result.at(currentPath.substring(0, currentPath.lastIndexOf("/")));
                        parent.set(currentPath.substring(currentPath.lastIndexOf("/") + 1), currentNode);
                        continue;
                    }
                } else if (currentNode.isArray()) {
                    Iterator<JsonNode> elements = currentNode.elements();
                    ArrayNode resArray = (ArrayNode) result.at(currentPath);

                    while (elements.hasNext()) {
                        JsonNode value = elements.next();
                        if (!findInArrayNode(resArray, value))
                            ((ArrayNode) resArray).add(value);
                    }
                } else {
                    ObjectNode parent = (ObjectNode) result.at(currentPath.substring(0, currentPath.lastIndexOf("/")));
                    parent.set(currentPath.substring(currentPath.lastIndexOf("/") + 1), currentNode);
                }


                while (fields.hasNext()) {
                    Entry<String, JsonNode> field = fields.next();

                    memory.push(field.getValue());
                    pathStack.push(currentPath + "/" + field.getKey());
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


    public void setNode(JsonNode rootNode, JsonNode newNode, String ptr){
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
            ((ObjectNode)rootNode).set("resultOfDereference",newNode);
        }
    }
}
