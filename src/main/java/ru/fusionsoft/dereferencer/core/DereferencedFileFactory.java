package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

public class DereferencedFileFactory {

    protected final ObjectMapper objectMapper;

    public DereferencedFileFactory(){
       objectMapper = new ObjectMapper();
    }
    public final DereferencedFile makeFile(URLConnection urlConnection) throws IOException, URISyntaxException {
        JsonNode source = objectMapper.readTree(urlConnection.getInputStream());
        URI baseURI = establishBaseURI(urlConnection.getURL().toURI(), source);
        return makeInstance(baseURI, source);
    }

    public URI establishBaseURI(URI retrievalURI, JsonNode source){
        if(source.has("$id"))
            return retrievalURI.resolve(source.get("$id").asText());
        else
            return retrievalURI;
    }

    public DereferencedFile makeInstance(URI baseURI, JsonNode source){
        return new FileImpl(baseURI,source);
    }
}
