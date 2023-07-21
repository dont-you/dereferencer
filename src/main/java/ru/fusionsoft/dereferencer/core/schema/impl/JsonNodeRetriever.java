package ru.fusionsoft.dereferencer.core.schema.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.TextNode;

import ru.fusionsoft.dereferencer.core.routing.ref.JsonPtr;

public class JsonNodeRetriever{
    public static JsonNode retrieve(JsonNode node, JsonPtr ptr){
        if (ptr.isResolved()){
            String path = ptr.getResolved();
            boolean isPropertyName = path.endsWith("#");
            if(isPropertyName)
                return TextNode.valueOf(path.substring(path.lastIndexOf("/"),path.length()-1));
            else
                return node.at(path);
        } else {
            return MissingNode.getInstance();
        }

    }
}
