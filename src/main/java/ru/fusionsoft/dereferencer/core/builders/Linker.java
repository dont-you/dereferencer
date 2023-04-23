package ru.fusionsoft.dereferencer.core.builders;

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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.fusionsoft.dereferencer.core.reference.Reference;
import ru.fusionsoft.dereferencer.core.reference.factories.ReferenceFactory;
import ru.fusionsoft.dereferencer.core.reference.impl.RemoteReference;
import ru.fusionsoft.dereferencer.enums.ReferenceType;
import ru.fusionsoft.dereferencer.exception.ReferenceException;

public class Linker{
    public JsonNode combine(Reference reference) throws ReferenceException{
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

    public void deref(Reference currentRef, Map<String,String> references, JsonNode rootNode) throws ReferenceException{
        int rounds = references.size();
        for (int i = 0; i < rounds;i++){
            List<String> dereferenced = new ArrayList<>();

            for(Entry<String,String> ref: references.entrySet()){
                try{
                    String key = ref.getKey();
                    String value = ref.getValue();
                    URI uri = new URI(value);

                    if(ReferenceType.isLocalReference(uri)){
                        if(rootNode.at(key).getClass()!=MissingNode.class){
                            derefLocalRef(key,value,rootNode);
                            dereferenced.add(key);
                        }

                    } else if(ReferenceType.isRemoteReference(uri)){
                        derefRemoteRef((RemoteReference)currentRef, key,value, rootNode);
                        dereferenced.add(key);

                    } else if(ReferenceType.isURLReference(uri)){
                        derefURLRef(currentRef, key,value, rootNode);
                        dereferenced.add(key);
                    }

                } catch (URISyntaxException e){
                    throw new ReferenceException("error in file references, with message: " + e.getMessage());
                }
            }

            for(String toRemove: dereferenced){
                references.remove(toRemove);
            }
        }
    }

    public void derefLocalRef(String ptr, String ref, JsonNode rootNode){
        JsonNode newNode = rootNode.at(ref.substring(1));
        setNode(rootNode, newNode, ptr);
    }

    public void derefRemoteRef(RemoteReference currentRefference,String ptr,String ref, JsonNode rootNode) throws ReferenceException{
        JsonNode newNode = combine(currentRefference.createUsingCurrent(ref));
        setNode(rootNode, newNode, ptr);
    }

    public void derefURLRef(Reference currentRefference,String ptr,String ref, JsonNode rootNode) throws ReferenceException{
        // TODO
        JsonNode newNode;
        try {
            newNode = combine(ReferenceFactory.create(new URI(ref)));
            setNode(rootNode, newNode, ptr);
        } catch (URISyntaxException e) {
        }
    }

    public void setNode(JsonNode rootNode, JsonNode newNode, String ptr){
        ObjectNode changingNode = ((ObjectNode) rootNode.at(ptr));
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
            changingNode.setAll((ObjectNode) newNode);
        }
    }

    public Map<String,String> findReferences(JsonNode jsonNode){
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
