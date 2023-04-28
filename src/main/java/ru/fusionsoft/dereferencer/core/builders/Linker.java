package ru.fusionsoft.dereferencer.core.builders;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.builders.paths.PathToRef;
import ru.fusionsoft.dereferencer.core.reference.Reference;
import ru.fusionsoft.dereferencer.exception.ReferenceException;

public class Linker{
    public JsonNode combine(Reference reference) throws ReferenceException, JsonMappingException, JsonProcessingException{
        JsonNode currentNode= Dereferencer.objectMapper.readTree("{}");
        ((ObjectNode) currentNode).set("resultOfDereference", reference.getSource());
        RefsDescriber references = RefsDescriber.describe(currentNode);
        deref(reference,references, currentNode);
        return reference.setToSource(currentNode.get("resultOfDereference"));
    }

    public void deref(Reference currentRef, RefsDescriber references, JsonNode rootNode) throws ReferenceException, JsonMappingException, JsonProcessingException{
        for(PathToRef remoteRef: references.getRemoteRefs()){
            String path = remoteRef.getPathToRef();
            String refValue = remoteRef.getRefValue();
            derefSpecifiedRef(currentRef, refValue, rootNode, path);
        }

        int rounds = references.getLocalRefs().size();

        for (int i = 0; i < rounds;i++){
            Set<PathToRef> dereferenced = new HashSet<>();
            Stack<PathToRef> paths = new Stack<>();
            for(PathToRef localRef: references.getLocalRefs()){
                paths.add(localRef);
            }

            while(!paths.empty()){
                PathToRef localRef = paths.pop();
                String path = localRef.getPathToRef();
                String refValue = localRef.getRefValue();
                boolean isDereferenced = derefSpecifiedRef(currentRef, refValue, rootNode, path);
                if(isDereferenced)
                    dereferenced.add(localRef);
            }


            for(PathToRef toRemove: dereferenced){
                references.getLocalRefs().remove(toRemove);
            }
        }
    }

    public boolean derefSpecifiedRef(Reference currentReference,String ref, JsonNode rootNode, String ptr) throws ReferenceException, JsonMappingException, JsonProcessingException{
        JsonNode newNode = combine(currentReference.createNewReference(ref));
        if(newNode.getNodeType() == JsonNodeType.MISSING)
            return false;
        setNode(rootNode, newNode, ptr);
        return true;
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
