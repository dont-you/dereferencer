package ru.fusionsoft.dereferencer.core.builders.paths;

import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;

public class PathToNode {
    private final String pathToRef;
    private final JsonNode node;
    private final int hash;

    public PathToNode(String pathToRef, JsonNode node){
        this.pathToRef = pathToRef;
        this.node = node;
        this.hash = Objects.hash(pathToRef, node);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if(!(obj instanceof PathToNode))
            return false;

        if(hash != obj.hashCode())
            return false;

        if(!pathToRef.equals(((PathToNode) obj).pathToRef))
            return false;
        if(!node.equals(((PathToNode) obj).node))
            return false;

        return true;
    }

    public String getPathToRef() {
        return pathToRef;
    }

    public JsonNode getNode() {
        return node;
    }
}
