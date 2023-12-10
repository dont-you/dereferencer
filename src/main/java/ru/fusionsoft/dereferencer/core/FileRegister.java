package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

public class FileRegister {
    public static ObjectMapper jsonMapper = new ObjectMapper();
    public static  ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final URNPool urnPool;
    private final LoaderFactory loaderFactory;
    private final FileFactory fileFactory;
    private final URI defaultBaseURI;
    private final Map<BaseURI, File> cache;

    public FileRegister(URNPool urnPool, LoaderFactory loaderFactory, FileFactory fileFactory, URI defaultBaseURI) {
        this.urnPool = urnPool;
        this.loaderFactory = loaderFactory;
        this.fileFactory = fileFactory;
        this.defaultBaseURI = defaultBaseURI;
        cache = new TreeMap<>();
    }

    public File get(URI uri) throws DereferenceException {
        BaseURI fileBaseURI = new BaseURI(defaultBaseURI, uri);
        File lookingFile = cache.get(fileBaseURI);

        if (lookingFile != null)
            return lookingFile;

        if (fileBaseURI.getCanonical().getScheme().equals("urn")) {
// TODO
//            fileBaseURI.updateCanonical(urnPool.getLocator(fileBaseURI.getCanonical()));

            lookingFile = cache.get(fileBaseURI);

            if (lookingFile != null)
                return lookingFile;
        }

        try {
            JsonNode sourceJson = loadSource(fileBaseURI.getCanonical().toURL());
            URI idFieldURI = getIdField(sourceJson);

            if (idFieldURI != null)
                fileBaseURI.updateCanonical(idFieldURI);

            lookingFile = cache.get(fileBaseURI);

            if (lookingFile != null)
                return lookingFile;

            return makeFile(fileBaseURI, sourceJson);
        } catch (URISyntaxException e) {
            throw new DereferenceException(
                    "could not parse id field from file with uri: " + fileBaseURI.getCanonical());
        } catch (MalformedURLException e) {
            throw new DereferenceException("could not parse url from uri " + fileBaseURI.getCanonical());
        }

    }

    public File get(JsonNode sourceJson) throws DereferenceException{
        try {
            URI idFieldURI = getIdField(sourceJson);

            if (idFieldURI == null)
                throw new DereferenceException("anonymous schema should have field '$id'");

            BaseURI fileBaseURI = new BaseURI(idFieldURI, idFieldURI);
            File lookingFile = cache.get(fileBaseURI);

            if (lookingFile != null)
                return lookingFile;

            return makeFile(fileBaseURI, sourceJson);

        } catch (URISyntaxException e) {
            throw new DereferenceException(
                    "could not parse id field from file anonymous schema: ");
        }
    }

    private URI getIdField(JsonNode source) throws URISyntaxException {
        if (source.has("$id")) {
            return new URI(source.get("$id").asText());
        }

        return null;
    }

    private File makeFile(BaseURI baseURI, JsonNode sourceJson) throws DereferenceException {
// TODO
//        urnPool.updateCache(baseURI.getCanonical(), loaderFactory);
        File lookingFile = fileFactory.makeFile(this, baseURI.getCanonical(), sourceJson);
        cache.put(baseURI, lookingFile);
        lookingFile.dereference();

        return lookingFile;
    }

    private JsonNode loadSource(URL url) throws DereferenceException {
        SourceLoader sourceLoader = loaderFactory.getSourceLoader(url);
        try {
            return makeJsonFromInputStream(sourceLoader.loadSource(url), sourceLoader.getSourceType(url));
        } catch (URISyntaxException | IOException e) {
            throw new DereferenceException("cant load source from url " + url);
        }
    }
    private JsonNode makeJsonFromInputStream(InputStream stream, SourceLoader.SourceType sourceType) throws DereferenceException{
        try{
            if (sourceType.isYaml()) {
                Object obj = yamlMapper.readValue(stream, Object.class);
                return jsonMapper.readTree(jsonMapper.writeValueAsString(obj));
            } else if (sourceType.isJson()) {
                return jsonMapper.readTree(stream);
            }
            throw new DereferenceException("");
        } catch (IOException e) {
            throw new DereferenceException("");
        }
    }

}
