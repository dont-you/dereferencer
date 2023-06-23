package ru.fusionsoft.dereferencer.core.routing.ref;

import java.net.URI;

import ru.fusionsoft.dereferencer.Dereferencer;

public enum ReferenceType {
    LOCAL,
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

    public static boolean isLocalReference(URI uri) {
        return uri.getPath().equals("") && uri.getFragment() != null;
    }
}
