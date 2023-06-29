package ru.fusionsoft.dereferencer.core.routing.ref;

import java.net.URI;

import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;

public enum ReferenceType {
    REMOTE,
    URL,
    URL_GITHUB;

    public static boolean isGitHubReference(URI uri) {
        return uri.getHost() != null
                && uri.getHost().equals(Dereferencer.PROPERTIES.getProperty("refs.hostname.github"));
    }

    public static boolean isURLReference(URI uri) {
        return uri.getHost() != null;
    }

    public static boolean isRemoteReference(URI uri) {
        return uri.getHost() == null && !uri.getPath().equals("");
    }

    public boolean isGitHubReference() {
        return this.equals(URL_GITHUB);
    }

    public boolean isURLReference() {
        return this.equals(URL);
    }

    public boolean isRemoteReference() {
        return this.equals(REMOTE);
    }

    public static ReferenceType getReferenceTypeByUri(URI uri) throws URIException{
        if (ReferenceType.isGitHubReference(uri))
            return URL_GITHUB;
        else if (ReferenceType.isRemoteReference(uri))
            return REMOTE;
        else if (ReferenceType.isURLReference(uri))
            return URL;
        else
            throw new URIException("can't determine reference type by uri - " + uri);
    }
}
