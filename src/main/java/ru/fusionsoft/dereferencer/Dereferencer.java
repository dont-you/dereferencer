package ru.fusionsoft.dereferencer;

import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.routing.ref.ReferenceFactory;
import ru.fusionsoft.dereferencer.core.SchemaLoader;
import ru.fusionsoft.dereferencer.core.schema.ISchemaNode;

// TODO LIST
//
// - Refactoring
// ---- SchemaNode
// ---- SchemaLoader
//
// - Tests
// ---- write integrations tests && fix bugs
// -------- test simple_scheme.json
// -------- test layer_1_scheme_1.json
// ---- write unit tests && fix bugs
// -------- some....
//
// - Referencing & Retrieving
// ---- Reference factory method create(Reference reference, JsonPtr ptr)
// ---- feat: write GitLabLoader
// ---- feat: logs while resolving relative
//
// - Schema nodes
// ---- write AllOfSchemaNode
// ---- feat: allOf merge is on and of
//
// - Dereferencer class
// ---- feat: method of creating preloaded schemas
//
// - feat: anon dereferencing(SchemaLoader.get(JsonNode), RouteManager.makeAnon())

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

    public void setDereferenceConfiguration(DereferenceConfiguration cfg) throws LoadException {
        schemaLoader.setDereferenceConfiguraion(cfg);
    }

    private static JsonNode executeDereference(SchemaLoader loader, URI uri) throws LoadException {
        ISchemaNode resultNode = loader.get(ReferenceFactory.create(uri));
        return resultNode.asJson();
    }

    private static JsonNode executeAnonymousDereference(SchemaLoader loader, JsonNode node)
            throws LoadException {
        ISchemaNode resultNode = loader.get(node);
        return resultNode.asJson();
    }

}
