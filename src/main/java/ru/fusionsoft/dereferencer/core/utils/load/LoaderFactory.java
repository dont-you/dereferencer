package ru.fusionsoft.dereferencer.core.utils.load;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.Tokens;
import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.RetrievingException;
import ru.fusionsoft.dereferencer.core.utils.load.impl.FileLoader;
import ru.fusionsoft.dereferencer.core.utils.load.impl.GitHubLoader;
import ru.fusionsoft.dereferencer.core.utils.load.impl.URLLoader;

public class LoaderFactory {
    private FileLoader fileLoader;
    private GitHubLoader gitHubLoader;
    private URLLoader urlLoader;
    public static final Properties HOSTNAMES;

    static {
        HOSTNAMES = new Properties();
        InputStream inputStream = Dereferencer.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            HOSTNAMES.setProperty("refs.hostname.github", properties.getProperty("refs.hostname.github"));
            HOSTNAMES.setProperty("refs.hostname.api-github", properties.getProperty("refs.hostname.api-github"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LoaderFactory(Tokens tokens) {
        fileLoader = new FileLoader();
        gitHubLoader = new GitHubLoader();
        urlLoader = new URLLoader();

        gitHubLoader.setToken(tokens.getGitHubToken());
    }

    public SourceLoader getLoader(URI uri) throws LoadException {
        if (isGitHubReference(uri))
            return gitHubLoader;
        else if (isFileSystemReference(uri))
            return fileLoader;
        else if (isURLReference(uri))
            return urlLoader;
        else
            throw new RetrievingException("source loader for resource with uri " + uri + " is not implemented");
    }

    public void setTokens(Tokens tokens) {
        gitHubLoader.setToken(tokens.getGitHubToken());
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

}
