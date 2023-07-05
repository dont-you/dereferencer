package ru.fusionsoft.dereferencer.core.schema;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.routing.ref.JsonPtr;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;

public interface ISchemaNode {
    public JsonNode asJson() throws LoadException;

    public Reference getCanonicalReference() throws LoadException;

    public ISchemaNode getSchemaNodeByJsonPointer(JsonPtr jsonPointer) throws LoadException;

    public void resolve() throws LoadException;

    public void delegate(JsonPtr childPtr, ISchemaNode childSchema) throws LoadException;

    public SchemaType getSchemaType() throws LoadException;

    public Route getSchemaRoute() throws LoadException;

    public SchemaStatus getStatus() throws LoadException;

    public ISchemaNode getSchemaNode() throws LoadException;
}
