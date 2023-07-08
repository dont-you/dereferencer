package ru.fusionsoft.dereferencer.core.routing;

import java.net.URI;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.routing.ref.ReferenceFactory;

public class RouteManager {
    private Reference defaultBaseReference;
    private final Set<Route> cache;
    private Logger logger;

    public RouteManager(URI defaultBaseUri, Set<Route> preloadedRoutes, Logger logger) throws URIException {
        cache = new TreeSet<>();
        setDefaultBaseUri(defaultBaseUri).setPreloadedRoutes(preloadedRoutes).setLogger(logger);
        logger.info("Successful init RouteManager with " + cache.size() + " preloaded routes");
    }

    public Route getRoute(Reference retrievalReference) throws URIException {
        Route target = new Route(ReferenceFactory.create(defaultBaseReference, retrievalReference));
        Iterator<Route> iter = cache.iterator();

        while(iter.hasNext()){
            Route intendedExistingRoute = iter.next();
            if(intendedExistingRoute.equals(target))
                return intendedExistingRoute;
        }

        cache.add(target);
        return target;
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
        cache.addAll(preloadedRoutes);
        return this;
    }

    public RouteManager setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }
}
