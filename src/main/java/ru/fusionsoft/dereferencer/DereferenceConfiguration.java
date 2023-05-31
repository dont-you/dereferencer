package ru.fusionsoft.dereferencer;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ru.fusionsoft.dereferencer.core.ref.Reference;
import ru.fusionsoft.dereferencer.core.schema.SchemaNode;

public class DereferenceConfiguration {

    private ObjectMapper jsonMapper;
    private ObjectMapper yamlMapper;
    private Logger logger;
    private DereferenceFlag dereferenceFlags[];
    private int cashSize;
    private Map<Reference, SchemaNode> preloadedSchemas;
    private URI defaultBaseUri;
    private String gitHubToken;
    private String gitLabToken;

    private DereferenceConfiguration() {
    }

    public static class DereferenceConfigurationBuilder {
        private DereferenceConfiguration cfg;

        private DereferenceConfigurationBuilder() {
            cfg = new DereferenceConfiguration();
            setJsonMapper(new ObjectMapper()).setYamlMapper(new ObjectMapper(new YAMLFactory()))
                    .setLogger(Logger.getGlobal())
                    .setDereferenceFlags(new DereferenceFlag[] {}).setCashSize(512)
                    .setPreloadedSchemas(new HashMap<>()).setGitHubToken(null).setGitLabToken(null)
                    .setDefaultBaseUri(Paths.get("./").toAbsolutePath().toUri());
        };

        public DereferenceConfigurationBuilder setDereferenceFlags(DereferenceFlag[] dereferenceFlags) {
            cfg.setDereferenceFlags(dereferenceFlags);
            return this;
        }

        public DereferenceConfigurationBuilder setCashSize(int cashSize) {
            cfg.setCashSize(cashSize);
            return this;
        }

        public DereferenceConfigurationBuilder setPreloadedSchemas(Map<Reference, SchemaNode> preloadedSchemas) {
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

        public DereferenceConfigurationBuilder setJsonMapper(ObjectMapper mapper) {
            cfg.setJsonMapper(mapper);
            return this;
        }

        public DereferenceConfigurationBuilder setYamlMapper(ObjectMapper mapper) {
            cfg.setYamlMapper(mapper);
            return this;
        }

        public DereferenceConfigurationBuilder setDefaultBaseUri(URI defaultBaseUri) {
            cfg.setDefaultBaseUri(defaultBaseUri);
            ;
            return this;
        }

        public DereferenceConfiguration build() {
            return cfg;
        }

    }

    public static DereferenceConfigurationBuilder builder() {
        return new DereferenceConfigurationBuilder();
    }

    public DereferenceFlag[] getDereferenceFlags() {
        return dereferenceFlags;
    }

    public void setDereferenceFlags(DereferenceFlag[] dereferenceFlags) {
        this.dereferenceFlags = dereferenceFlags;
    }

    public int getCashSize() {
        return cashSize;
    }

    public void setCashSize(int cashSize) {
        this.cashSize = cashSize;
    }

    public Map<Reference, SchemaNode> getPreloadedSchemas() {
        return preloadedSchemas;
    }

    public void setPreloadedSchemas(Map<Reference, SchemaNode> preloadedSchemas) {
        this.preloadedSchemas = preloadedSchemas;
    }

    public String getGitHubToken() {
        return gitHubToken;
    }

    public void setGitHubToken(String gitHubToken) {
        this.gitHubToken = gitHubToken;
    }

    public String getGitLabToken() {
        return gitLabToken;
    }

    public void setGitLabToken(String gitLabToken) {
        this.gitLabToken = gitLabToken;
    }

    public ObjectMapper getJsonMapper() {
        return jsonMapper;
    }

    public ObjectMapper getYamlMapper() {
        return yamlMapper;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setJsonMapper(ObjectMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public void setYamlMapper(ObjectMapper yamlMapper) {
        this.yamlMapper = yamlMapper;
    }

    public URI getDefaultBaseUri() {
        return defaultBaseUri;
    }

    public void setDefaultBaseUri(URI defaultBaseUri) {
        this.defaultBaseUri = defaultBaseUri;
    }
}
