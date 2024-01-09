package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
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
    public static ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
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

    public File get(@NotNull URI uri) throws DereferenceException {
        BaseURI fileBaseURI = new BaseURI(defaultBaseURI, uri);
        File lookingFile = cache.get(fileBaseURI);

        if (lookingFile != null)
            return lookingFile;

        if (fileBaseURI.getCanonical().getScheme().equals("urn")) {
            URL url = urnPool.getLocator(fileBaseURI.getCanonical());

            if(url==null)
                throw new DereferenceException("could not resolve urn: " + fileBaseURI.getCanonical());

            fileBaseURI.updateCanonical(url);
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

    public File get(@NotNull JsonNode sourceJson) throws DereferenceException {
        try {
            URI idFieldURI = getIdField(sourceJson);

            if (idFieldURI == null)
                throw new DereferenceException("anonymous schema should have field '$id'");

            BaseURI fileBaseURI = new BaseURI(defaultBaseURI, idFieldURI);
            File lookingFile = cache.get(fileBaseURI);

            if (lookingFile != null)
                return lookingFile;

            return makeFile(fileBaseURI, sourceJson);

        } catch (URISyntaxException e) {
            throw new DereferenceException(
                    "could not parse id field from file anonymous schema: ");
        }
    }

    private @Nullable URI getIdField(JsonNode source) throws URISyntaxException {
        if (source.has("$id")) {
            return new URI(source.get("$id").asText());
        }

        return null;
    }

    private File makeFile(BaseURI baseURI, JsonNode sourceJson) throws DereferenceException {
        URL updateURNCacheURl = urnPool.updateCache(baseURI.getCanonical(), loaderFactory);
        if(updateURNCacheURl!=null)
            // TODO add logger
            System.out.println("urn pool cache updated by " + updateURNCacheURl);

        File lookingFile = fileFactory.makeFile(this, baseURI.getCanonical(), sourceJson);
        cache.put(baseURI, lookingFile);
        lookingFile.dereference();

        return lookingFile;
    }

    private JsonNode loadSource(URL url) throws DereferenceException {
        try {
            SourceLoader sourceLoader = loaderFactory.getSourceLoader(url);
            SourceLoader.SourceType sourceType = sourceLoader.getSourceType(url);
            InputStream stream = sourceLoader.loadSource(url);

            if (sourceType.isYaml()) {
                Object obj = yamlMapper.readValue(stream, Object.class);
                return jsonMapper.readTree(jsonMapper.writeValueAsString(obj));
            } else if (sourceType.isJson()) {
                return jsonMapper.readTree(stream);
            } else {
                throw new DereferenceException("source type of file with url - " + url + " is not implemented");
            }
        } catch (URISyntaxException | IOException e) {
            throw new DereferenceException("exception while getting file from url - " + url, e);
        }
    }
}
