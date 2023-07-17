package ru.fusionsoft.dereferencer;

import java.net.URI;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import ru.fusionsoft.dereferencer.core.LoadConfiguration;
import ru.fusionsoft.dereferencer.core.LoadingFlag;
import ru.fusionsoft.dereferencer.core.Tokens;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.schema.SchemaNode;

public class DereferenceConfiguration implements LoadConfiguration {

    private Logger logger;
    private LoadingFlag[] loadingFlags;
    private int cashSize;
    private Map<Route, SchemaNode> preloadedSchemas;
    private URI defaultBaseUri;
    private Tokens tokens;

    private DereferenceConfiguration() {
    }

    public static class DereferenceConfigurationBuilder {
        private final DereferenceConfiguration cfg;

        private DereferenceConfigurationBuilder() {
            cfg = new DereferenceConfiguration();
            setLogger(Logger.getGlobal())
                    .setLoadingFlags(new LoadingFlag[] {}).setCashSize(-1)
                    .setPreloadedSchemas(new HashMap<>()).setTokens(new Tokens())
                    .setDefaultBaseUri(Paths.get("./").toAbsolutePath().toUri());
        }

        public DereferenceConfigurationBuilder setLoadingFlags(LoadingFlag[] loadingFlags) {
            cfg.setLoadingFlags(loadingFlags);
            return this;
        }

        public DereferenceConfigurationBuilder setCashSize(int cashSize) {
            cfg.setCashSize(cashSize);
            return this;
        }

        public DereferenceConfigurationBuilder setPreloadedSchemas(Map<Route, SchemaNode> preloadedSchemas) {
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

        public DereferenceConfiguration build() {
            return cfg;
        }

    }

    public static DereferenceConfigurationBuilder builder() {
        return new DereferenceConfigurationBuilder();
    }

    @Override
    public LoadingFlag[] getLoadingFlags() {
        return loadingFlags;
    }

    @Override
    public int getCashSize() {
        return cashSize;
    }

    @Override
    public Map<Route, SchemaNode> getPreloadedSchemas() {
        return preloadedSchemas;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public URI getDefaultBaseUri() {
        return defaultBaseUri;
    }

    @Override
    public Tokens getTokens() {
        return tokens;
    }

    public void setLoadingFlags(LoadingFlag[] loadingFlags) {
        this.loadingFlags = loadingFlags;
    }

    public void setCashSize(int cashSize) {
        this.cashSize = cashSize;
    }

    public void setPreloadedSchemas(Map<Route, SchemaNode> preloadedSchemas) {
        this.preloadedSchemas = preloadedSchemas;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setDefaultBaseUri(URI defaultBaseUri) {
        this.defaultBaseUri = defaultBaseUri;
    }

    public void setTokens(Tokens tokens) {
        this.tokens = tokens;
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
}
