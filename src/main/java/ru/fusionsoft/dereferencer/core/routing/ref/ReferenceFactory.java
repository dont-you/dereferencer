package ru.fusionsoft.dereferencer.core.routing.ref;

import java.net.URI;
import java.net.URISyntaxException;

import ru.fusionsoft.dereferencer.core.exceptions.URIException;

public class ReferenceFactory {
    public static Reference create(String uri) throws URIException {
        try {
            return new Reference(new URI(uri));
        } catch (URISyntaxException e) {
            throw new URIException("uri " + uri + " contains errors");
        }
    }

    public static Reference create(URI uri) throws URIException {
        return new Reference(uri);
    }

    public static Reference create(URI absolute, JsonPtr jsonPtr) throws URIException {
        return new Reference(absolute, jsonPtr);
    }

    public static Reference create(Reference contextReference, URI relative) throws URIException {
        return create(resolveUri(contextReference.getAbsolute(), relative));
    }

    public static Reference create(Reference contextReference, Reference relativeReference) throws URIException {
        URI resolvedUri = resolveUri(contextReference.getAbsolute(), relativeReference.getAbsolute());
        return create(resolvedUri, relativeReference.getJsonPtr());
    }

    public static URI resolveUri(URI contextUri, URI relative) throws URIException {
        try {
            if (!contextUri.getPath().startsWith("/"))
                return addSlashToUriPath(contextUri).resolve(relative);
            else
                return contextUri.resolve(relative);
        } catch (URISyntaxException e) {
            throw new URIException("can't resolve - " + relative + ", relative to - " + contextUri);
        }
    }

    public static Reference create(Reference reference, JsonPtr ptr) throws URIException {
        return create(reference.getAbsolute(), ptr);
    }

    public static Reference create(Reference reference, String pathToRelativeReference, String relativeReference) throws URIException {
        URI uri;
        URI relativeUri;
        try {
            relativeUri = new URI(relativeReference);
        } catch (URISyntaxException e1) {
            throw new URIException("uri " + relativeReference + " contains errors");
        }

        String pathFromUri = relativeUri.getPath();
        String[] parts = pathFromUri.split("/");

        try{
            Integer.parseInt(parts[0]);
            return create(reference, resolveJsonPtr(pathFromUri, parts));
        } catch (NumberFormatException e) {
            return create(reference, relativeUri);
        }

    }

    private static JsonPtr resolveJsonPtr(String pathToValue, String pathFromValue[]){
        String currentPath = pathToValue;
        for(String key: pathFromValue){
            try{
                int upLevelTo = Integer.parseInt(key);
                for(int i=0 ; i < upLevelTo ; i++){
                    currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
                }
            } catch (NumberFormatException e) {
                currentPath += "/" + key;
            }
        }

        return new JsonPtr(currentPath);
    }



    private static URI addSlashToUriPath(URI uri) throws URISyntaxException {
        return new URI(uri.getScheme(),
                uri.getUserInfo(), uri.getHost(), uri.getPort(),
                "/" + uri.getPath(), uri.getQuery(),
                uri.getFragment());
    }
}
