package ru.fusionsoft.dereferencer.core.routing;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.routing.ref.ReferenceFactory;

public class Route {
    private Reference defaultBaseRef;
    private Reference canonical = null;
    private Reference retrievalRef = null;
    private Reference embeddedInContentRef = null;
    private AvailableToFetch availableToFetch;

    public Route(URI defaultBaseUri) throws URIException {
        availableToFetch = new AvailableToFetch(this);
        setDefaultBaseUri(defaultBaseUri);
    }

    public Reference getCanonical() {
        return canonical;
    }

    public URI getDefaultBaseUri() {
        return defaultBaseRef.getAbsolute();
    }

    public void setDefaultBaseUri(URI defaultBaseUri) throws URIException {
        if (defaultBaseUri == null)
            throw new URIException("default base uri cannot be null");

        this.defaultBaseRef = ReferenceFactory.create(defaultBaseUri);
    }

    public URI getRetrievalUri() {
        return retrievalRef.getUri();
    }

    public void setRetrievalUri(URI retrievalUri) throws URIException {
        if (retrievalUri == null)
            return;

        this.retrievalRef = ReferenceFactory.create(defaultBaseRef.getAbsolute(), retrievalUri);
        availableToFetch.add(retrievalRef);
        canonical = retrievalRef;
    }

    public void setRetrievalReference(Reference retrievalRef) {
        this.retrievalRef = retrievalRef;
        availableToFetch.add(retrievalRef);
        canonical = retrievalRef;
    }

    public URI getEmbeddedInContentUri() {
        return embeddedInContentRef.getUri();
    }

    public void setEmbeddedInContentUri(URI embeddedInContentUri) throws URIException {
        if (embeddedInContentUri == null)
            return;

        URI contextUri = (retrievalRef != null) ? retrievalRef.getAbsolute() : defaultBaseRef.getAbsolute();

        this.embeddedInContentRef = ReferenceFactory.create(contextUri, embeddedInContentUri);
        availableToFetch.add(embeddedInContentRef);
        canonical = embeddedInContentRef;
    }

    public AvailableToFetch getAvailableToFetch() {
        return availableToFetch;
    }

    public Reference resolveRelative(String relative) throws URIException {
        return ReferenceFactory.create(canonical.getAbsolute(), URI.create(relative));
    }

    static class AvailableToFetch implements Cloneable {
        private Set<Reference> duplicates;
        private Route relatedRoute;

        AvailableToFetch(Route relatedRoute) {
            this.relatedRoute = relatedRoute;
            duplicates = new HashSet<>();
        }

        AvailableToFetch add(Reference reference) {
            duplicates.add(reference);
            return this;
        }

        public Route getRelatedRoute() {
            return relatedRoute;
        }

        @Override
        public boolean equals(Object obj) {
            for (Reference reference : ((AvailableToFetch) obj).duplicates) {
                if (duplicates.contains(reference)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return canonical.equals(((Route) obj).getCanonical());
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
