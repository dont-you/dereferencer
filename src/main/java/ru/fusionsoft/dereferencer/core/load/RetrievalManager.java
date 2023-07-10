package ru.fusionsoft.dereferencer.core.load;

import java.io.IOException;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.RetrievingException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;

public class RetrievalManager {
    private final ObjectMapper jsonMapper;
    private final ObjectMapper yamlMapper;
    private ILoaderFactory loaderFactory;
    private Logger logger;

    public RetrievalManager(ILoaderFactory loaderFactory, Logger logger) {
        this.loaderFactory = loaderFactory;
        this.logger = logger;
        this.jsonMapper = new ObjectMapper();
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    public JsonNode retrieve(Route route)
            throws LoadException {
        Reference canonical = route.getCanonical();
        SourceLoader sourceLoader = loaderFactory.getLoader(canonical.getAbsolute());
        SupportedSourceTypes sourceType = sourceLoader.getSourceType(canonical);
        JsonNode result;
        try {
            if (sourceType.isYaml()) {
                Object obj = yamlMapper.readValue(sourceLoader.getSource(canonical), Object.class);
                result = jsonMapper.readTree(jsonMapper.writeValueAsString(obj));
            } else if (sourceType.isJson()) {
                result = jsonMapper.readTree(sourceLoader.getSource(canonical));
            } else {
                throw new RetrievingException(
                        "source type of resource by uri - " + canonical.getUri() + " is not supported");
            }

        } catch (IOException e) {
            throw new UnknownException(
                    "unknown exception caused while getting source with msg - " + e.getMessage());
        }

        logger.info("successful fetch schema from uri - " + canonical.getUri());
        return result;
    }

    public RetrievalManager setLoaderFactory(ILoaderFactory loaderFactory) {
        this.loaderFactory = loaderFactory;
        return this;
    }

    public RetrievalManager setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

}
