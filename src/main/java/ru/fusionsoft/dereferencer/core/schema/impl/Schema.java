package ru.fusionsoft.dereferencer.core.schema.impl;

import static ru.fusionsoft.dereferencer.core.schema.SchemaStatus.NOT_RESOLVED;
import static ru.fusionsoft.dereferencer.core.schema.SchemaStatus.PROCESSING;
import static ru.fusionsoft.dereferencer.core.schema.SchemaStatus.RESOLVED;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.routing.ref.JsonPtr;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.SchemaLoader;
import ru.fusionsoft.dereferencer.core.schema.SchemaNode;
import ru.fusionsoft.dereferencer.core.schema.SchemaStatus;
import ru.fusionsoft.dereferencer.core.schema.SchemaType;

import static ru.fusionsoft.dereferencer.core.schema.SchemaType.*;

public class Schema implements SchemaNode {
    protected int id;
    protected SchemaLoader loader;
    protected Route schemaRoute;
    protected JsonNode sourceJson;
    protected JsonNode resolvedJson;
    protected SchemaRelatives relatives;
    protected SchemaStatus status;

    public Schema(SchemaLoader loader, Route schemaRoute, JsonNode sourceJson) {
        this.loader = loader;
        this.schemaRoute = schemaRoute;
        this.sourceJson = sourceJson;
        this.resolvedJson = null;
        this.relatives = new SchemaRelatives();
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
                relatives.getAllChildren().forEach((k, v) -> {
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
    public SchemaNode getSchemaNodeByJsonPointer(JsonPtr jsonPointer)
            throws LoadException {
        SchemaNode target = relatives.getChild(jsonPointer);
        if (target == null) {
            if (jsonPointer.isResolved())
                target = loader.get(schemaRoute.resolveRelative("#" + jsonPointer.getResolved()),
                        sourceJson.at(jsonPointer.getResolved()));
            else
                target = loader.get(schemaRoute.resolveRelative("#" + jsonPointer.getPlainName()),
                        MissingNode.getInstance());

            relatives.addChild(jsonPointer, target);
        }
        return target;
    }

    @Override
    public SchemaNode resolveIfNotResolved() throws LoadException {
        if (status == SchemaStatus.NOT_RESOLVED)
            return resolve();
        else
            return this;
    }

    @Override
    public SchemaNode resolve() throws LoadException {
        loader.getLogger()
                .info("schema $" + id + " STARTED the PROCESSING");
        status = PROCESSING;
        executeResolving();
        status = RESOLVED;
        loader.getLogger().info("schema $" + id + " IS RESOLVED");
        return this;
    }

    @Override
    public void delegate(JsonPtr childPtr, SchemaNode childSchema) throws LoadException {
        MissingSchema missedDelegate = (MissingSchema) childSchema;
        JsonNode jsonNode = sourceJson.at(childPtr.getResolved());

        if (!jsonNode.isMissingNode()) {
            missedDelegate.setPresentSchema(
                    loader.get(schemaRoute.resolveRelative("#" + childPtr.getResolved()), jsonNode));
        } else {
            relatives.resolveMeLater.put(childPtr, childSchema);
        }
    }

    @Override
    public SchemaType getSchemaType() {
        return DEFAULT_SCHEMA;
    }

    protected void executeResolving() throws LoadException {
        Set<String> processedKeywords = new HashSet<>(Arrays.asList("$ref", "$id", "$anchor", "allOf"));
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

                    if (fieldKey.equals("$ref")) {
                        // relatives.addChild(new JsonPtr(currentPath),
                        // loader.get(schemaRoute.resolveRelative(fieldValue.asText())));
                        // String fieldValueLiteral = fieldValue.asText();
                        // Reference ref = fieldValueLiteral.startsWith("#")
                        //         ? schemaRoute.resolveRelative(resolveJsonPtrFromRef(fieldValueLiteral,currentPath))
                        //         : schemaRoute.resolveRelative(fieldValueLiteral);
                        Reference ref = schemaRoute.resolveRelative(currentPath, fieldValue.asText());

                        relatives.addChild(new JsonPtr(currentPath),
                                loader.get(ref));
                        continue;
                    } else if (!currentPath.isEmpty() && fieldKey.equals("allOf")) {
                        Reference ref = schemaRoute.resolveRelative("#" + currentPath);
                        relatives.addChild(ref.getJsonPtr(), loader.get(ref, currentNode));
                        continue;
                    } else if (!currentPath.isEmpty() && fieldKey.equals("$id")) {
                        relatives.addChild(new JsonPtr(currentPath),
                                loader.get(schemaRoute.resolveRelative(currentPath), currentNode));
                        continue;
                    } else if (!currentPath.isEmpty() && fieldKey.equals("$anchor")) {
                        Reference ref = schemaRoute.resolveRelative("#" + currentPath);
                        ref.getJsonPtr().setPlainName(fieldValue.asText());
                        relatives.addChild(ref.getJsonPtr(), loader.get(ref, currentNode));
                        continue;
                    }
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

    protected JsonPtr resolveJsonPtrFromRef(String refValue, String pathToValue) throws LoadException{
        String pathFromValue = refValue.substring(1);
        String[] parts = pathFromValue.split("/");

        try{
            Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return new JsonPtr(pathFromValue);
        }

        String currentPath = pathToValue;
        for(String key: parts){
            try{
                int upLevelTo = Integer.parseInt(key);
                for(int i=0 ; i < upLevelTo ; i++){
                    currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
                }
            } catch (NumberFormatException e) {
                currentPath += "/" + key;
            }
        }

        return new JsonPtr(currentPath);
    }

    protected boolean isRelativeSchemaTo(SchemaNode intendedRelativeSchema) throws LoadException {
        return intendedRelativeSchema.getCanonicalReference().equals(this.getCanonicalReference());
    }

    public Route getSchemaRoute() {
        return schemaRoute;
    }

    public SchemaStatus getStatus() {
        return status;
    }

    @Override
    public SchemaRelatives getSchemaRelatives() throws LoadException {
        return relatives;
    }

    @Override
    public void setRelatives(SchemaRelatives relatives) throws LoadException {
        this.relatives = relatives;
    }
}
