package ru.fusionsoft.dereferencer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.fusionsoft.dereferencer.exception.ReferenceException;
import ru.fusionsoft.dereferencer.reference.Reference;
import ru.fusionsoft.dereferencer.reference.ReferenceFactory;
import ru.fusionsoft.dereferencer.reference.ReferenceType;
import ru.fusionsoft.dereferencer.reference.impl.RemoteReference;

public class Linker{
    public JsonNode combine(Reference reference) throws StreamReadException, DatabindException, IOException, URISyntaxException, ReferenceException{
        JsonNode currentNode = reference.getSource();
        Map<String, String> references = findReferences(currentNode);
        deref(reference,references, currentNode);
        if(reference.getReferenceType()==ReferenceType.REMOTE){
            RemoteReference remoteReference = (RemoteReference) reference;
            if(remoteReference.getFragment()!=null){
                return currentNode.at(remoteReference.getFragment());
            }
        }
        return currentNode;
    }

    public void deref(Reference currentRef, Map<String,String> references, JsonNode rootNode) throws URISyntaxException, IOException, ReferenceException{
        int rounds = references.size();
        for (int i = 0; i < rounds;i++){
            List<String> dereferenced = new ArrayList<>();

            for(Entry<String,String> ref: references.entrySet()){
                if(ReferenceType.isLocalReference(new URI(ref.getValue()))){
                    if(rootNode.at(ref.getKey()).getClass()!=MissingNode.class){
                        derefLocalRef(ref.getKey(), ref.getValue(),rootNode);
                        dereferenced.add(ref.getKey());
                    }
                } else if(ReferenceType.isRemoteReference(new URI(ref.getValue()))){
                    derefRemoteRef((RemoteReference)currentRef, ref.getKey(), ref.getValue(), rootNode);
                    dereferenced.add(ref.getKey());
                }
            }

            for(String toRemove: dereferenced){
                references.remove(toRemove);
            }
        }
    }

    public void derefLocalRef(String ptr, String ref, JsonNode rootNode){
        ObjectNode changingNode = ((ObjectNode) rootNode.at(ptr));
        JsonNode newNode = rootNode.at(ref.substring(1));
        if(newNode.getNodeType()!=JsonNodeType.OBJECT){
            JsonNode parentNode = rootNode.at(JsonPointer
                                              .compile(ptr)
                                              .head());
            if(parentNode.getNodeType()==JsonNodeType.ARRAY){
                ((ArrayNode) parentNode).set(Integer.parseInt(ptr.substring(ptr.lastIndexOf("/")+1)), newNode);
            } else {
                ((ObjectNode) parentNode).set(ptr.substring(ptr.lastIndexOf("/")+1), newNode);
            }

        } else {
            changingNode.removeAll();
            changingNode.setAll((ObjectNode)newNode);
        }
    }

     public void derefRemoteRef(RemoteReference currentRefference,String ptr,String ref, JsonNode rootNode) throws StreamReadException, DatabindException, IOException, URISyntaxException, ReferenceException{
        ObjectNode changingNode = ((ObjectNode) rootNode.at(ptr));
        JsonNode newNode= combine(ReferenceFactory.create(new URI(currentRefference.getDirectory()+"/"+ref)));
        if(newNode.getNodeType()!=JsonNodeType.OBJECT){
            JsonNode parentNode = rootNode.at(JsonPointer
                                              .compile(ptr)
                                              .head());
            if(parentNode.getNodeType()==JsonNodeType.ARRAY){
                ((ArrayNode) parentNode).set(Integer.parseInt(ptr.substring(ptr.lastIndexOf("/")+1)), newNode);
            } else {
                ((ObjectNode) parentNode).set(ptr.substring(ptr.lastIndexOf("/")+1), newNode);
            }
        } else {
            changingNode.removeAll();
            changingNode.setAll((ObjectNode)newNode);
        }
    }

    public Map<String,String> findReferences(JsonNode jsonNode) throws JsonMappingException, JsonProcessingException{
        Map<String,String> references = new HashMap<>();
        JsonNode currentNode;
        Stack<JsonNode> memory = new Stack<>();
        Stack<String> pathStack = new Stack<>();
        memory.push(jsonNode);
        pathStack.push("");

        while (!memory.empty()) {
            currentNode = memory.pop();
            String currentPath = pathStack.pop();
            Iterator<Entry<String, JsonNode>> fields = currentNode.fields();

            while(fields.hasNext()){
                Entry<String, JsonNode> field = fields.next();

                if(field.getValue().isArray()){
                    Iterator<JsonNode> elements = field.getValue().elements();

                    int i=0;
                    while(elements.hasNext()){
                        memory.push(elements.next());
                        pathStack.push(currentPath + "/" + field.getKey() + "/" + i++);
                    }

                } else {
                    if(field.getKey().equals("$ref")){
                        references.put(currentPath, field.getValue().asText());
                    } else {
                        memory.push(field.getValue());
                        pathStack.push(currentPath + "/" + field.getKey());
                    }
                }
            }
        }
        return references;
    }

}
