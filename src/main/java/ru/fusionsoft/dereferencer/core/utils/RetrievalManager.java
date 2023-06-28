package ru.fusionsoft.dereferencer.core.utils;

import java.io.IOException;

import javax.xml.transform.Source;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.utils.load.SourceLoader;

public class RetrievalManager {
    private ObjectMapper jsonMapper;
    private ObjectMapper yamlMapper;
    private String gitHubToken;
    private String gitLabToken;

    public RetrievalManager(ObjectMapper jsonMapper, ObjectMapper yamlMapper, String gitHubToken, String gitLabToken) {
        setJsonMapper(jsonMapper).setYamlMapper(yamlMapper).setGitHubToken(gitHubToken).setGitLabToken(gitLabToken);
    }

    public JsonNode retrieve(Route route) throws StreamReadException, DatabindException, IOException, URIException {
        // TODO
        Reference canonical = route.getCanonical();
        SourceLoader sourceLoader = canonical.getSourceLoader();

        if (sourceLoader.getSourceType().isYaml()) {
            Object obj = yamlMapper.readValue(sourceLoader.getSource(), Object.class);
            return jsonMapper.readTree(jsonMapper.writeValueAsString(obj));
        } else if (sourceLoader.getSourceType().isJson()) {
            return jsonMapper.readTree(sourceLoader.getSource());
        } else {
            // TODO
            throw new URIException("");
        }
    }

    public RetrievalManager setJsonMapper(ObjectMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
        return this;
    }

    public RetrievalManager setYamlMapper(ObjectMapper yamlMapper) {
        this.yamlMapper = yamlMapper;
        return this;
    }

    public RetrievalManager setGitHubToken(String gitHubToken) {
        this.gitHubToken = gitHubToken;
        return this;
    }

    public RetrievalManager setGitLabToken(String gitLabToken) {
        this.gitLabToken = gitLabToken;
        return this;
    }
}
