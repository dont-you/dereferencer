package ru.fusionsoft.dereferencer.core.schema;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import ru.fusionsoft.dereferencer.DereferenceConfiguration;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.exceptions.UnresolvableSchemaException;
import ru.fusionsoft.dereferencer.core.utils.RetrievalManager;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.routing.RouteManager;
import ru.fusionsoft.dereferencer.core.routing.ref.JsonPtr;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;

public class SchemaLoader {
    private RouteManager routeManager;
    private RetrievalManager retrievalManager;
    private Logger logger;
    private Map<Route, ISchemaNode> preloadedSchemas;
    private LoadingCache<Route, ISchemaNode> cache;

    public SchemaLoader(DereferenceConfiguration derefCfg) throws URIException {
        routeManager = new RouteManager(derefCfg.getDefaultBaseUri(), preloadedSchemas.keySet(), logger);
        retrievalManager = new RetrievalManager(derefCfg.getJsonMapper(), derefCfg.getYamlMapper(),
                derefCfg.getGitHubToken(),
                derefCfg.getGitLabToken());
        logger = derefCfg.getLogger();
        preloadedSchemas = derefCfg.getPreloadedSchemas();
        setCache(derefCfg.getCashSize());
    }

    public ISchemaNode get(Reference reference) throws ExecutionException, DereferenceException {
        if (reference.isContainsFragment()) {
            URI absolute = reference.getAbsolute();
            JsonPtr jsonPtr = reference.getJsonPtr();
            return getFromCache(routeManager.getRoute(absolute)).getSchemaNodeByJsonPointer(jsonPtr);
        } else {
            return getFromCache(routeManager.getRoute(reference));
        }
    }

    public ISchemaNode get(Reference reference, JsonNode node)
            throws UnresolvableSchemaException, URIException, ExecutionException {
        // ISchemaNode targetNode = new SuperSchema(); // TODO delete

        // if(node.isMissingNode()){
        // targetNode = null; // new MissingSchema(.... TODO
        // return targetNode;
        // }

        // make Route-> call get(route,node)

        // cache.put(targetNode.getSchemaRoute(), targetNode);
        // targetNode.resolve();
        // return targetNode;
        return null;
    }

    public ISchemaNode get(JsonNode node) throws UnresolvableSchemaException {
        // Route route = routeManager.createAnonRoute();
        // ISchemaNode targetNode = new SuperSchema(); // TODO delete;
        // cache.put(route, targetNode);
        // targetNode.resolve();
        // return targetNode;
        return null;
    }

    public ISchemaNode get(Route route, JsonNode jsonNode) {
        // TODO
        // if(node.has("allOf")){
        // targetNode = null; // = new AllOfSchema(.... TODO
        // } else if(node.has("$id")){
        // targetNode = null; // = new SuperSchema(.... TODO
        // } else {
        // targetNode = null; // = new SubSchema(.... TODO
        // }
        return null;
    }

    private ISchemaNode getFromCache(Route schemaRoute) throws ExecutionException, DereferenceException {
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

    public void setDereferenceConfiguraion(DereferenceConfiguration cfg) throws URIException {
        routeManager.setDefaultBaseUri(cfg.getDefaultBaseUri()).setPreloadedRoutes(cfg.getPreloadedSchemas().keySet())
                .setLogger(logger);
        retrievalManager.setJsonMapper(cfg.getJsonMapper()).setYamlMapper(cfg.getYamlMapper())
                .setGitHubToken(cfg.getGitHubToken()).setGitLabToken(cfg.getGitLabToken());
        logger = cfg.getLogger();
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
            public ISchemaNode load(Route key) throws UnresolvableSchemaException, ExecutionException, URIException,
                    StreamReadException, DatabindException, IOException {
                // TODO
                JsonNode node = retrievalManager.retrieve(key);
                if (node.has("$id")) {
                    // TODO ... add duplicate
                }
                ISchemaNode ISchemaNode = get(key, retrievalManager.retrieve(key));
                logger.info("successful loading schema into cache with - '{retrieval uri: \""
                        + key.getRetrievalUri() + "\", uri embedded in content :"
                        + key.getEmbeddedInContentUri() + "'");
                return ISchemaNode;
            }

        });
    }
}