package ru.fusionsoft.dereferencer.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public interface SourceLoader {
    boolean canLoad(URL url);
    InputStream loadSource(URL url) throws URISyntaxException, IOException;
    SourceType getSourceType(URL url) throws URISyntaxException, IOException;
    enum SourceType{
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
            if (extension.contains("json") || extension.contains("schema+json") || extension.contains("schema-instance+json"))
                return JSON;
            else if (extension.contains("x-yaml") || extension.contains("yaml"))
                return YAML;
            else
                return NOT_IMPLEMENTED;
        }
    }
}
