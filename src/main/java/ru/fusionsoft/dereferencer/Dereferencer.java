package ru.fusionsoft.dereferencer;

import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;
import ru.fusionsoft.dereferencer.core.exceptions.UnresolvableSchemaException;
import ru.fusionsoft.dereferencer.core.routing.ref.ReferenceFactory;
import ru.fusionsoft.dereferencer.core.SchemaLoader;
import ru.fusionsoft.dereferencer.core.schema.ISchemaNode;

// TODO LIST
//
// - Error handling
// ---- ...
//
// - Refactoring
// ---- SchemaNode
// ---- SchemaLoader
//
// - Tests
// ---- write integrations tests && fix bugs
// -------- fix: bugs from test in commit b1343bc
// ---- write unit tests && fix bugs
// -------- some....
//
// - Referencing & Retrieving
// ---- RouteManager make anonRoute
// ---- feat: write GitLabLoader
// ---- feat: logs while resolving relative
//
// - Schema nodes
// ---- feat: allOf merge is on and of
// ---- write AllOfSchemaNode
//
// - Dereferencer class
// ---- feat: method of creating preloaded schemas
//
// - perf: prevention of resource duplicates

public class Dereferencer {

    private SchemaLoader schemaLoader;

    public Dereferencer() throws LoadException {
        schemaLoader = new SchemaLoader(DereferenceConfiguration.builder().build());
    }

    public Dereferencer(DereferenceConfiguration cfg) throws LoadException {
        schemaLoader = new SchemaLoader(cfg);
    }

    public static JsonNode deref(URI uri) throws LoadException {
        return deref(uri, DereferenceConfiguration.builder().build());
    }

    public static JsonNode deref(URI uri, DereferenceConfiguration cfg) throws LoadException {
        SchemaLoader schemaLoader = new SchemaLoader(cfg);
        return executeDereference(schemaLoader, uri);
    }

    public static JsonNode anonymousDeref(JsonNode node) throws LoadException {
        return anonymousDeref(node, DereferenceConfiguration.builder().build());
    }

    public static JsonNode anonymousDeref(JsonNode node, DereferenceConfiguration cfg)
            throws LoadException {
        SchemaLoader schemaLoader = new SchemaLoader(cfg);
        return executeAnonymousDereference(schemaLoader, node);
    }

    public JsonNode dereference(URI uri) throws LoadException {
        return executeDereference(schemaLoader, uri);
    }

    public JsonNode anonymousDereference(JsonNode node) throws LoadException {
        return executeAnonymousDereference(schemaLoader, node);
    }

    public void setDereferenceConfiguration(DereferenceConfiguration cfg) throws URIException {
        schemaLoader.setDereferenceConfiguraion(cfg);
    }

    private static JsonNode executeDereference(SchemaLoader loader, URI uri) throws LoadException {
        try {
            ISchemaNode resultNode = loader.get(ReferenceFactory.create(uri));
            return resultNode.asJson();
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    private static JsonNode executeAnonymousDereference(SchemaLoader loader, JsonNode node)
            throws LoadException {
        try {
            ISchemaNode resultNode = loader.get(node);
            return resultNode.asJson();
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    private static LoadException handleException(Exception e) {
        // TODO do something like this later....
        if (e.getCause() instanceof UnresolvableSchemaException) {
            return (LoadException) e;
        } else {
            return new UnknownException(e.getMessage());
        }
    }

}
