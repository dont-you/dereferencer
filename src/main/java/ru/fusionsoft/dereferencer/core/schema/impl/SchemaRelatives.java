package ru.fusionsoft.dereferencer.core.schema.impl;

import static ru.fusionsoft.dereferencer.core.schema.SchemaType.MISSING_SCHEMA;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.routing.ref.JsonPtr;
import ru.fusionsoft.dereferencer.core.schema.SchemaNode;

public class SchemaRelatives{
    final Set<SchemaNode> parents;
    final Set<SchemaNode> grandChildren;
    final Map<JsonPtr, SchemaNode> children;
    final Map<JsonPtr, SchemaNode> resolveMeLater;

    SchemaRelatives() {
        this.parents = new HashSet<>();
        this.grandChildren = new HashSet<>();
        this.children = new HashMap<>();
        this.resolveMeLater = new HashMap<>();
    }

    public void addChild(JsonPtr addedSchemaPtr, SchemaNode addedSchema) throws LoadException {
        if (addedSchema.getSchemaType() == MISSING_SCHEMA)
            resolveMeLater.put(addedSchemaPtr, addedSchema);
        else
            children.put(addedSchemaPtr, addedSchema);

        Iterator<Entry<JsonPtr, SchemaNode>> iter = resolveMeLater.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<JsonPtr, SchemaNode> notResolvedChild = iter.next();
            JsonPtr lostPtr = notResolvedChild.getKey();
            SchemaNode lostSchema = notResolvedChild.getValue();

            if (addedSchemaPtr.equals(lostPtr)) {
                ((MissingSchema) lostSchema).setPresentSchema(addedSchema);
                iter.remove();
            } else if (addedSchemaPtr.isSuperSetTo(lostPtr)) {
                addedSchema.delegate(addedSchemaPtr.subtractPtr(lostPtr), lostSchema);
                iter.remove();
            }
        }
    }

    public SchemaNode getChild(JsonPtr targetPtr) throws LoadException {
        Iterator<Entry<JsonPtr, SchemaNode>> iter = children.entrySet().iterator();

        while (iter.hasNext()) {
            Entry<JsonPtr, SchemaNode> child = iter.next();
            JsonPtr ptr = child.getKey();
            SchemaNode schema = child.getValue();

            if (ptr.equals(targetPtr))
                return schema;
            else if (ptr.isSuperSetTo(targetPtr))
                return schema.getSchemaNodeByJsonPointer(ptr.subtractPtr(targetPtr));
        }

        return null;

    }

    public Map<JsonPtr, SchemaNode> getAllChildren() {
        return Stream.of(children, resolveMeLater).flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
