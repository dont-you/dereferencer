package ru.fusionsoft.dereferencer.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
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
import ru.fusionsoft.dereferencer.core.schema.ISchemaNode;
import ru.fusionsoft.dereferencer.core.schema.SchemaStatus;
import ru.fusionsoft.dereferencer.core.load.RetrievalManager;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.routing.RouteManager;
import ru.fusionsoft.dereferencer.core.routing.ref.JsonPtr;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.routing.ref.ReferenceFactory;
import ru.fusionsoft.dereferencer.core.schema.impl.MissingSchemaNode;
import ru.fusionsoft.dereferencer.core.schema.impl.SchemaNode;

public class SchemaLoader {
    private final RouteManager routeManager;
    private final RetrievalManager retrievalManager;
    private Logger logger;
    private Map<Route, ISchemaNode> preloadedSchemas;
    private LoadingCache<Route, ISchemaNode> cache;
    private int countCreatedSchemas;

    public SchemaLoader(LoadConfiguration cfg) throws LoadException {
        countCreatedSchemas = 0;
        preloadedSchemas = cfg.getPreloadedSchemas();
        logger = cfg.getLogger();
        routeManager = new RouteManager(cfg.getDefaultBaseUri(), preloadedSchemas.keySet(), logger);
        retrievalManager = new RetrievalManager(cfg.getLoaderFactory(), logger);
        setCache(cfg.getCashSize());
    }

    public ISchemaNode get(Reference reference) throws LoadException {
        if (reference.isContainsFragment()) {
            URI absolute = reference.getAbsolute();
            JsonPtr jsonPtr = reference.getJsonPtr();
            return getFromCache(routeManager.getRoute(ReferenceFactory.create(absolute)))
                    .getSchemaNodeByJsonPointer(jsonPtr);
        } else {
            return getFromCache(routeManager.getRoute(reference));
        }
    }

    public ISchemaNode get(Reference reference, JsonNode node)
            throws LoadException {
        Route routeToSchema = routeManager.getRoute(reference);

        if (node.isMissingNode()) {
            return new MissingSchemaNode(this, routeToSchema);
        }

        ISchemaNode targetNode = createSchema(routeToSchema, node);
        cache.put(targetNode.getSchemaRoute(), targetNode);
        logger.info("successful loading schema into cache with current canonical uri - "
                + targetNode.getSchemaRoute().getCanonical().getUri());
        targetNode.resolve();
        return targetNode;
    }

    public ISchemaNode get(JsonNode node) throws LoadException {
        // TODO make anon schemas
        return null;
    }

    private ISchemaNode getFromCache(Route schemaRoute) throws LoadException {
        ISchemaNode targetNode;
        if (preloadedSchemas.containsKey(schemaRoute)) {
            targetNode = preloadedSchemas.get(schemaRoute);
        } else {
            try {
                targetNode = cache.get(schemaRoute);
            } catch (ExecutionException e) {
                throw handleException(e);
            }
            if (targetNode.getStatus() == SchemaStatus.NOT_RESOLVED)
                targetNode.resolve();
        }

        return targetNode;
    }

    public void setDereferenceConfiguration(LoadConfiguration cfg) throws LoadException {
        logger = cfg.getLogger();
        routeManager.setDefaultBaseUri(cfg.getDefaultBaseUri()).setPreloadedRoutes(cfg.getPreloadedSchemas().keySet())
                .setLogger(logger);
        retrievalManager.setLoaderFactory(cfg.getLoaderFactory()).setLogger(logger);
        preloadedSchemas = cfg.getPreloadedSchemas();
        setCache(cfg.getCashSize());
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setPreloadedSchemas(Map<Route, ISchemaNode> preloadedSchemas) {
        this.preloadedSchemas = preloadedSchemas;
    }

    public void setCache(int cacheSize) {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();

        if (cacheSize != -1) {
            builder.maximumSize(cacheSize);
        }

        cache = builder.build(new CacheLoader<>() {
            @Override
            public ISchemaNode load(Route key) throws LoadException {
                ISchemaNode ISchemaNode = createSchema(key, retrievalManager.retrieve(key));
                logger.info("successful loading schema into cache with current canonical uri - "
                        + key.getCanonical().getUri());
                return ISchemaNode;
            }

        });
    }

    private ISchemaNode createSchema(Route route, JsonNode source) throws LoadException {
        ISchemaNode targetNode;
        String lastCanonical = route.getCanonical().getUri().toString();

        if (source.has("$id")) {
            try {
                route.setCanonical(ReferenceFactory.create(new URI(source.at("/$id").asText())));
                logger.info("canonical change by embedded in content uri: {"
                        + "\tfrom - " + lastCanonical
                        + "\tto - " + route.getCanonical().getUri()
                        + "}");
                if (preloadedSchemas.containsKey(route)) {
                    return preloadedSchemas.get(route);
                }

                ISchemaNode alreadyExistingSchema = cache.getIfPresent(route);
                if (alreadyExistingSchema != null)
                    return alreadyExistingSchema;
            } catch (URISyntaxException e) {
                throw new URIException(
                        "embedded in content uri of schema with retrieval uri " + lastCanonical + " contains errors");
            }
        }

        // TODO set
        // if (source.has("allOf")) {
        // // TODO do after writing class AllOfSchemaNode
        // targetNode = null;
        // // targetNode = new AllOfSchemaNode(this, routeToSchema);
        // } else {
        // targetNode = new SchemaNode(this, route, source, false);
        // }

        // TODO remove
        targetNode = new SchemaNode(this, route, source, false);

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
