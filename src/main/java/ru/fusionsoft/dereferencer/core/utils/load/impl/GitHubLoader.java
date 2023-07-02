package ru.fusionsoft.dereferencer.core.utils.load.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.stream.Collectors;

import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.utils.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.utils.load.SupportedSourceTypes;

public class GitHubLoader implements SourceLoader {

    private String token = null;

    @Override
    public InputStream getSource(Reference ref) throws DereferenceException {
        try {
            URI apiUri = transformUriToApiUri(ref.getAbsolute());
            HttpURLConnection conn;

            conn = (HttpURLConnection) apiUri.toURL().openConnection();
            conn.setRequestProperty("Accept", "application/vnd.github.v3.raw");

            if (token != null)
                conn.setRequestProperty("Authorization", "token " + token);

            return conn.getInputStream();
        } catch (IOException e) {
            // TODO
            throw new URIException("");
        }
    }

    @Override
    public SupportedSourceTypes getSourceType(Reference ref) throws DereferenceException {
        String path = ref.getAbsolute().getPath().toString();
        return SupportedSourceTypes.resolveSourceType(path.substring(path.lastIndexOf(".") + 1));
    }

    private URI transformUriToApiUri(URI uri) throws URIException {
        String apiGithubHostName = Dereferencer.PROPERTIES.getProperty("refs.hostname.api-github");

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
