package ru.fusionsoft.dereferencer.core.load;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;

public class RetrievalManager{
    private String gitHubToken;
    private String gitLabToken;

    public RetrievalManager(String gitHubToken, String gitLabToken){
        this.gitHubToken = gitHubToken;
        this.gitLabToken = gitLabToken;
    }

    public JsonNode retrieveFromReference(Reference reference){
        // TODO
        return null;
    }
    // TODO
}
