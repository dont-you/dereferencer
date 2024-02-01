package ru.fusionsoft.dereferencer.core.impl.urn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.jetbrains.annotations.Nullable;
import ru.fusionsoft.dereferencer.core.LoaderFactory;
import ru.fusionsoft.dereferencer.core.URNPool;

import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

public class TagURIPool implements URNPool {
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final Map<TagURI, URI> tags;

    public TagURIPool() {
        tags = new TreeMap<>();
    }

    @Override
    public @Nullable URI getLocator(URI urn) {
        TagURI processedTag = TagURI.parse(URI.create(urn.getSchemeSpecificPart()));
        TagURI searchedTagURI = tags.keySet().stream()
                .filter(tagURI -> tagURI.isSup(processedTag))
                .min(TagURI::compareTo)
                .orElse(null);

        if (searchedTagURI == null)
            return null;

        return TagURI.resolve(tags.get(searchedTagURI), processedTag.getSubPart(searchedTagURI));
    }

    @Override
    public @Nullable URI updateCache(URI uri, LoaderFactory loaderFactory) {
        try {
            URL urlToOrigins = uri.resolve(".origins.yaml").toURL();
            JsonNode jsonNode = yamlMapper.readTree(loaderFactory.getSourceLoader(urlToOrigins).loadSource(urlToOrigins));
            jsonNode.fields().forEachRemaining(tagEntity -> tagEntity.getValue().fields().forEachRemaining(tag -> {
                URI locator = tag.getKey().endsWith("*") ? uri.resolve(tag.getValue().asText().concat("/*")) : uri.resolve(tag.getValue().asText());
                tags.put(new TagURI(tagEntity.getKey(), tag.getKey()), locator);
            }));
            return urlToOrigins.toURI();
        } catch (Exception e) {
            return null;
        }
    }
}
