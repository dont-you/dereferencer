package ru.fusionsoft.dereferencer;

import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

import ru.fusionsoft.dereferencer.core.LoadConfiguration;
import ru.fusionsoft.dereferencer.core.LoadingFlag;
import ru.fusionsoft.dereferencer.utils.DereferenceLoaderFactory;
import ru.fusionsoft.dereferencer.utils.Tokens;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.schema.ISchemaNode;

public class DereferenceConfiguration extends LoadConfiguration {
    private Tokens tokens;
    private String pathToSavedDirectory;

    private DereferenceConfiguration() {
    }

    public static class DereferenceConfigurationBuilder {
        private final DereferenceConfiguration cfg;

        private DereferenceConfigurationBuilder() {
            cfg = new DereferenceConfiguration();
            setTokens(new Tokens());
            cfg.loaderFactory = new DereferenceLoaderFactory(cfg.tokens);
            cfg.setPathToSavedDirectory(null);
        }

        public DereferenceConfigurationBuilder setLoadingFlags(LoadingFlag[] loadingFlags) {
            cfg.setLoadingFlags(loadingFlags);
            return this;
        }

        public DereferenceConfigurationBuilder setCashSize(int cashSize) {
            cfg.setCashSize(cashSize);
            return this;
        }

        public DereferenceConfigurationBuilder setPreloadedSchemas(Map<Route, ISchemaNode> preloadedSchemas) {
            cfg.setPreloadedSchemas(preloadedSchemas);
            return this;
        }

        public DereferenceConfigurationBuilder setGitHubToken(String gitHubToken) {
            cfg.setGitHubToken(gitHubToken);
            return this;
        }

        public DereferenceConfigurationBuilder setGitLabToken(String gitLabToken) {
            cfg.setGitLabToken(gitLabToken);
            return this;
        }

        public DereferenceConfigurationBuilder setLogger(Logger logger) {
            cfg.setLogger(logger);
            return this;
        }

        public DereferenceConfigurationBuilder setDefaultBaseUri(URI defaultBaseUri) {
            cfg.setDefaultBaseUri(defaultBaseUri);
            return this;
        }

        public DereferenceConfigurationBuilder setTokens(Tokens tokens) {
            cfg.setTokens(tokens);
            return this;
        }

        public DereferenceConfigurationBuilder setPathToSavedDirectory(String pathToSavedDirectory) {
            cfg.setPathToSavedDirectory(pathToSavedDirectory);
            return this;
        }


        public DereferenceConfiguration build() {
            return cfg;
        }

    }

    public static DereferenceConfigurationBuilder builder() {
        return new DereferenceConfigurationBuilder();
    }

    public Tokens getTokens() {
        return tokens;
    }

    public void setTokens(Tokens tokens) {
        if (this.tokens == null) {
            this.tokens = tokens;
        } else {
            this.tokens.setGitHubToken(tokens.getGitHubToken());
            this.tokens.setGitLabToken(tokens.getGitLabToken());
        }

    }

    public String getGitLabToken() {
        return tokens.getGitLabToken();
    }

    public String getGitHubToken() {
        return tokens.getGitHubToken();
    }

    public void setGitHubToken(String gitHubToken) {
        tokens.setGitHubToken(gitHubToken);
    }

    public void setGitLabToken(String gitLabToken) {
        tokens.setGitLabToken(gitLabToken);
    }

    public String getPathToSavedDirectory() {
        return pathToSavedDirectory;
    }

    public void setPathToSavedDirectory(String pathToSavedDirectory) {
        this.pathToSavedDirectory = pathToSavedDirectory;
    }
}
