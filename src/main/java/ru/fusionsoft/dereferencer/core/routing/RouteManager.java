package ru.fusionsoft.dereferencer.core.routing;

import java.net.URI;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.routing.ref.ReferenceFactory;

public class RouteManager {
    private Reference defaultBaseReference;
    private final Set<Route> cache;

    public RouteManager(URI defaultBaseUri, Set<Route> preloadedRoutes) throws URIException {
        cache = new TreeSet<>();
        setDefaultBaseUri(defaultBaseUri).setPreloadedRoutes(preloadedRoutes);
    }

    public Route getRoute(Reference retrievalReference) throws URIException {
        Route target = new Route(ReferenceFactory.create(defaultBaseReference, retrievalReference));
        Iterator<Route> iter = cache.iterator();

        while (iter.hasNext()) {
            Route intendedExistingRoute = iter.next();
            if (intendedExistingRoute.equals(target))
                return intendedExistingRoute;
        }

        cache.add(target);
        return target;
    }

    public RouteManager setDefaultBaseUri(URI uri) throws URIException {
        this.defaultBaseReference = ReferenceFactory.create(uri);
        return this;
    }

    public RouteManager setPreloadedRoutes(Set<Route> preloadedRoutes) {
        cache.addAll(preloadedRoutes);
        return this;
    }
}
