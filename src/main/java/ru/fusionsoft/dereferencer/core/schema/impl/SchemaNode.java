package ru.fusionsoft.dereferencer.core.schema.impl;

import static ru.fusionsoft.dereferencer.core.schema.SchemaStatus.NOT_RESOLVED;
import static ru.fusionsoft.dereferencer.core.schema.SchemaStatus.PROCESSING;
import static ru.fusionsoft.dereferencer.core.schema.SchemaStatus.RESOLVED;
import static ru.fusionsoft.dereferencer.core.schema.SchemaType.MISSING_SCHEMA;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.routing.ref.JsonPtr;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.schema.ISchemaNode;
import ru.fusionsoft.dereferencer.core.SchemaLoader;
import ru.fusionsoft.dereferencer.core.schema.SchemaStatus;
import ru.fusionsoft.dereferencer.core.schema.SchemaType;

import static ru.fusionsoft.dereferencer.core.schema.SchemaType.*;

public class SchemaNode implements ISchemaNode {
    protected int id;
    protected SchemaLoader loader;
    protected Route schemaRoute;
    protected JsonNode sourceJson;
    protected JsonNode resolvedJson;
    protected SchemaChilds schemaChilds;
    protected boolean mergeAllOfFlag;
    protected SchemaStatus status;

    public SchemaNode(SchemaLoader loader, Route schemaRoute, JsonNode sourceJson, Boolean mergeAllOfFlag) {
        this.loader = loader;
        this.schemaRoute = schemaRoute;
        this.sourceJson = sourceJson;
        this.resolvedJson = null;
        this.schemaChilds = new SchemaChilds();
        this.mergeAllOfFlag = mergeAllOfFlag;
        status = NOT_RESOLVED;
        id = loader.getCountCreatedSchemas() + 1;
        loader.getLogger()
                .info("schema $" + id + " with canonical uri " + schemaRoute.getCanonical().getUri()
                        + " CREATED but NOT RESOLVED");
    }

    @Override
    public JsonNode asJson() throws LoadException {
        if (status == NOT_RESOLVED) {
            throw new LoadException(
                    "unable to represent schema as json, call method 'resolve' and try again");
        }

        try {
            if (resolvedJson == null) {
                resolvedJson = sourceJson;
                schemaChilds.getAllChilds().forEach((k, v) -> {
                    try {
                        JsonNode value = v.asJson();

                        if (k.getResolved().equals("")) {
                            if (value.isObject()) {
                                ObjectNode parent = (ObjectNode) resolvedJson;
                                parent.removeAll();
                                parent.setAll((ObjectNode) value);
                            } else {
                                resolvedJson = value;
                            }
                        } else {
                            JsonNode parent = resolvedJson.at(k.getParent().getResolved());
                            if (parent.isObject())
                                ((ObjectNode) parent).set(k.getPropertyName(), value);
                            else
                                ((ArrayNode) parent).set(Integer.parseInt(k.getPropertyName()), value);
                        }
                    } catch (LoadException e) {
                        throw new RuntimeException();
                    }
                });
            }
        } catch (RuntimeException e) {
            throw new UnknownException(
                    "unknown exception caused while generating json with msg - " + e.getMessage());
        }

        return resolvedJson;
    }

    @Override
    public Reference getCanonicalReference() {
        return schemaRoute.getCanonical();
    }

    @Override
    public ISchemaNode getSchemaNodeByJsonPointer(JsonPtr jsonPointer)
            throws LoadException {
        return schemaChilds.getChild(jsonPointer);
    }

    @Override
    public void resolve() throws LoadException {
        loader.getLogger()
                .info("schema $" + id + " STARTED the PROCESSING");
        status = PROCESSING;
        executeResolving();
        status = RESOLVED;
        loader.getLogger().info("schema $" + id + " IS RESOLVED");
    }

    @Override
    public void delegate(JsonPtr childPtr, ISchemaNode childSchema) throws LoadException {
        MissingSchemaNode missedDelegate = (MissingSchemaNode) childSchema;
        JsonNode jsonNode = sourceJson.at(childPtr.getResolved());

        if (!jsonNode.isMissingNode()) {
            missedDelegate.setPresentSchema(
                    loader.get(schemaRoute.resolveRelative("#" + childPtr.getResolved()), jsonNode));
        } else {
            schemaChilds.resolveMeLater.put(childPtr, childSchema);
        }
    }

    @Override
    public SchemaType getSchemaType() {
        return DEFAULT_SCHEMA;
    }

