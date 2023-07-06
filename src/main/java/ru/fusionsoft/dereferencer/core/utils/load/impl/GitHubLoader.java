package ru.fusionsoft.dereferencer.core.utils.load.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.utils.load.LoaderFactory;
import ru.fusionsoft.dereferencer.core.utils.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.utils.load.SupportedSourceTypes;

public class GitHubLoader implements SourceLoader {

    private String token = null;

    @Override
    public InputStream getSource(Reference ref) throws LoadException {
        try {
            URI apiUri = transformUriToApiUri(ref.getAbsolute());
            HttpURLConnection conn;

            conn = (HttpURLConnection) apiUri.toURL().openConnection();
            conn.setRequestProperty("Accept", "application/vnd.github.v3.raw");

            if (token != null)
                conn.setRequestProperty("Authorization", "token " + token);

            return conn.getInputStream();
        } catch (IOException e) {
            throw new UnknownException(
                    "unknown exception caused while getting source with msg - " + e.getMessage());
        }
    }

    @Override
    public SupportedSourceTypes getSourceType(Reference ref) throws LoadException {
        Path path = Paths.get(ref.getAbsolute());
        try {
            return SupportedSourceTypes.resolveSourceTypeByMimeType(Files.probeContentType(path));
        } catch (IOException e) {
            throw new UnknownException(
                    "unknown exception caused while getting mime type with msg - " + e.getMessage());
        }
    }

    private URI transformUriToApiUri(URI uri) throws URIException {
        String apiGithubHostName = LoaderFactory.HOSTNAMES.getProperty("refs.hostname.api-github");

        if (uri.getHost().equals(apiGithubHostName))
            return uri;

        String[] uriPath = uri.getPath().split("/");
        String resultPath = "/repos/" + uriPath[1] + "/" + uriPath[2] + "/contents/"
                + String.join("/", Arrays.stream(uriPath).collect(Collectors.toList()).subList(5, uriPath.length));
        URI resultUri;

        try {
            resultUri = new URI(
                    uri.getScheme(), uri.getUserInfo(),
                    apiGithubHostName,
                    uri.getPort(), resultPath,
                    "ref=" + uriPath[4], uri.getFragment());
        } catch (URISyntaxException e) {
            throw new URIException(resultPath);
        }

        return resultUri;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
