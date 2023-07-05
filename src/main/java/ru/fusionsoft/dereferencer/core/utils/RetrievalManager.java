package ru.fusionsoft.dereferencer.core.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ru.fusionsoft.dereferencer.core.Tokens;
import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.utils.load.LoaderFactory;
import ru.fusionsoft.dereferencer.core.utils.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.utils.load.SupportedSourceTypes;

public class RetrievalManager {
    private ObjectMapper jsonMapper;
    private ObjectMapper yamlMapper;
    private LoaderFactory loaderFactory;

    public RetrievalManager(Tokens tokens) {
        this.loaderFactory = new LoaderFactory(tokens);
        this.jsonMapper = new ObjectMapper();
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    public JsonNode retrieve(Route route)
            throws StreamReadException, DatabindException, IOException, LoadException {
        Reference canonical = route.getCanonical();
        SourceLoader sourceLoader = loaderFactory.getLoader(canonical.getAbsolute());
        SupportedSourceTypes sourceType = sourceLoader.getSourceType(canonical);

        if (sourceType.isYaml()) {
            Object obj = yamlMapper.readValue(sourceLoader.getSource(canonical), Object.class);
            return jsonMapper.readTree(jsonMapper.writeValueAsString(obj));
        } else if (sourceType.isJson()) {
            return jsonMapper.readTree(sourceLoader.getSource(canonical));
        } else {
            // TODO
            throw new URIException("");
        }
    }

    public void setTokens(Tokens tokens){
        loaderFactory.setTokens(tokens);
    }
}
