package ru.fusionsoft.dereferencer.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.RetrievingException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;
import ru.fusionsoft.dereferencer.core.schema.SchemaNode;
import ru.fusionsoft.dereferencer.core.schema.SchemaStatus;
import ru.fusionsoft.dereferencer.core.load.RetrievalManager;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.routing.RouteManager;
import ru.fusionsoft.dereferencer.core.routing.ref.JsonPtr;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.routing.ref.ReferenceFactory;
import ru.fusionsoft.dereferencer.core.schema.impl.AllOfSchema;
import ru.fusionsoft.dereferencer.core.schema.impl.MissingSchema;
import ru.fusionsoft.dereferencer.core.schema.impl.Schema;

public class SchemaLoader {
    private final RouteManager routeManager;
    private final RetrievalManager retrievalManager;
    private Logger logger;
    private LoadingCache<Route, SchemaNode> cache;
    private int countCreatedSchemas;
    private Set<LoadingFlag> flags;

    public SchemaLoader(LoadConfiguration cfg) throws LoadException {
        flags = new HashSet<>(Arrays.asList(cfg.getLoadingFlags()));
        countCreatedSchemas = 0;
        logger = cfg.getLogger();
        routeManager = new RouteManager(cfg.getDefaultBaseUri(), cfg.getPreloadedSchemas().keySet(), logger);
        retrievalManager = new RetrievalManager(cfg.getLoaderFactory(), logger);
        setCache(cfg.getCashSize());
        cache.putAll(cfg.getPreloadedSchemas());
    }

    public SchemaNode get(Reference reference) throws LoadException {
        if (reference.isContainsFragment()) {
            URI absolute = reference.getAbsolute();
            JsonPtr jsonPtr = reference.getJsonPtr();
            return getFromCache(routeManager.getRoute(ReferenceFactory.create(absolute)))
                    .getSchemaNodeByJsonPointer(jsonPtr);
        } else {
            return getFromCache(routeManager.getRoute(reference));
        }
    }

    public SchemaNode get(Reference reference, JsonNode node)
            throws LoadException {
        Route routeToSchema = routeManager.getRoute(reference);

        if (node.isMissingNode()) {
            return new MissingSchema(this, routeToSchema);
        }

        SchemaNode targetNode = createSchema(routeToSchema, node);
        cache.put(targetNode.getSchemaRoute(), targetNode);
        logger.info("successful loading schema into cache with current canonical uri - "
                + targetNode.getSchemaRoute().getCanonical().getUri());
        targetNode.resolve();
        return targetNode;
    }

    public SchemaNode get(JsonNode node) throws LoadException {
        return null;
    }

    private SchemaNode getFromCache(Route schemaRoute) throws LoadException {
        try {
            SchemaNode targetNode = cache.get(schemaRoute);

            if (targetNode.getStatus() == SchemaStatus.NOT_RESOLVED)
                targetNode.resolve();

            return targetNode;
        } catch (ExecutionException e) {
            throw handleException(e);
        }
    }

    public void setDereferenceConfiguration(LoadConfiguration cfg) throws LoadException {
        logger = cfg.getLogger();
        routeManager.setDefaultBaseUri(cfg.getDefaultBaseUri()).setPreloadedRoutes(cfg.getPreloadedSchemas().keySet())
                .setLogger(logger);
        retrievalManager.setLoaderFactory(cfg.getLoaderFactory()).setLogger(logger);
        setCache(cfg.getCashSize());
        cache.putAll(cfg.getPreloadedSchemas());
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setCache(int cacheSize) {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();

        if (cacheSize != -1) {
            builder.maximumSize(cacheSize);
        }

        cache = builder.build(new CacheLoader<>() {
            @Override
            public SchemaNode load(Route key) throws LoadException {
                SchemaNode SchemaNode = createSchema(key, retrievalManager.retrieve(key));
                logger.info("successful loading schema into cache with current canonical uri - "
                        + key.getCanonical().getUri());
                return SchemaNode;
            }

        });
    }

    private SchemaNode createSchema(Route route, JsonNode source) throws LoadException {
        SchemaNode targetNode;
        String lastCanonical = route.getCanonical().getUri().toString();

        if (source.has("$id")) {
            try {
                route.setCanonical(ReferenceFactory.create(new URI(source.at("/$id").asText())));
                logger.info("canonical change by embedded in content uri: {"
                        + "\tfrom - " + lastCanonical
                        + "\tto - " + route.getCanonical().getUri()
                        + "}");

                SchemaNode alreadyExistingSchema = cache.getIfPresent(route);
                if (alreadyExistingSchema != null)
                    return alreadyExistingSchema;
            } catch (URISyntaxException e) {
                throw new URIException(
                        "embedded in content uri of schema with retrieval uri " + lastCanonical + " contains errors");
            }
        }

        if (source.has("allOf") && flags.contains(LoadingFlag.MERGE_ALL_OF))
            targetNode = new AllOfSchema(this, route, source);
        else
            targetNode = new Schema(this, route, source);

        countCreatedSchemas++;
        return targetNode;
    }

    public int getCountCreatedSchemas() {
        return countCreatedSchemas;
    }

    private static LoadException handleException(Exception e) {
        Throwable t = e.getCause();
        if (t instanceof RetrievingException) {
            return (RetrievingException) t;
        } else if (t instanceof URIException) {
            return (URIException) t;
        } else if (t instanceof UnknownException) {
            return (UnknownException) t;
        } else {
            return (LoadException) t;
        }
    }
}
