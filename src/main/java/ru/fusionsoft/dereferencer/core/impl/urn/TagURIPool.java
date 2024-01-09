package ru.fusionsoft.dereferencer.core.impl.urn;

import org.jetbrains.annotations.Nullable;
import ru.fusionsoft.dereferencer.core.LoaderFactory;
import ru.fusionsoft.dereferencer.core.URNPool;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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

    public TagURIPool() {
        tags = new TreeMap<>();
    }

    @Override
    public @Nullable URL getLocator(URI urn) {
        TagURI processedTag = TagURI.parse(URI.create(urn.getSchemeSpecificPart()));
        URI locator = null;
        int prefixSizeOfBaseTag = -1;

        for (Entry<TagURI, URI> tagEntry : tags.entrySet()) {
            int currentPrefixSize = tagEntry.getKey().getPrefixSize();
            if (TagURI.isSub(processedTag, tagEntry.getKey()) && prefixSizeOfBaseTag < currentPrefixSize) {
                locator = tagEntry.getValue();
                prefixSizeOfBaseTag = currentPrefixSize;
            }
        }

        try {
            return TagURI.resolve(processedTag, locator).toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            return null;
        }
    }

    @Override
    public @Nullable URL updateCache(URI uri, LoaderFactory loaderFactory) {
        try {
            URL urlToOrigins = uri.resolve(".origins.yaml").toURL();
            JsonNode jsonNode = yamlMapper.readTree(loaderFactory.getSourceLoader(urlToOrigins).loadSource(urlToOrigins));
            tags.putAll(parseOrigins(urlToOrigins.toURI(), jsonNode));
            return urlToOrigins;
        } catch (Exception e) {
            return null;
        }
    }

    private Map<TagURI, URI> parseOrigins(URI baseURI, JsonNode jsonNode) {
        Map<TagURI, URI> parsedTags = new TreeMap<>();
        Iterator<Entry<String, JsonNode>> taggingEntities = jsonNode.fields();

        while (taggingEntities.hasNext()) {
            Entry<String, JsonNode> tagEntity = taggingEntities.next();
            Iterator<Entry<String, JsonNode>> originTags = tagEntity.getValue().fields();

            while (originTags.hasNext()) {
                Entry<String, JsonNode> tag = originTags.next();
                parsedTags.put(new TagURI(tagEntity.getKey(), tag.getKey()), baseURI.resolve(tag.getValue().asText()));
            }
        }
        return parsedTags;
    }
}
