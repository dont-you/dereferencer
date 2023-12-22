package ru.fusionsoft.dereferencer.core.impl.urn;

import ru.fusionsoft.dereferencer.core.SourceLoader;
import ru.fusionsoft.dereferencer.core.URNPool;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class TagURIPool implements URNPool {
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final Map<TagURI, URI> tags;

    public TagURIPool(){
        tags = new TreeMap<>();
    }

    @Override
    public URI getLocator(URI urn) throws DereferenceException {
        TagURI processedTag = TagURI.parse(URI.create(urn.getSchemeSpecificPart()));
        URI locator = null;
        int prefixSizeOfBaseTag = -1;

        for(Entry<TagURI, URI> tagEntry: tags.entrySet()){
            int currentPrefixSize = tagEntry.getKey().getPrefixSize();
            if(TagURI.isSub(processedTag ,tagEntry.getKey()) && prefixSizeOfBaseTag < currentPrefixSize){
                locator = tagEntry.getValue();
                prefixSizeOfBaseTag = currentPrefixSize;
            }
        }

        try {
            return TagURI.resolve(processedTag, locator);
        } catch (URISyntaxException e) {
            throw new DereferenceException("could not parse TagUri " + urn);
        }
    }

    @Override
    public void updateCache(URI uri, SourceLoader sourceLoader) {
        try {
            URI uriToOrigins = uri.resolve(".origins.yaml");
            JsonNode jsonNode = yamlMapper.readTree(sourceLoader.loadSource(uriToOrigins.toURL()));
            tags.putAll(parseOrigins(uriToOrigins,jsonNode));
        } catch (IOException | URISyntaxException e) {
            System.err.println("error, while processing .origins.yaml from " + uri);
        }
    }

    private Map<TagURI, URI> parseOrigins(URI baseURI, JsonNode jsonNode){
        Map<TagURI, URI> parsedTags = new TreeMap<>();
        Iterator<Entry<String, JsonNode>> taggingEntities = jsonNode.fields();

        while(taggingEntities.hasNext()){
            Entry<String, JsonNode> tagEntity = taggingEntities.next();
            Iterator<Entry<String, JsonNode>> originTags = tagEntity.getValue().fields();

            while(originTags.hasNext()){
                Entry<String, JsonNode> tag = originTags.next();
                parsedTags.put(new TagURI(tagEntity.getKey(), tag.getKey()), baseURI.resolve(tag.getValue().asText()));
            }
        }
        return parsedTags;
    }
}
