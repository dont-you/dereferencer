package ru.fusionsoft.dereferencer.core.load.urn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.load.urn.TagURI;
import ru.fusionsoft.dereferencer.core.load.urn.URNResolver;

import java.net.URI;
import java.util.Map;
import java.util.TreeMap;

public class TagURIResolver extends URNResolver {
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final Map<TagURI, URI> tags;

    public TagURIResolver() {
        tags = new TreeMap<>();
    }
    @Override
    public void updatePool(URI uri){
        try {
            URI uriToOrigins = uri.resolve(".origins.yaml");
            JsonNode jsonNode = yamlMapper.readTree(load(uriToOrigins));
            jsonNode.fields().forEachRemaining(tagEntity -> tagEntity.getValue().fields().forEachRemaining(tag -> {
                tags.put(new TagURI(tagEntity.getKey(), tag.getKey()), makeLocator(tag.getKey(), tag.getValue().asText(), uriToOrigins));
            }));
        } catch (Exception e) {
            // TODO LOG
        }
    }

    private URI makeLocator(String tag, String locator, URI baseURI){
        return tag.endsWith("*") ? baseURI.resolve(locator.concat("/*")) : baseURI.resolve(locator);
    }

    @Override
    public URI resolve(URI urn) throws DereferenceException {
        TagURI processedTag = TagURI.parse(URI.create(urn.getSchemeSpecificPart()));
        TagURI searchedTagURI = tags.keySet().stream()
                .filter(tagURI -> tagURI.isSup(processedTag))
                .min(TagURI::compareTo)
                .orElse(null);

        if (searchedTagURI == null)
            throw new DereferenceException("could not resolve urn " + urn);

        return TagURI.resolve(tags.get(searchedTagURI), processedTag.getSubPart(searchedTagURI));
    }
}
