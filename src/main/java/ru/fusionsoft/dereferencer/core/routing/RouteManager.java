package ru.fusionsoft.dereferencer.core.routing;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.routing.Route.AvailableToFetch;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.routing.ref.ReferenceFactory;

public class RouteManager {
    private Reference defaultBaseReference;
    private LoadingCache<AvailableToFetch, Route> cache;
    private Logger logger;

    public RouteManager(URI defaultBaseUri, Set<Route> preloadedRoutes, Logger logger) throws URIException {
        setDefaultBaseUri(defaultBaseUri).setPreloadedRoutes(preloadedRoutes).setLogger(logger);
        logger.info("Successful init ReferenceFactory with " + cache.size() + " preloaded references");
    }

    public Route getRoute(URI retrievalUri) throws ExecutionException, URIException {
        Route target = new Route(defaultBaseReference.getAbsolute());
        target.setRetrievalUri(retrievalUri);
        return cache.get(target.getAvailableToFetch());
    }

    public Route getRoute(Reference retrievalReference) throws ExecutionException, URIException {
        Route target = new Route(defaultBaseReference.getAbsolute());
        target.setRetrievalReference(retrievalReference);
        return cache.get(target.getAvailableToFetch());
    }

    public Route createAnonRoute() {
        // TODO
        return null;
    }

    public RouteManager setDefaultBaseUri(URI uri) throws URIException {
        this.defaultBaseReference = ReferenceFactory.create(uri);
        return this;
    }

    public RouteManager setPreloadedRoutes(Set<Route> preloadedRoutes) {
        Map<AvailableToFetch, Route> loadToCache = preloadedRoutes.stream()
                .collect(Collectors.toMap(k -> k.getAvailableToFetch(), v -> v));

        cache = CacheBuilder.newBuilder().build(new CacheLoader<AvailableToFetch, Route>() {
            @Override
            public Route load(AvailableToFetch key) throws URIException {
                logger.info("successful loading route into cache with current canonical - "
                        + key.getRelatedRoute().getCanonical());
                return key.getRelatedRoute();
            }

        });

        cache.putAll(loadToCache);
        return this;
    }

    public RouteManager setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }
}
