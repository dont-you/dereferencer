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
import ru.fusionsoft.dereferencer.utils.impl.GitLabSourceLoader;
import ru.fusionsoft.dereferencer.utils.urn.URN;
import ru.fusionsoft.dereferencer.utils.urn.URNResolver;

public class DereferenceLoaderFactory implements ILoaderFactory {
    Tokens tokens;
    URNResolver urnResolver;
    public static final Properties HOSTNAMES;

    static {
        HOSTNAMES = new Properties();
        InputStream inputStream = LoaderFactory.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            HOSTNAMES.setProperty("refs.hostname.github", properties.getProperty("refs.hostname.github"));
            HOSTNAMES.setProperty("refs.hostname.api-github", properties.getProperty("refs.hostname.api-github"));
            HOSTNAMES.setProperty("refs.hostname.gitlab", properties.getProperty("refs.hostname.gitlab"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DereferenceLoaderFactory(Tokens tokens) {
        this.tokens = tokens;
        urnResolver = new URNResolver();
    }

    @Override
    public SourceLoader getLoader(URI uri) throws LoadException {
        if (isUrnReference(uri))
            return getLoader(urnResolver.getLocator(URN.parse(uri)));
        else if (isGitHubReference(uri))
            return new GitHubSourceLoader(tokens.getGitHubToken(),uri);
        else if (isGitLabRegerence(uri))
            return new GitLabSourceLoader(tokens.getGitLabToken(),uri);
        else if (isFileSystemReference(uri))
            return new FileLoader(uri);
        else if (isURLReference(uri))
            return new URLLoader(uri);
        else
            throw new RetrievingException("source loader for resource with uri " + uri + " is not implemented");
    }

    public void setTokens(Tokens tokens) {
        this.tokens.setGitHubToken(tokens.getGitHubToken());
        this.tokens.setGitLabToken(tokens.getGitLabToken());
    }

    private boolean isGitHubReference(URI uri) {
        return uri.getHost() != null
                && uri.getHost().equals(HOSTNAMES.getProperty("refs.hostname.github"));
    }

    private boolean isGitLabRegerence(URI uri) {
        return uri.getHost() != null
                && uri.getHost().equals(HOSTNAMES.getProperty("refs.hostname.gitlab"));
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
