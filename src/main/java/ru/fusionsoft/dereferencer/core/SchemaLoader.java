package ru.fusionsoft.dereferencer.core;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.schema.ISchemaNode;
import ru.fusionsoft.dereferencer.core.schema.SchemaStatus;
import ru.fusionsoft.dereferencer.core.utils.RetrievalManager;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.routing.RouteManager;
import ru.fusionsoft.dereferencer.core.routing.ref.JsonPtr;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.routing.ref.ReferenceFactory;
import ru.fusionsoft.dereferencer.core.schema.impl.MissingSchemaNode;
import ru.fusionsoft.dereferencer.core.schema.impl.SchemaNode;

public class SchemaLoader {
    private RouteManager routeManager;
    private RetrievalManager retrievalManager;
    private Logger logger;
    private Map<Route, ISchemaNode> preloadedSchemas;
    private LoadingCache<Route, ISchemaNode> cache;
    private int countCreatedSchemas;

    public SchemaLoader(LoadConfiguration cfg) throws LoadException {
        countCreatedSchemas = 0;
        preloadedSchemas = cfg.getPreloadedSchemas();
        logger = cfg.getLogger();
        routeManager = new RouteManager(cfg.getDefaultBaseUri(), preloadedSchemas.keySet(), logger);
        retrievalManager = new RetrievalManager(cfg.getTokens(), logger);
        setCache(cfg.getCashSize());
    }

    public ISchemaNode get(Reference reference) throws ExecutionException, LoadException {
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
            throws ExecutionException, LoadException {
        ISchemaNode targetNode;
        Route routeToSchema = routeManager.getRoute(reference);
        targetNode = createSchema(routeToSchema, node);
        cache.put(targetNode.getSchemaRoute(), targetNode);
        logger.info("successful loading schema into cache with currenct canonical uri - "
                + targetNode.getSchemaRoute().getCanonical().getUri());
        targetNode.resolve();
        return targetNode;
    }

    public ISchemaNode get(JsonNode node) throws LoadException {
        // TODO make anon schemas
        return null;
    }

    private ISchemaNode getFromCache(Route schemaRoute) throws ExecutionException, LoadException {
        ISchemaNode targetNode;
        if (preloadedSchemas.containsKey(schemaRoute)) {
            targetNode = preloadedSchemas.get(schemaRoute);
        } else {
            targetNode = cache.get(schemaRoute);
            if (targetNode.getStatus() == SchemaStatus.NOT_RESOLVED)
                targetNode.resolve();
        }

        return targetNode;
    }

    public void setDereferenceConfiguraion(LoadConfiguration cfg) throws URIException {
        logger = cfg.getLogger();
        routeManager.setDefaultBaseUri(cfg.getDefaultBaseUri()).setPreloadedRoutes(cfg.getPreloadedSchemas().keySet())
                .setLogger(logger);
        retrievalManager.setTokens(cfg.getTokens()).setLogger(logger);
        preloadedSchemas = cfg.getPreloadedSchemas();
        setCache(cfg.getCashSize());
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Map<Route, ISchemaNode> getPreloadedSchemas() {
        return preloadedSchemas;
    }

    public void setPreloadedSchemas(Map<Route, ISchemaNode> preloadedSchemas) {
        this.preloadedSchemas = preloadedSchemas;
    }

    public LoadingCache<Route, ISchemaNode> getCache() {
        return cache;
    }

    public void setCache(int cacheSize) {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();

        if (cacheSize != -1) {
            builder.maximumSize(cacheSize);
        }

        cache = builder.build(new CacheLoader<Route, ISchemaNode>() {
            @Override
            public ISchemaNode load(Route key) throws ExecutionException, StreamReadException, DatabindException,
                    IOException, LoadException, URISyntaxException {
                // TODO
                ISchemaNode ISchemaNode = createSchema(key, retrievalManager.retrieve(key));
                logger.info("successful loading schema into cache with currenct canonical uri - "
                        + key.getCanonical().getUri());
                return ISchemaNode;
            }

        });
    }

    private ISchemaNode createSchema(Route route, JsonNode source) throws LoadException {
        ISchemaNode targetNode;

        if (source.isMissingNode()) {
            targetNode = new MissingSchemaNode(this, route);
            return targetNode;
        }

        if (source.has("$id")) {
            try {
                String lastCanonical = route.getCanonical().getUri().toString();
                route.setCanonical(ReferenceFactory.create(new URI(source.at("/$id").asText())));
                logger.info("canonical change by embedded in content uri: {"
                        + "\tfrom - " + lastCanonical
                        + "\tto - " + route.getCanonical().getUri()
                        + "}");
                if (preloadedSchemas.containsKey(route)) {
                    return preloadedSchemas.get(route);
                }

                ISchemaNode alredyExistingSchema = cache.getIfPresent(route);
                if (alredyExistingSchema != null)
                    return alredyExistingSchema;
            } catch (URISyntaxException e) {
                // TODO
                throw new URIException("");
            }
        }

        if (source.has("allOf")) {
            // TODO do after writing class AllOfSchemaNode
            targetNode = null;
            // targetNode = new AllOfSchemaNode(this, routeToSchema);
        } else {
            targetNode = new SchemaNode(this, route, source, false);
        }

        countCreatedSchemas++;
        return targetNode;
    }

    public int getCountCreatedSchemas() {
        return countCreatedSchemas;
    }
}
