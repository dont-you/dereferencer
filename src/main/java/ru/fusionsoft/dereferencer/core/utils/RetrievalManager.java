package ru.fusionsoft.dereferencer.core.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.routing.ref.ReferenceType;
import ru.fusionsoft.dereferencer.core.utils.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.utils.load.SupportedSourceTypes;
import ru.fusionsoft.dereferencer.core.utils.load.impl.FileLoader;
import ru.fusionsoft.dereferencer.core.utils.load.impl.GitHubLoader;
import ru.fusionsoft.dereferencer.core.utils.load.impl.URLLoader;

public class RetrievalManager {
    private ObjectMapper jsonMapper;
    private ObjectMapper yamlMapper;
    private FileLoader fileLoader = new FileLoader();
    private GitHubLoader gitHubLoader = new GitHubLoader();
    private URLLoader urlLoader = new URLLoader();

    public RetrievalManager(ObjectMapper jsonMapper, ObjectMapper yamlMapper, String gitHubToken, String gitLabToken) {
        setJsonMapper(jsonMapper).setYamlMapper(yamlMapper).setGitHubToken(gitHubToken).setGitLabToken(gitLabToken);
    }

    public JsonNode retrieve(Route route) throws StreamReadException, DatabindException, IOException, DereferenceException {
        // TODO
        Reference canonical = route.getCanonical();
        SourceLoader sourceLoader = getSourceLoaderByRefType(canonical.getReferenceType());
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

    public SourceLoader getSourceLoaderByRefType(ReferenceType type){
        if(type.isGitHubReference())
            return gitHubLoader;
        else if(type.isRemoteReference())
            return fileLoader;
        else
            return urlLoader;
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
        this.gitHubLoader.setToken(gitHubToken);
        return this;
    }

    public RetrievalManager setGitLabToken(String gitLabToken) {
        // this.gitLabToken = gitLabToken;
        return this;
    }
}
