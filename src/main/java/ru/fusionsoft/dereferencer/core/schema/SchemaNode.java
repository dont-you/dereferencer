package ru.fusionsoft.dereferencer.core.schema;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.routing.ref.JsonPtr;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.schema.impl.SchemaRelatives;

public interface SchemaNode {
    JsonNode asJson() throws LoadException;

    Reference getCanonicalReference();

    SchemaNode getSchemaNodeByJsonPointer(JsonPtr jsonPointer) throws LoadException;

    SchemaNode resolve() throws LoadException;

    SchemaNode resolveIfNotResolved() throws LoadException;

    void delegate(JsonPtr childPtr, SchemaNode childSchema) throws LoadException;

    SchemaType getSchemaType() throws LoadException;

    Route getSchemaRoute() throws LoadException;

    SchemaStatus getStatus() throws LoadException;

    SchemaRelatives getSchemaRelatives() throws LoadException;

    void setRelatives(SchemaRelatives relatives) throws LoadException;
}
