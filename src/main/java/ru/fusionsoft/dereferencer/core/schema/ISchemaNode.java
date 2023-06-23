package ru.fusionsoft.dereferencer.core.schema;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.routing.ref.JsonPtr;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;

public interface ISchemaNode {
    public JsonNode asJson() throws DereferenceException;

    public Reference getCanonicalReference() throws DereferenceException;

    public ISchemaNode getSchemaNodeByJsonPointer(JsonPtr jsonPointer) throws DereferenceException;

    public void resolve() throws DereferenceException;

    public void delegate(JsonPtr childPtr, ISchemaNode childSchema) throws DereferenceException;

    public SchemaType getSchemaType() throws DereferenceException;

    public Route getSchemaRoute() throws DereferenceException;

    public SchemaStatus getStatus() throws DereferenceException;

    public ISchemaNode getSchemaNode() throws DereferenceException;
}
