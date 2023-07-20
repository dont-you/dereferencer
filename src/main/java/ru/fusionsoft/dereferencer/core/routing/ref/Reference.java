package ru.fusionsoft.dereferencer.core.routing.ref;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import ru.fusionsoft.dereferencer.core.exceptions.URIException;

public class Reference implements Comparable<Reference> {
    private final URI uri;
    private final URI absolute;
    private final JsonPtr jsonPtr;

    Reference(URI uri) throws URIException {
        try {
            this.uri = uri.normalize();
            this.absolute = new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);

            if (uri.getFragment() != null)
                jsonPtr = new JsonPtr(uri.getFragment());
            else
                jsonPtr = null;
        } catch (URISyntaxException e) {
            throw new URIException("can't parse uri - " + uri);
        }
    }

    Reference(URI absolute, JsonPtr jsonPtr) throws URIException {
        try {
            if (jsonPtr != null)
                this.uri = new URI(absolute.normalize().toASCIIString() +
                        "#" + (jsonPtr.isResolved() ? jsonPtr.getResolved() : jsonPtr.getPlainName()));
            else
                this.uri = absolute;
            this.absolute = absolute;
            this.jsonPtr = jsonPtr;
        } catch (URISyntaxException e) {
            throw new URIException("can't parse uri - " + absolute);
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

    @Override
    public int compareTo(Reference ref) {
        return uri.compareTo(ref.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }
}
