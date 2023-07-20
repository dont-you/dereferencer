package ru.fusionsoft.dereferencer.core.schema.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.routing.ref.JsonPtr;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.schema.SchemaNode;
import ru.fusionsoft.dereferencer.core.SchemaLoader;
import ru.fusionsoft.dereferencer.core.schema.SchemaStatus;
import ru.fusionsoft.dereferencer.core.schema.SchemaType;
import static ru.fusionsoft.dereferencer.core.schema.SchemaType.*;
import static ru.fusionsoft.dereferencer.core.schema.SchemaStatus.*;

public class MissingSchema implements SchemaNode {
    private final SchemaLoader loader;
    private final Route schemaRoute;
    private SchemaNode presentSchema;
    private SchemaRelatives relatives;

    public MissingSchema(SchemaLoader loader, Route schemaRoute) {
        this.loader = loader;
        this.schemaRoute = schemaRoute;
        presentSchema = null;
        relatives = new SchemaRelatives();
    }

    public void setPresentSchema(SchemaNode presentSchema) throws LoadException {
        presentSchema.setRelatives(relatives);
        this.presentSchema = presentSchema;
    }

    @Override
    public JsonNode asJson() throws LoadException {
        if (presentSchema == null)
            throw new LoadException(
                    "schema with canonical - " + schemaRoute.getCanonical().getUri() + " could not be found");
        else
            return presentSchema.asJson();
    }

    @Override
    public void delegate(JsonPtr childPtr, SchemaNode childSchema) throws LoadException {
        if (presentSchema == null)
            relatives.addChild(childPtr, childSchema);
        else
            presentSchema.delegate(childPtr, childSchema);
    }

    @Override
    public Reference getCanonicalReference() {
        if (presentSchema == null)
            return schemaRoute.getCanonical();
        else
            return presentSchema.getCanonicalReference();
    }

    @Override
    public SchemaNode getSchemaNodeByJsonPointer(JsonPtr jsonPointer) throws LoadException {
        if (presentSchema == null) {
            SchemaNode target = relatives.getChild(jsonPointer);
            if (target == null) {
                target = loader.get(schemaRoute.resolveRelative("#" + jsonPointer.getResolved()),
                        MissingNode.getInstance());
                relatives.addChild(jsonPointer, target);
            }
            return target;
        } else {
            return presentSchema.getSchemaNodeByJsonPointer(jsonPointer);
        }
    }

    @Override
    public Route getSchemaRoute() {
        return schemaRoute;
    }

    @Override
    public SchemaType getSchemaType() throws LoadException {
        if (presentSchema == null)
            return MISSING_SCHEMA;
        else
            return presentSchema.getSchemaType();
    }

    @Override
    public SchemaStatus getStatus() throws LoadException {
        if (presentSchema == null)
            return NOT_RESOLVED;
        else
            return presentSchema.getStatus();
    }

    @Override
    public SchemaNode resolveIfNotResolved() throws LoadException {
        if (presentSchema == null)
            throw new LoadException(
                    "schema with canonical - " + schemaRoute.getCanonical().getUri() + " could not be found");
        else
            return presentSchema.resolveIfNotResolved();
    }

    @Override
    public SchemaNode resolve() throws LoadException {
        if (presentSchema == null)
            throw new LoadException(
                    "schema with canonical - " + schemaRoute.getCanonical().getUri() + " could not be found");
        else
            return presentSchema.resolve();
    }

    @Override
    public SchemaRelatives getSchemaRelatives() throws LoadException {
        if (presentSchema == null)
            return relatives;
        else
            return presentSchema.getSchemaRelatives();
    }

    @Override
    public void setRelatives(SchemaRelatives relatives) throws LoadException {
        this.relatives = relatives;

        if (presentSchema != null)
            presentSchema.setRelatives(relatives);
    }
}
