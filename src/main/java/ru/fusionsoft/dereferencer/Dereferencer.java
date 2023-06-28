package ru.fusionsoft.dereferencer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;
import ru.fusionsoft.dereferencer.core.exceptions.UnresolvableSchemaException;
import ru.fusionsoft.dereferencer.core.routing.ref.ReferenceFactory;
import ru.fusionsoft.dereferencer.core.schema.SchemaLoader;
import ru.fusionsoft.dereferencer.core.schema.ISchemaNode;

// TODO LIST
//
// - errors handling
// ---- ...
//
// - refactoring
// ---- make SourceLoader implementations not singletone
// ---- remove Reference implementations and make Reference class not abstract with field SourceLoader
// ---- ShcemaNode - refactor executeResolving method
//
// - test
// ---- write integration tests and fix bugs
//
// - referencing & retrieving
// ---- JsonPtr method equals
// ---- logic of adding token to GitHubLoader
// ---- RouteManager make anonRoute
//
// - Schema nodes
// ---- SchemaLoader finish get methods
// -------- checking for uri-duplicate by cannonical embedded in content uri
// ---- feat: allOf merge is on and of
// ---- write AllOfSchemaNode

public class Dereferencer {
    public static final Properties PROPERTIES;

    static {
        PROPERTIES = new Properties();
        InputStream inputStream = Dereferencer.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SchemaLoader schemaLoader;

    public Dereferencer() throws URIException {
        schemaLoader = new SchemaLoader(DereferenceConfiguration.builder().build());
    }

    public Dereferencer(DereferenceConfiguration cfg) throws URIException {
        schemaLoader = new SchemaLoader(cfg);
    }

    public static JsonNode dereference(URI uri) throws DereferenceException {
        return dereference(uri, DereferenceConfiguration.builder().build());
    }

    public static JsonNode dereference(URI uri, DereferenceConfiguration cfg) throws DereferenceException {
        SchemaLoader schemaLoader = new SchemaLoader(cfg);
        return executeDereference(schemaLoader, uri);
    }

    public static JsonNode anonymousDereference(JsonNode node) throws DereferenceException {
        return anonymousDereference(node, DereferenceConfiguration.builder().build());
    }

    public static JsonNode anonymousDereference(JsonNode node, DereferenceConfiguration cfg)
            throws DereferenceException {
        SchemaLoader schemaLoader = new SchemaLoader(cfg);
        return executeAnonymousDereference(schemaLoader, node);
    }

    public JsonNode deref(URI uri) throws DereferenceException {
        return executeDereference(schemaLoader, uri);
    }

    public JsonNode anonymousDeref(JsonNode node) throws DereferenceException {
        return executeAnonymousDereference(schemaLoader, node);
    }

    public void setDereferenceConfiguration(DereferenceConfiguration cfg) throws URIException {
        schemaLoader.setDereferenceConfiguraion(cfg);
    }

    private static JsonNode executeDereference(SchemaLoader loader, URI uri) throws DereferenceException {
        try {
            ISchemaNode resultNode = loader.get(ReferenceFactory.create(uri));
            return resultNode.asJson();
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    private static JsonNode executeAnonymousDereference(SchemaLoader loader, JsonNode node)
            throws DereferenceException {
        try {
            ISchemaNode resultNode = loader.get(node);
            return resultNode.asJson();
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    private static DereferenceException handleException(Exception e) {
        // TODO do something like this later....
        if (e.getCause() instanceof UnresolvableSchemaException) {
            return (DereferenceException) e;
        } else {
            return new UnknownException(e.getMessage());
        }
    }

}
