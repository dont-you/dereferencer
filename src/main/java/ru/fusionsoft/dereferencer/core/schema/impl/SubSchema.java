package ru.fusionsoft.dereferencer.core.schema.impl;

import com.fasterxml.jackson.core.JsonPointer;

import ru.fusionsoft.dereferencer.core.schema.SchemaNode;

public class SubSchema extends SchemaNode{
    public SuperSchema superSchema;

    @Override
    public SchemaNode getSchemaNodeByJsonPointer(JsonPointer jsonPointer){
        // TODO
        return null;
    }

}
