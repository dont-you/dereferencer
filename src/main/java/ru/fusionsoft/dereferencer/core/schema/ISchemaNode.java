package ru.fusionsoft.dereferencer.core.schema;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.routing.ref.JsonPtr;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;

public interface ISchemaNode {
    JsonNode asJson() throws LoadException;

    Reference getCanonicalReference() throws LoadException;

    ISchemaNode getSchemaNodeByJsonPointer(JsonPtr jsonPointer) throws LoadException;

    void resolve() throws LoadException;

    void delegate(JsonPtr childPtr, ISchemaNode childSchema) throws LoadException;

    SchemaType getSchemaType() throws LoadException;

    Route getSchemaRoute() throws LoadException;

    SchemaStatus getStatus() throws LoadException;
}
