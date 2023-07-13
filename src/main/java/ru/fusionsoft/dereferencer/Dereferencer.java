package ru.fusionsoft.dereferencer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import ru.fusionsoft.dereferencer.core.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.routing.ref.ReferenceFactory;
import ru.fusionsoft.dereferencer.core.SchemaLoader;
import ru.fusionsoft.dereferencer.core.schema.ISchemaNode;
import ru.fusionsoft.dereferencer.utils.DereferenceLoaderFactory;
import ru.fusionsoft.dereferencer.utils.urn.URN;
import ru.fusionsoft.dereferencer.utils.urn.URNResolver;

// TODO LIST
//
// - Dereferencer class
// ---- feat: dereference uri array
//
// - TagUri class
// ---- parse tag uri
// ---- make uri by tag uri and uri from cache

public class Dereferencer {

    private SchemaLoader schemaLoader;
    private DereferenceLoaderFactory loaderFactory;

    public Dereferencer() throws LoadException {
        DereferenceConfiguration cfg = DereferenceConfiguration.builder().build();
        schemaLoader = new SchemaLoader(cfg);
        loaderFactory = (DereferenceLoaderFactory) cfg.getLoaderFactory();
    }

    public Dereferencer(DereferenceConfiguration cfg) throws LoadException {
        schemaLoader = new SchemaLoader(cfg);
        loaderFactory = (DereferenceLoaderFactory) cfg.getLoaderFactory();
    }

    public static JsonNode deref(URI uri) throws LoadException {
        return deref(uri, DereferenceConfiguration.builder().build());
    }

    public static JsonNode deref(URI uri, DereferenceConfiguration cfg) throws LoadException {
        SchemaLoader schemaLoader = new SchemaLoader(cfg);
        return executeDereference(schemaLoader, uri);
    }

    public static JsonNode anonymousDeref(JsonNode node) throws LoadException {
        return anonymousDeref(node, DereferenceConfiguration.builder().build());
    }

    public static JsonNode anonymousDeref(JsonNode node, DereferenceConfiguration cfg)
            throws LoadException {
        SchemaLoader schemaLoader = new SchemaLoader(cfg);
        return executeAnonymousDereference(schemaLoader, node);
    }

    public JsonNode dereference(URI uri) throws LoadException {
        URNResolver urnResolver = loaderFactory.getUrnResolver();
        urnResolver.addToCache(getUrnCache(new URI[]{uri}));
        return executeDereference(schemaLoader, uri);
    }

    public JsonNode anonymousDereference(JsonNode node) throws LoadException {
        return executeAnonymousDereference(schemaLoader, node);
    }

    public void setDereferenceConfiguration(DereferenceConfiguration cfg) throws LoadException {
        schemaLoader.setDereferenceConfiguration(cfg);
    }

    private Map<URN, URI> getUrnCache(URI uris[]) throws LoadException {
        Map<URN, URI> cache = new HashMap<>();

        cache.putAll(getTagUriCache(uris));

        return cache;
    }

    private Map<URN, URI> getTagUriCache(URI uris[]) throws LoadException {
        Map<URN, URI> cache = new HashMap<>();
        try {
            for (URI uri : uris) {
                URI uriToOrigins = makeUriWithNewPath(uri,
                        uri.getPath().substring(0, uri.getPath().lastIndexOf("/") + 1) + ".origins.yaml");
                SourceLoader sourceLoader = loaderFactory.getLoader(uriToOrigins);
                ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
                ObjectMapper jsonMapper = new ObjectMapper();

                JsonNode origins = jsonMapper.readTree(
                        jsonMapper.writeValueAsString(
                                yamlMapper.readValue(sourceLoader.getSource(uriToOrigins), Object.class)));

                Iterator<Entry<String, JsonNode>> tagEntityes = origins.fields();
                while(tagEntityes.hasNext()){
                    Entry<String, JsonNode> taggingEntity = tagEntityes.next();
                    Iterator<Entry<String, JsonNode>> tags = taggingEntity.getValue().fields();

                    while(tags.hasNext()){
                        Entry<String, JsonNode> tag = tags.next();
                        cache.put(URN.parse(new URI(String.format("urn:tag:%s:%s", taggingEntity.getKey(), tag.getKey()))),
                                new URI(tag.getValue().asText()));
                    }
                }
            }
        } catch (IOException e) {
            throw new UnknownException(""); // TODO
        } catch (URISyntaxException e) {
            throw new URIException(""); // TODO
        }

        return cache;

    }

    private URI makeUriWithNewPath(URI uri, String path) throws URISyntaxException {
        return new URI(uri.getScheme(),
                uri.getUserInfo(), uri.getHost(), uri.getPort(),
                path, uri.getQuery(),
                uri.getFragment());
    }

    private static JsonNode executeDereference(SchemaLoader loader, URI uri) throws LoadException {
        ISchemaNode resultNode = loader.get(ReferenceFactory.create(uri));
        return resultNode.asJson();
    }

    private static JsonNode executeAnonymousDereference(SchemaLoader loader, JsonNode node)
            throws LoadException {
        ISchemaNode resultNode = loader.get(node);
        return resultNode.asJson();
    }
}
