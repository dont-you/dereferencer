package ru.fusionsoft.dereferencer;

import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.routing.ref.ReferenceFactory;
import ru.fusionsoft.dereferencer.core.SchemaLoader;
import ru.fusionsoft.dereferencer.core.schema.SchemaNode;

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
        schemaLoader.setDereferenceConfiguration(cfg);
    }

    private static JsonNode executeDereference(SchemaLoader loader, URI uri) throws LoadException {
        SchemaNode resultNode = loader.get(ReferenceFactory.create(uri));
        return resultNode.asJson();
    }

    private static JsonNode executeAnonymousDereference(SchemaLoader loader, JsonNode node)
            throws LoadException {
        SchemaNode resultNode = loader.get(node);
        return resultNode.asJson();
    }

}
