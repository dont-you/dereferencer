package ru.fusionsoft.dereferencer.core.routing.ref;

import java.net.URI;
import java.net.URISyntaxException;

import ru.fusionsoft.dereferencer.core.exceptions.URIException;

public class ReferenceFactory {
    public static Reference create(URI uri) throws URIException {
        return new Reference(uri);
    }

    public static Reference create(Reference contextReference, URI relative) throws URIException {
        return create(contextReference.getAbsolute(), relative);
    }

    public static Reference create(URI contextUri, URI relative) throws URIException {
        try {
            if (!contextUri.getPath().startsWith("/"))
                return create(addSlashToUriPath(contextUri).resolve(relative));
            else
                return create(contextUri.resolve(relative));
        } catch (URISyntaxException e) {
            throw new URIException("can't resolve - " + relative + ", relative to - " + contextUri);
        }
    }

    public static Reference create(Reference reference, JsonPtr ptr) throws URIException {
        // TODO
        return null;
    }

    private static URI addSlashToUriPath(URI uri) throws URISyntaxException {
        return new URI(uri.getScheme(),
                uri.getUserInfo(), uri.getHost(), uri.getPort(),
                "/" + uri.getPath(), uri.getQuery(),
                uri.getFragment());
    }
}
