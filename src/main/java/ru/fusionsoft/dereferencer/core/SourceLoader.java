package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public interface SourceLoader {

    public static final ObjectMapper jsonMapper = new ObjectMapper();
    public static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public boolean canLoad(URI uri);
    public JsonNode loadSource(URI uri);

    private static JsonNode makeJsonFromInputStream(InputStream stream, SourceType sourceType) throws DereferenceException{
        try{
            if (sourceType.isYaml()) {
                Object obj = yamlMapper.readValue(stream, Object.class);
                return jsonMapper.readTree(jsonMapper.writeValueAsString(obj));
            } else if (sourceType.isJson()) {
                return jsonMapper.readTree(stream);
            }
            throw new DereferenceException("");
        } catch (IOException e) {
            throw new DereferenceException("");
        }
    }

    public static enum SourceType{
        JSON,
        YAML,
        NOT_IMPLEMENTED;

        public boolean isYaml() {
            return this.equals(YAML);
        }

        public boolean isJson() {
            return this.equals(JSON);
        }

        public static SourceType resolveSourceTypeByMimeType(String extension) {
            if (extension.contains("application/json"))
                return JSON;
            else if (extension.contains("application/x-yaml") || extension.contains("application/yaml")
                     || extension.contains("text/x-yaml"))
                return YAML;
            else
                return NOT_IMPLEMENTED;
        }

    }
}
