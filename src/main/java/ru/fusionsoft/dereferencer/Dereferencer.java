package ru.fusionsoft.dereferencer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;
import ru.fusionsoft.dereferencer.core.routing.Route;
import ru.fusionsoft.dereferencer.core.schema.SchemaNode;
import ru.fusionsoft.dereferencer.core.SchemaLoader;
import ru.fusionsoft.dereferencer.utils.DereferenceLoaderFactory;
import ru.fusionsoft.dereferencer.utils.urn.URN;
import ru.fusionsoft.dereferencer.utils.urn.URNResolver;

public class Dereferencer {

    private SchemaLoader schemaLoader;
    private DereferenceLoaderFactory loaderFactory;
    private URI defaultBaseUri;
    private String pathToSavedDirectory;

    public Dereferencer() throws LoadException {
        DereferenceConfiguration cfg = DereferenceConfiguration.builder().build();
        schemaLoader = new SchemaLoader(cfg);
        loaderFactory = (DereferenceLoaderFactory) cfg.getLoaderFactory();
        defaultBaseUri = cfg.getDefaultBaseUri();
        pathToSavedDirectory = cfg.getPathToSavedDirectory();
    }

    public Dereferencer(DereferenceConfiguration cfg) throws LoadException {
        schemaLoader = new SchemaLoader(cfg);
        loaderFactory = (DereferenceLoaderFactory) cfg.getLoaderFactory();
        defaultBaseUri = cfg.getDefaultBaseUri();
        pathToSavedDirectory = cfg.getPathToSavedDirectory();
    }

    public static JsonNode deref(URI uri) throws LoadException {
        return deref(uri, DereferenceConfiguration.builder().build());
    }

    public static JsonNode deref(URI uri, DereferenceConfiguration cfg) throws LoadException {
        SchemaLoader schemaLoader = new SchemaLoader(cfg);
        return schemaLoader.get(uri).asJson();
    }

    public static JsonNode anonymousDeref(JsonNode node) throws LoadException {
        return anonymousDeref(node, DereferenceConfiguration.builder().build());
    }

    public static JsonNode anonymousDeref(JsonNode node, DereferenceConfiguration cfg)
            throws LoadException {
        SchemaLoader schemaLoader = new SchemaLoader(cfg);
        return schemaLoader.get(node).asJson();
    }

    public JsonNode dereference(URI uri) throws LoadException {
        URNResolver urnResolver = loaderFactory.getUrnResolver();
        urnResolver.addToCache(getUrnCache(new URI[] { uri }));
        return schemaLoader.get(uri).asJson();
    }

    public void dereference(URI[] uris) throws LoadException {
        URNResolver urnResolver = loaderFactory.getUrnResolver();
        urnResolver.addToCache(getUrnCache(uris));
        ObjectMapper jsonMapper = new ObjectMapper();

        for (URI uri : uris) {
            try {
                jsonMapper.writeValue(
                        Paths.get(pathToSavedDirectory + uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1))
                                .toFile(),
                        schemaLoader.get(uri).asJson());
            } catch (IOException e) {
                throw new UnknownException("could not write result by path - " + pathToSavedDirectory);
            }
        }
    }

    public JsonNode anonymousDereference(JsonNode node) throws LoadException {
        return schemaLoader.get(node).asJson();
    }

    public void setDereferenceConfiguration(DereferenceConfiguration cfg) throws LoadException {
        schemaLoader.setDereferenceConfiguration(cfg);
    }

    public static Map<Route, SchemaNode> makePreloadedSchemas(URI uri) throws LoadException {
        DereferenceConfiguration cfg = DereferenceConfiguration.builder().build();
        SchemaLoader loader = new SchemaLoader(cfg);
        loader.get(uri);
        return loader.getCache();
    }

    private Map<URN, URI> getUrnCache(URI uris[]) throws LoadException {
        Map<URN, URI> cache = new HashMap<>();

        cache.putAll(getTagUriCache(uris));

        return cache;
    }

    private Map<URN, URI> getTagUriCache(URI uris[]) throws LoadException {
        Map<URN, URI> cache = new HashMap<>();
        for (URI uri : uris) {
            try {
                URI uriToOrigins = defaultBaseUri.resolve(uri)
                        .resolve(uri.getPath().substring(0, uri.getPath().lastIndexOf("/") + 1) + ".origins.yaml");
                ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory()), jsonMapper = new ObjectMapper();
                boolean hasOrigins = true;
                JsonNode origins = null;

                try {
                    origins = jsonMapper.readTree(
                            jsonMapper.writeValueAsString(
                                    yamlMapper.readValue(loaderFactory.getLoader(uriToOrigins).getSource(),
                                            Object.class)));
                } catch (LoadException e) {
                    hasOrigins = false;
                }

                if (hasOrigins) {
                    Iterator<Entry<String, JsonNode>> tagEntityes = origins.fields();
                    while (tagEntityes.hasNext()) {
                        Entry<String, JsonNode> taggingEntity = tagEntityes.next();
                        Iterator<Entry<String, JsonNode>> tags = taggingEntity.getValue().fields();

                        while (tags.hasNext()) {
                            Entry<String, JsonNode> tag = tags.next();
                            try {
                                cache.put(
                                        URN.parse(new URI(
                                                String.format("urn:tag:%s:%s", taggingEntity.getKey(), tag.getKey()))),
                                        new URI(tag.getValue().asText()));
                            } catch (URISyntaxException e) {
                                throw new URIException(String.format(
                                        "could not create urn tag with:\n\ttaggingEntity - %s\n\ttag - %s",
                                        taggingEntity.getKey(), tag.getKey()));
                            }
                        }
                    }
                }

            } catch (IOException e) {
                throw new UnknownException("unknow exception during getting uri cache with msg - " + e.getMessage());
            }
        }
        return cache;
    }
}