    protected void executeResolving() throws LoadException {
        Set<String> processedKeywords = new HashSet<>(Arrays.asList("$ref", "$id", "$anchor"));
        JsonNode currentNode;
        String currentPath;
        Stack<JsonNode> memory = new Stack<>();
        Stack<String> pathStack = new Stack<>();
        memory.push(sourceJson);
        pathStack.push("");

        while (!memory.empty()) {
            currentNode = memory.pop();
            currentPath = pathStack.pop();
            Iterator<Entry<String, JsonNode>> fields = currentNode.fields();

            while (fields.hasNext()) {
                Entry<String, JsonNode> field = fields.next();
                String fieldKey = field.getKey();
                JsonNode fieldValue = field.getValue();

                if (processedKeywords.contains(fieldKey)) {
                    loader.getLogger().info(
                            "in schema $" + id + " found " + fieldKey + " key with value: " + fieldValue.asText());

                    if (fieldKey.equals("$ref"))
                        schemaChilds.addChild(new JsonPtr(currentPath),
                                loader.get(schemaRoute.resolveRelative(fieldValue.asText())));
                    // else if (fieldKey.equals("allOf"))
                    // schemaChilds.addChild(new JsonPtr(currentPath),
                    // loader.get(schemaRoute.resolveRelative(currentPath), fieldValue));
                    else if (!currentPath.isEmpty() && fieldKey.equals("$id"))
                        schemaChilds.addChild(new JsonPtr(currentPath),
                                loader.get(schemaRoute.resolveRelative(currentPath), currentNode));
                    else if (!currentPath.isEmpty() && fieldKey.equals("$anchor"))
                        schemaChilds.addChild(new JsonPtr(currentPath, fieldValue.asText()),
                                loader.get(schemaRoute.resolveRelative(currentPath), currentNode));

                    continue;
                }

                if (fieldKey.contains("/")) {
                    fieldKey = fieldKey.replaceAll("/", "~1");
                } else if (fieldKey.contains("~")) {
                    fieldKey = fieldKey.replaceAll("~", "~0");
                }

                if (fieldValue.isArray()) {
                    Iterator<JsonNode> elements = field.getValue().elements();

                    int i = 0;
                    while (elements.hasNext()) {
                        memory.push(elements.next());
                        pathStack.push(currentPath + "/" + fieldKey + "/" + i++);
                    }
                } else {
                    memory.push(fieldValue);
                    pathStack.push(currentPath + "/" + fieldKey);
                }
            }
        }
    }

    protected boolean isRelativeSchemaTo(ISchemaNode intendedRelativeSchema) throws LoadException {
        return intendedRelativeSchema.getCanonicalReference().equals(this.getCanonicalReference());
    }

    protected class SchemaChilds {
        private final Map<JsonPtr, ISchemaNode> childs;
        private final Map<JsonPtr, ISchemaNode> resolveMeLater;

        SchemaChilds() {
            this.childs = new HashMap<>();
            this.resolveMeLater = new HashMap<>();
        }

        public void addChild(JsonPtr addedSchemaPtr, ISchemaNode addedSchema) throws LoadException {
            childs.put(addedSchemaPtr, addedSchema);

            Iterator<Entry<JsonPtr, ISchemaNode>> iter = resolveMeLater.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<JsonPtr, ISchemaNode> notResolvedChild = iter.next();
                JsonPtr lostPtr = notResolvedChild.getKey();
                ISchemaNode lostSchema = notResolvedChild.getValue();

                if (addedSchemaPtr.equals(lostPtr)) {
                    ((MissingSchemaNode) lostSchema).setPresentSchema(addedSchema);
                    iter.remove();
                } else if (addedSchemaPtr.isSuperSetTo(lostPtr)) {
                    addedSchema.delegate(addedSchemaPtr.subtractPtr(lostPtr), lostSchema);
                    iter.remove();
                }
            }
        }

        public ISchemaNode getChild(JsonPtr targetPtr) throws LoadException {
            Iterator<Entry<JsonPtr, ISchemaNode>> iter = childs.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<JsonPtr, ISchemaNode> child = iter.next();
                JsonPtr ptr = child.getKey();
                ISchemaNode schema = child.getValue();

                if (ptr.equals(targetPtr))
                    return schema;
                else if (ptr.isSuperSetTo(targetPtr))
                    return schema.getSchemaNodeByJsonPointer(ptr.subtractPtr(targetPtr));
            }

            ISchemaNode createdSubSchema = loader.get(schemaRoute.resolveRelative("#" + targetPtr.getResolved()),
                    targetPtr.isResolved() ? sourceJson.at(targetPtr.getResolved()) : MissingNode.getInstance());

            if (createdSubSchema.getSchemaType() == MISSING_SCHEMA)
                resolveMeLater.put(targetPtr, createdSubSchema);
            else
                childs.put(targetPtr, createdSubSchema);

            return createdSubSchema;
        }

        public Map<JsonPtr, ISchemaNode> getAllChilds() {
            return Stream.of(childs, resolveMeLater).flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

    public Route getSchemaRoute() {
        return schemaRoute;
    }

    public SchemaStatus getStatus() {
        return status;
    }

}
