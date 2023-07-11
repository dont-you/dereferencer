package ru.fusionsoft.dereferencer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.RetrievingException;
import ru.fusionsoft.dereferencer.core.load.ILoaderFactory;
import ru.fusionsoft.dereferencer.core.load.LoaderFactory;
import ru.fusionsoft.dereferencer.core.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.load.impl.URLLoader;
import ru.fusionsoft.dereferencer.utils.impl.FileClient;
import ru.fusionsoft.dereferencer.utils.impl.GitHubClient;
import ru.fusionsoft.dereferencer.utils.urn.URN;
import ru.fusionsoft.dereferencer.utils.urn.URNResolver;

public class ClientFactory implements ILoaderFactory{
    private final FileClient fileClient;
    private final GitHubClient gitHubClient;
    private final URLLoader urlLoader;
    private final URNResolver urnResolver;
    public static final Properties HOSTNAMES;

    static {
        HOSTNAMES = new Properties();
        InputStream inputStream = LoaderFactory.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            HOSTNAMES.setProperty("refs.hostname.github", properties.getProperty("refs.hostname.github"));
            HOSTNAMES.setProperty("refs.hostname.api-github", properties.getProperty("refs.hostname.api-github"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientFactory(Tokens tokens) {
        fileClient = new FileClient();
        gitHubClient = new GitHubClient();
        urlLoader = new URLLoader();
        urnResolver = new URNResolver();

        gitHubClient.setToken(tokens.getGitHubToken());
    }

    @Override
    public SourceLoader getLoader(URI uri) throws LoadException {
        if (isUrnReference(uri))
            return getLoader(urnResolver.getLocator(new URN(uri)));
        if (isGitHubReference(uri))
            return gitHubClient;
        else if (isFileSystemReference(uri))
            return fileClient;
        else if (isURLReference(uri))
            return urlLoader;
        else
            throw new RetrievingException("source loader for resource with uri " + uri + " is not implemented");
    }

    public SourceClient getClient(URI uri) throws LoadException {
        if (isGitHubReference(uri))
            return gitHubClient;
        else if (isFileSystemReference(uri))
            return fileClient;
        else if (isUrnReference(uri))
            return getClient(urnResolver.getLocator(new URN(uri)));
        else
            throw new RetrievingException("source client for resource with uri " + uri + " is not implemented");
    }

    public void setTokens(Tokens tokens) {
        gitHubClient.setToken(tokens.getGitHubToken());
    }

    private boolean isGitHubReference(URI uri) {
        return uri.getHost() != null
                && uri.getHost().equals(HOSTNAMES.getProperty("refs.hostname.github"));
    }

    private boolean isURLReference(URI uri) {
        return uri.getHost() != null;
    }

    private boolean isFileSystemReference(URI uri) {
        return uri.getHost() == null && !uri.getPath().equals("");
    }

    private boolean isUrnReference(URI uri) {
        return uri.getScheme()!=null && uri.getScheme().equals("urn");
    }

    public URNResolver getUrnResolver(){
        return urnResolver;
    }
}
