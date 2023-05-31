package ru.fusionsoft.dereferencer.core.schema;

import java.util.Map;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;

import ru.fusionsoft.dereferencer.core.load.SchemaLoader;

public abstract class SchemaNode{
    private SchemaLoader loader;
    private Reference ref;
    private JsonNode sourceJson;
    private JsonNode resolvedJson;
    private Map<Reference, SchemaNode> resolvedItems;
    private Map<Reference, SchemaNode> notResolved;

    public static SchemaNode load(Reference reference, SchemaLoader schemaLoader){
        // TODO
        return null;
    }

    public JsonNode asJson(){
        // TODO
        return null;
    }

    protected void retryResolving(){
        // TODO
    }

    public abstract SchemaNode getSchemaNodeByJsonPointer(JsonPointer jsonPointer);
}
