package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;

public class SourceTypeResolver {
    public static ObjectMapper jsonMapper = new ObjectMapper();
    public static ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public static JsonNode readJsonFrom(InputStream inputStream, String mimetype) throws IOException {
        if (FileType.resolveSourceTypeByMimeType(mimetype) == FileType.YAML) {
            return yamlMapper.readTree(inputStream);
        } else {
            return jsonMapper.readTree(inputStream);
        }
    }

    private enum FileType {
        JSON,
        YAML,
        NOT_IMPLEMENTED;

        private static final Tika tika = new Tika();

        public static FileType resolveSourceTypeByMimeType(String mimyType) {
            if (mimyType.contains("json") || mimyType.contains("schema+json")
                    || mimyType.contains("schema-instance+json"))
                return JSON;
            else if (mimyType.contains("x-yaml") || mimyType.contains("yaml"))
                return YAML;
            else
                return NOT_IMPLEMENTED;
        }

    }
}