package ru.fusionsoft.dereferencer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;
import ru.fusionsoft.dereferencer.core.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.routing.ref.ReferenceFactory;
import ru.fusionsoft.dereferencer.core.SchemaLoader;
import ru.fusionsoft.dereferencer.core.schema.ISchemaNode;
import ru.fusionsoft.dereferencer.utils.ClientFactory;
import ru.fusionsoft.dereferencer.utils.SourceClient;
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
    private ClientFactory clientFactory;

    public Dereferencer() throws LoadException {
        DereferenceConfiguration cfg = DereferenceConfiguration.builder().build();
        schemaLoader = new SchemaLoader(cfg);
        clientFactory = (ClientFactory) cfg.getLoaderFactory();

    }

    public Dereferencer(DereferenceConfiguration cfg) throws LoadException {
        schemaLoader = new SchemaLoader(cfg);
        clientFactory = (ClientFactory) cfg.getLoaderFactory();
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
        URNResolver urnResolver = clientFactory.getUrnResolver();
        urnResolver.addToCache(getUrnCache(uri));
        return executeDereference(schemaLoader, uri);
    }

    public JsonNode anonymousDereference(JsonNode node) throws LoadException {
        return executeAnonymousDereference(schemaLoader, node);
    }

    public void setDereferenceConfiguration(DereferenceConfiguration cfg) throws LoadException {
        schemaLoader.setDereferenceConfiguration(cfg);
    }

    private Map<URN, URI> getUrnCache(URI uri) throws LoadException {
        Map<URN, URI> cache = new HashMap<>();
        String somePattern = "$";
        try {
            URI uriToDirectory = makeUriWithNewPath(uri, uri.getPath().substring(0, uri.getPath().lastIndexOf("/")));
            SourceClient client = clientFactory.getClient(uri);
            List<String> urnMapsFiles = client.directoryList(uriToDirectory).stream()
                    .filter(e -> e.startsWith(somePattern)).toList();
            SourceLoader loader = clientFactory.getLoader(uriToDirectory);
            ObjectMapper jsonMapper = new ObjectMapper();

            for (String fileName : urnMapsFiles) {
                URI uriToFile = makeUriWithNewPath(uriToDirectory, fileName);
                JsonNode map = jsonMapper.readTree(loader.getSource(uriToFile));
                Iterator<Entry<String, JsonNode>> fields = map.fields();
                while (fields.hasNext()) {
                    Entry<String, JsonNode> pair = fields.next();
                    cache.put(URN.parse(new URI(pair.getKey())), new URI(pair.getValue().asText()));
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
