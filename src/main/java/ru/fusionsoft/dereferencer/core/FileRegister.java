package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;

import org.jetbrains.annotations.NotNull;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;

public class FileRegister {
    private final URNPool urnPool;
    private final LoaderFactory loaderFactory;
    private final FileFactory fileFactory;
    private final TypeAdapter typeAdapter;
    private final URI defaultBaseURI;
    private final Map<URI, File> cache;

    public FileRegister(URNPool urnPool, LoaderFactory loaderFactory, FileFactory fileFactory, TypeAdapter typeAdapter, URI defaultBaseURI) {
        this.urnPool = urnPool;
        this.loaderFactory = loaderFactory;
        this.fileFactory = fileFactory;
        this.typeAdapter = typeAdapter;
        this.defaultBaseURI = defaultBaseURI;
        cache = new TreeMap<>();
    }

    public File get(@NotNull URI uri) throws DereferenceException {
        uri = defaultBaseURI.resolve(uri);
        File lookingFile = cache.get(uri);

        if (lookingFile != null)
            return lookingFile;

        if (uri.getScheme().equals("urn")) {
            uri = urnPool.getLocator(uri);

            if (uri == null)
                throw new DereferenceException("could not resolve urn");

            lookingFile = cache.get(uri);

            if (lookingFile != null)
                return lookingFile;
        }

        JsonNode sourceJson = loadSource(uri);
        lookingFile = makeFile(uri, sourceJson);

        if (cache.containsKey(lookingFile.getBaseURI()))
            return cache.get(lookingFile.getBaseURI());

        cache.put(uri, lookingFile);
        lookingFile.resolve();
        return lookingFile;
    }

    private File makeFile(URI uri, JsonNode sourceJson) throws DereferenceException {
        URI updateURNCacheURl = urnPool.updateCache(uri, loaderFactory);
        if (updateURNCacheURl != null)
            // TODO add logger
            System.out.println("urn pool cache updated by " + updateURNCacheURl);

        return fileFactory.makeFile(this, uri, sourceJson);
    }

    private JsonNode loadSource(URI uri) throws DereferenceException {
        try {
            SourceLoader sourceLoader = loaderFactory.getSourceLoader(uri);
            String mimeType = sourceLoader.getMimeType(uri);
            InputStream stream = sourceLoader.loadSource(uri);
            return typeAdapter.readJsonFrom(stream, mimeType);
        } catch (URISyntaxException | IOException e) {
            throw new DereferenceException("exception while getting file from url - " + uri, e);
        }
    }
}
