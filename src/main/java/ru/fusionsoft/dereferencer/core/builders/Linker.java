package ru.fusionsoft.dereferencer.core.builders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.fusionsoft.dereferencer.core.builders.paths.PathToRef;
import ru.fusionsoft.dereferencer.core.reference.Reference;
import ru.fusionsoft.dereferencer.core.reference.factories.ReferenceFactory;
import ru.fusionsoft.dereferencer.core.reference.impl.RemoteReference;
import ru.fusionsoft.dereferencer.enums.ReferenceType;
import ru.fusionsoft.dereferencer.exception.ReferenceException;

public class Linker{
    public JsonNode combine(Reference reference) throws ReferenceException{
        JsonNode currentNode = reference.getSource();
        RefsDescriber references = RefsDescriber.describe(currentNode);
        deref(reference,references, currentNode);
        if(reference.getReferenceType()==ReferenceType.REMOTE){
            RemoteReference remoteReference = (RemoteReference) reference;
            if(remoteReference.getFragment()!=null){
                return currentNode.at(remoteReference.getFragment());
            }
        }
        return currentNode;
    }

    public void deref(Reference currentRef, RefsDescriber references, JsonNode rootNode) throws ReferenceException{
        for(PathToRef remoteRef: references.getRemoteRefs()){
            String path = remoteRef.getPathToRef();
            String refValue = remoteRef.getRefValue();
            derefSpecifiedRef(currentRef, refValue, rootNode, path);
        }

        int rounds = references.getLocalRefs().size();

        for (int i = 0; i < rounds;i++){
            Set<PathToRef> dereferenced = new HashSet<>();
            for(PathToRef localRef: references.getLocalRefs()){
                String path = localRef.getPathToRef();
                String refValue = localRef.getRefValue();
                Object nodeFromRef = rootNode.at(refValue.substring(1));

                if(nodeFromRef.getClass()==MissingNode.class){
                    continue;
                } else if(nodeFromRef.getClass()==ObjectNode.class){
                    if(((ObjectNode) nodeFromRef).at("/$ref").getClass()!=MissingNode.class){
                        continue;
                    }
                }

                derefSpecifiedRef(currentRef, refValue, rootNode, path);
                dereferenced.add(localRef);
            }

            for(PathToRef toRemove: dereferenced){
                references.getLocalRefs().remove(toRemove);
            }
        }
    }

    public void derefSpecifiedRef(Reference currentReference,String ref, JsonNode rootNode, String ptr) throws ReferenceException{
        JsonNode newNode=null;
        try{
            URI uri = new URI(ref);
            if(ReferenceType.isRemoteReference(uri)){
                newNode = combine(((RemoteReference) currentReference).createUsingCurrent(ref));
            } else if(ReferenceType.isURLReference(uri)){
                newNode = combine(ReferenceFactory.create(uri));
            } else if(ReferenceType.isLocalReference(uri)) {
                newNode = rootNode.at(ref.substring(1));
            }
        } catch (URISyntaxException e){
            throw new ReferenceException("error in dereferencing ref, with message: " + e.getMessage());
        }

        setNode(rootNode, newNode, ptr);
    }

    public void setNode(JsonNode rootNode, JsonNode newNode, String ptr){
        ObjectNode changingNode = ((ObjectNode) rootNode.at(ptr));

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
            changingNode.removeAll();
            changingNode.setAll((ObjectNode) newNode);
        }
    }
}
