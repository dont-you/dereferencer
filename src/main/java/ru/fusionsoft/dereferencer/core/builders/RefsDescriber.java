package ru.fusionsoft.dereferencer.core.builders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.builders.paths.PathToRef;
import ru.fusionsoft.dereferencer.enums.ReferenceType;
import ru.fusionsoft.dereferencer.exception.ReferenceException;

public class RefsDescriber{
    private Set<PathToRef> localRefs = new HashSet<>();
    private Set<PathToRef> remoteRefs = new HashSet<>();

    public static RefsDescriber describe(JsonNode rootNode) throws ReferenceException{
        return findReferences(rootNode);
    }

    private static RefsDescriber findReferences(JsonNode rootNode) throws ReferenceException{
        RefsDescriber refsDescriber = new RefsDescriber();
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

                    int i=0;
                    while(elements.hasNext()){
                        memory.push(elements.next());
                        pathStack.push(currentPath + "/" + field.getKey() + "/" + i++);
                    }

                } else {
                    if(field.getKey().equals("$ref")){
                        String refValue = field.getValue().asText();
                        try {
                            if(ReferenceType.isLocalReference(new URI(refValue))){
                                refsDescriber.localRefs.add(new PathToRef(currentPath, refValue));
                            } else {
                                refsDescriber.remoteRefs.add(new PathToRef(currentPath, refValue));
                            }
                        } catch (URISyntaxException e) {
                            throw new ReferenceException("ref - '" + refValue + "' is invalid");
                        }
                    } else {
                        memory.push(field.getValue());
                        pathStack.push(currentPath + "/" + field.getKey());
                    }
                }
            }
        }
        return refsDescriber;

    }

    public Set<PathToRef> getLocalRefs() {
        return localRefs;
    }

    public Set<PathToRef> getRemoteRefs() {
        return remoteRefs;
    }
}
