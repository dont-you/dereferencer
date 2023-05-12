package ru.fusionsoft.dereferencer.core.reference.factories;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.stream.Collectors;

import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.reference.Reference;
import ru.fusionsoft.dereferencer.exception.ReferenceException;
import ru.fusionsoft.dereferencer.core.reference.impl.internal.LocalReference;
import ru.fusionsoft.dereferencer.core.reference.impl.internal.RemoteReference;
import ru.fusionsoft.dereferencer.core.reference.impl.external.GitHubReference;
import ru.fusionsoft.dereferencer.core.reference.impl.external.URLReference;
import ru.fusionsoft.dereferencer.enums.ReferenceType;

public class ReferenceFactory {
    public static Reference create(URI uri) throws ReferenceException {
        URI normalizedUri = uri.normalize();

        if (ReferenceType.isGitHubReference(normalizedUri))
            return makeGitHubReference(normalizedUri);
        else if (ReferenceType.isURLReference(normalizedUri))
            return new URLReference(normalizedUri);
        else if (ReferenceType.isRemoteReference(normalizedUri))
            return new RemoteReference(normalizedUri);
        else
            throw new ReferenceException("failed to recognize reference - " + normalizedUri);
    }

    public static Reference create(String uri) throws ReferenceException {
        try {
            URI createdUri = new URI(uri);
            return create(createdUri);
        } catch (URISyntaxException e) {
            throw new ReferenceException("error making reference by uri - " + uri);
        }
    }

    public static Reference createRelative(Reference relativeReference, String targetPath) throws ReferenceException {
        try {
            URI uri = new URI(targetPath);
            if (ReferenceType.isLocalReference(uri)) {
                return new LocalReference(relativeReference, uri.getFragment());
            } else if (uri.isAbsolute()) {
                return create(uri);
            }
            URI relativeUri = relativeReference.getUri();
            String path = relativeUri.getPath();
            path = path.substring(0, path.lastIndexOf("/") + 1) + uri.getPath();

            Reference createdReference = create(new URI(
                    relativeUri.getScheme(), relativeUri.getUserInfo(),
                    relativeUri.getHost(), relativeUri.getPort(),
                    path, relativeUri.getQuery(),
                    null));

            if (uri.getFragment() != null && !uri.getFragment().equals("")) {
                return new LocalReference(createdReference, uri.getFragment());
            }

            return createdReference;
        } catch (URISyntaxException e) {
            throw new ReferenceException("failed to create relative reference with message: " + e.getMessage());
        }
    }

    public static GitHubReference makeGitHubReference(URI uri) throws ReferenceException {
        String[] uriPath = uri.getPath().split("/");
        String resultPath = "/repos/" + uriPath[1] + "/" + uriPath[2] + "/contents/"
                + String.join("/", Arrays.stream(uriPath).map(e -> {
                    return e;
                }).collect(Collectors.toList()).subList(5, uriPath.length));
        URI resultUri;
        try {
            resultUri = new URI(
                    uri.getScheme(), uri.getUserInfo(),
                    Dereferencer.getProperties().getProperty("refs.hostname.api-github"),
                    uri.getPort(), resultPath,
                    "ref=" + uriPath[4], uri.getFragment());
        } catch (URISyntaxException e1) {
            throw new ReferenceException("failed to create github reference with uri: " + uri.toString());
        }

        return new GitHubReference(resultUri);
    }
}
