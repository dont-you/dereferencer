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
import ru.fusionsoft.dereferencer.core.load.impl.FileLoader;
import ru.fusionsoft.dereferencer.core.load.impl.URLLoader;
import ru.fusionsoft.dereferencer.utils.impl.GitHubSourceLoader;
import ru.fusionsoft.dereferencer.utils.urn.URN;
import ru.fusionsoft.dereferencer.utils.urn.URNResolver;

public class DereferenceLoaderFactory implements ILoaderFactory {
    private final FileLoader fileLoader;
    private final GitHubSourceLoader gitHubSourceLoader;
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

    public DereferenceLoaderFactory(Tokens tokens) {
        fileLoader = new FileLoader();
        gitHubSourceLoader = new GitHubSourceLoader();
        urlLoader = new URLLoader();
        urnResolver = new URNResolver();

        gitHubSourceLoader.setToken(tokens.getGitHubToken());
    }

    @Override
    public SourceLoader getLoader(URI uri) throws LoadException {
        if (isUrnReference(uri))
            return getLoader(urnResolver.getLocator(URN.parse(uri)));
        if (isGitHubReference(uri))
            return gitHubSourceLoader;
        else if (isFileSystemReference(uri))
            return fileLoader;
        else if (isURLReference(uri))
            return urlLoader;
        else
            throw new RetrievingException("source loader for resource with uri " + uri + " is not implemented");
    }

    public void setTokens(Tokens tokens) {
        gitHubSourceLoader.setToken(tokens.getGitHubToken());
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
        return uri.getScheme() != null && uri.getScheme().equals("urn");
    }

    public URNResolver getUrnResolver() {
        return urnResolver;
    }
}
