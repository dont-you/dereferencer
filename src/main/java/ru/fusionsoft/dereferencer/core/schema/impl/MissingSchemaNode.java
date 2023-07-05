package ru.fusionsoft.dereferencer.core.schema.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.UnresolvableSchemaException;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.routing.ref.JsonPtr;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.routing.ref.ReferenceFactory;
import ru.fusionsoft.dereferencer.core.schema.ISchemaNode;
import ru.fusionsoft.dereferencer.core.SchemaLoader;
import ru.fusionsoft.dereferencer.core.schema.SchemaStatus;
import ru.fusionsoft.dereferencer.core.schema.SchemaType;
import static ru.fusionsoft.dereferencer.core.schema.SchemaType.*;
import static ru.fusionsoft.dereferencer.core.schema.SchemaStatus.*;

public class MissingSchemaNode implements ISchemaNode {
    private SchemaLoader loader;
    private Route schemaRoute;
    private ISchemaNode presentSchema;
    private Map<JsonPtr, ISchemaNode> childs;

    public MissingSchemaNode(SchemaLoader loader, Route schemaRoute) {
        this.loader = loader;
        this.schemaRoute = schemaRoute;
        presentSchema = null;
        childs = new HashMap<>();
    }

    public void setPresentSchema(ISchemaNode presentSchema) throws LoadException {
        this.presentSchema = presentSchema;
        for (Entry<JsonPtr, ISchemaNode> child : childs.entrySet()) {
            presentSchema.delegate(child.getKey(), child.getValue());
        }
        childs.clear();
    }

    @Override
    public JsonNode asJson() throws LoadException {
        if (presentSchema == null)
            throw new UnresolvableSchemaException(""); // TODO describe exception
        else
            return presentSchema.asJson();
    }

    @Override
    public void delegate(JsonPtr childPtr, ISchemaNode childSchema) throws LoadException {
        if (presentSchema == null)
            childs.put(childPtr, childSchema);
        else
            presentSchema.delegate(childPtr, childSchema);
    }

    @Override
    public Reference getCanonicalReference() throws LoadException {
        if (presentSchema == null)
            return schemaRoute.getCanonical();
        else
            return presentSchema.getCanonicalReference();
    }

    @Override
    public ISchemaNode getSchemaNode() throws LoadException {
        return presentSchema;
    }

    @Override
    public ISchemaNode getSchemaNodeByJsonPointer(JsonPtr jsonPointer) throws LoadException {
        if (presentSchema == null) {
            if (childs.containsKey(jsonPointer))
                return childs.get(jsonPointer);

            Optional<JsonPtr> superSet = childs.keySet().stream().filter((e) -> {
                return e.isSuperSetTo(jsonPointer);
            }).findAny();
            if (!superSet.isEmpty())
                return childs.get(superSet.get()).getSchemaNodeByJsonPointer(jsonPointer);

            ISchemaNode createdSubSchema = loader.get(
                                                      ReferenceFactory.create(schemaRoute.getCanonical(), jsonPointer),
                                                      null);
            childs.put(jsonPointer, createdSubSchema);
            return createdSubSchema;
        } else {
            return presentSchema.getSchemaNodeByJsonPointer(jsonPointer);
        }
    }

    @Override
    public Route getSchemaRoute() throws LoadException {
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
    public void resolve() throws LoadException {
        if (presentSchema == null)
            throw new UnresolvableSchemaException(""); // TODO
        else
            presentSchema.resolve();
    }
}
