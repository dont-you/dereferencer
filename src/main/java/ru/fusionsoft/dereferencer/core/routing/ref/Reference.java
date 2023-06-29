package ru.fusionsoft.dereferencer.core.routing.ref;

import java.net.URI;
import java.net.URISyntaxException;

import ru.fusionsoft.dereferencer.core.exceptions.URIException;

public class Reference {
    private final ReferenceType referenceType;
    private final URI uri;
    private final URI absolute;
    private final JsonPtr jsonPtr;

    Reference(URI uri, ReferenceType referenceType) throws URIException {
        try {
            this.uri = uri.normalize();
            this.absolute = new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);

            if (uri.getFragment() != null)
                jsonPtr = new JsonPtr(uri.getFragment());
            else
                jsonPtr = null;

            this.referenceType = referenceType;
        } catch (URISyntaxException e) {
            throw new URIException("can't parse uri - " + uri);
        }
    }

    public URI getUri() {
        return uri;
    }

    public URI getAbsolute() {
        return absolute;
    }

    public JsonPtr getJsonPtr() {
        return jsonPtr;
    }

    public boolean isContainsFragment() {
        return jsonPtr != null;
    }

    public ReferenceType getReferenceType(){
        return referenceType;
    }
}
