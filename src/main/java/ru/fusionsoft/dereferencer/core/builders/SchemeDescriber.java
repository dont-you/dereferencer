package ru.fusionsoft.dereferencer.core.builders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.builders.paths.PathToNode;
import ru.fusionsoft.dereferencer.enums.ReferenceType;
import ru.fusionsoft.dereferencer.exception.ReferenceException;

public class SchemeDescriber {
    private Set<PathToNode> localRefs = new HashSet<>();
    private Set<PathToNode> remoteRefs = new HashSet<>();
    private Set<PathToNode> allOfs = new HashSet<>();

    public static SchemeDescriber describe(JsonNode rootNode) throws ReferenceException{
        return findReferences(rootNode);
    }

    private static SchemeDescriber findReferences(JsonNode rootNode) throws ReferenceException{
        SchemeDescriber schemeDescriber = new SchemeDescriber();
        JsonNode currentNode;
        Stack<JsonNode> memory = new Stack<>();
        Stack<String> pathStack = new Stack<>();
        memory.push(rootNode);
        pathStack.push("");

        while (!memory.empty()) {
            currentNode = memory.pop();
            String currentPath = pathStack.pop();
            Iterator<Entry<String, JsonNode>> fields = currentNode.fields();

            while(fields.hasNext()){
                Entry<String, JsonNode> field = fields.next();

                if(field.getValue().isArray()){
                    Iterator<JsonNode> elements = field.getValue().elements();

                    if(field.getKey().equals("allOf")){
                        JsonNode refValue = field.getValue();
                        schemeDescriber.allOfs.add(new PathToNode(currentPath, refValue));
                        memory.push(refValue);
                        pathStack.push(currentPath + "/" + field.getKey());
                    }

                    int i=0;
                    while(elements.hasNext()){
                        memory.push(elements.next());
                        pathStack.push(currentPath + "/" + field.getKey() + "/" + i++);
                    }

                } else {
                    if(field.getKey().equals("$ref")){
                        JsonNode refValue = field.getValue();
                        try {
                            if(ReferenceType.isLocalReference(new URI(refValue.asText()))){
                                schemeDescriber.localRefs.add(new PathToNode(currentPath, refValue));
                            } else {
                                schemeDescriber.remoteRefs.add(new PathToNode(currentPath, refValue));
                            }
                        } catch (URISyntaxException e) {
                            throw new ReferenceException("ref - '" + refValue + "' is invalid");
                        }
                    }  else {
                        memory.push(field.getValue());
                        pathStack.push(currentPath + "/" + field.getKey());
                    }
                }
            }
        }
        return schemeDescriber;

    }

    public Set<PathToNode> getLocalRefs() {
        return localRefs;
    }

    public Set<PathToNode> getRemoteRefs() {
        return remoteRefs;
    }

    public Set<PathToNode> getAllOfs() {
        return allOfs;
    }
}
